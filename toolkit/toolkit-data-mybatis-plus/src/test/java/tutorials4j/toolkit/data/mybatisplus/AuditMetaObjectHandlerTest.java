package tutorials4j.toolkit.data.mybatisplus;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import java.time.Instant;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tutorials4j.toolkit.data.mybatisplus.component.AuditMetaObjectHandler;

/**
 * {@link AuditMetaObjectHandler} 单元测试：插入/更新时的审计字段自动填充。
 *
 * <p>MyBatis-Plus 的 strict 填充依赖实体上的 {@code @TableField(fill = ...)} 与 已初始化的 {@code TableInfo}，测试中用
 * {@link TableInfoHelper} 手动完成实体元数据初始化。
 *
 * @author YunJiao Yang
 */
class AuditMetaObjectHandlerTest {
  private final AuditMetaObjectHandler handler = new AuditMetaObjectHandler();

  @BeforeAll
  static void initTableInfos() {
    initTableInfo(TestEntity.class);
    initTableInfo(UpdateOnlyEntity.class);
  }

  private static void initTableInfo(Class<?> entityClass) {
    if (TableInfoHelper.getTableInfo(entityClass) != null) {
      return;
    }
    MapperBuilderAssistant assistant = new MapperBuilderAssistant(new MybatisConfiguration(), "");
    assistant.setCurrentNamespace(entityClass.getName());
    TableInfoHelper.initTableInfo(assistant, entityClass);
  }

  @BeforeEach
  void login() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("alice", "credentials"));
  }

  @AfterEach
  void logout() {
    SecurityContextHolder.clearContext();
  }

  private static MetaObject metaObject(Object entity) {
    return SystemMetaObject.forObject(entity);
  }

  @Test
  @DisplayName("insertFill 填充创建人、创建时间与最后修改时间")
  void shouldFillAuditFieldsOnInsert() {
    TestEntity entity = new TestEntity();

    handler.insertFill(metaObject(entity));

    assertThat(entity.getCreatedBy()).isEqualTo("alice");
    assertThat(entity.getCreatedDate()).isNotNull();
    assertThat(entity.getLastModifiedDate()).isNotNull();
  }

  @Test
  @DisplayName("现状记录：insertFill 不填充 lastModifiedBy（仅 updateFill 填充）")
  void shouldNotFillLastModifiedByOnInsert() {
    TestEntity entity = new TestEntity();

    handler.insertFill(metaObject(entity));

    assertThat(entity.getLastModifiedBy()).isNull();
  }

  @Test
  @DisplayName("insertFill 不覆盖已有值（strict 策略只填充 null）")
  void shouldNotOverwriteExistingValuesOnInsert() {
    Instant createdDate = Instant.parse("2024-01-01T00:00:00Z");
    TestEntity entity = new TestEntity();
    entity.setCreatedBy("existing");
    entity.setCreatedDate(createdDate);

    handler.insertFill(metaObject(entity));

    assertThat(entity.getCreatedBy()).isEqualTo("existing");
    assertThat(entity.getCreatedDate()).isEqualTo(createdDate);
  }

  @Test
  @DisplayName("未登录时创建人为 null 并跳过填充，时间字段仍填充")
  void shouldSkipAuditorWhenNotAuthenticated() {
    SecurityContextHolder.clearContext();
    TestEntity entity = new TestEntity();

    handler.insertFill(metaObject(entity));

    assertThat(entity.getCreatedBy()).isNull();
    assertThat(entity.getCreatedDate()).isNotNull();
    assertThat(entity.getLastModifiedDate()).isNotNull();
  }

  @Test
  @DisplayName("updateFill 填充最后修改人与最后修改时间")
  void shouldFillModifiedFieldsOnUpdate() {
    TestEntity entity = new TestEntity();

    handler.updateFill(metaObject(entity));

    assertThat(entity.getLastModifiedBy()).isEqualTo("alice");
    assertThat(entity.getLastModifiedDate()).isNotNull();
  }

  @Test
  @DisplayName("updateFill 不触碰创建人与创建时间")
  void shouldLeaveCreationFieldsUntouchedOnUpdate() {
    TestEntity entity = new TestEntity();

    handler.updateFill(metaObject(entity));

    assertThat(entity.getCreatedBy()).isNull();
    assertThat(entity.getCreatedDate()).isNull();
  }

  @Test
  @DisplayName("updateFill 不覆盖已有值")
  void shouldNotOverwriteExistingValuesOnUpdate() {
    Instant lastModifiedDate = Instant.parse("2024-06-01T00:00:00Z");
    TestEntity entity = new TestEntity();
    entity.setLastModifiedBy("bob");
    entity.setLastModifiedDate(lastModifiedDate);

    handler.updateFill(metaObject(entity));

    assertThat(entity.getLastModifiedBy()).isEqualTo("bob");
    assertThat(entity.getLastModifiedDate()).isEqualTo(lastModifiedDate);
  }

  @Test
  @DisplayName("未标注 fill 的字段不会被填充")
  void shouldIgnoreFieldsWithoutFillAnnotation() {
    TestEntity entity = new TestEntity();

    handler.insertFill(metaObject(entity));
    handler.updateFill(metaObject(entity));

    assertThat(entity.getRemark()).isNull();
  }

  @Test
  @DisplayName("现状记录：lastModifiedBy 需标注 INSERT_UPDATE，标注 UPDATE 时 updateFill 不会填充")
  void shouldNotFillUpdateOnlyLastModifiedBy() {
    UpdateOnlyEntity entity = new UpdateOnlyEntity();

    handler.updateFill(metaObject(entity));

    assertThat(entity.getLastModifiedBy()).isNull();
    assertThat(entity.getLastModifiedDate()).isNotNull();
  }

  @TableName("test_audit_entity")
  static class TestEntity {
    @TableId private Long id;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private Instant createdDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastModifiedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Instant lastModifiedDate;

    private String remark;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getCreatedBy() {
      return createdBy;
    }

    public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
      return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
      this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
      return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
      this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
      return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
      this.lastModifiedDate = lastModifiedDate;
    }

    public String getRemark() {
      return remark;
    }

    public void setRemark(String remark) {
      this.remark = remark;
    }
  }

  @TableName("test_update_only_entity")
  static class UpdateOnlyEntity {
    @TableId private Long id;

    @TableField(fill = FieldFill.UPDATE)
    private String lastModifiedBy;

    @TableField(fill = FieldFill.UPDATE)
    private Instant lastModifiedDate;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getLastModifiedBy() {
      return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
      this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
      return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
      this.lastModifiedDate = lastModifiedDate;
    }
  }
}
