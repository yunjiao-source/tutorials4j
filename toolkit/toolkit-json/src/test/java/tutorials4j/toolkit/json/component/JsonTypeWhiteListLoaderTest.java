package tutorials4j.toolkit.json.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link JsonTypeWhiteListLoader} 单元测试：白名单文件加载、注释过滤与生命周期回调。
 *
 * <p>测试资源中包含额外的 {@code META-INF/tutorials4j/json-type-white-list.conf}（含注释、空白行
 * 与首尾带空格的条目），用于验证多资源合并与解析规则。
 *
 * @author YunJiao Yang
 */
class JsonTypeWhiteListLoaderTest {
  private static final String MAIN_RESOURCE_ENTRY = "java.util.ArrayList";
  private static final String TEST_RESOURCE_ENTRY = "com.example.TestAllowType";
  private static final String TRIMMED_ENTRY = "com.example.PaddedType";
  private static final String BLANK_SEPARATED_ENTRY = "java.example.BlankSeparated";

  private final JsonTypeWhiteListLoader loader = new JsonTypeWhiteListLoader();

  @Test
  @DisplayName("执行生命周期回调前白名单为空")
  void shouldBeEmptyBeforeLoad() {
    assertThat(loader.contains(MAIN_RESOURCE_ENTRY)).isFalse();
    assertThat(loader.contains(TEST_RESOURCE_ENTRY)).isFalse();
  }

  @Test
  @DisplayName("load 合并 classpath 上所有白名单资源")
  void shouldLoadEntriesFromAllResources() throws IOException {
    Set<String> loaded = loader.load();

    assertThat(loaded)
        .contains(
            MAIN_RESOURCE_ENTRY,
            "java.util.LinkedHashMap",
            TEST_RESOURCE_ENTRY,
            TRIMMED_ENTRY,
            BLANK_SEPARATED_ENTRY);
  }

  @Test
  @DisplayName("忽略空行与 # 注释，并去除条目首尾空白")
  void shouldIgnoreCommentsAndTrimEntries() throws IOException {
    Set<String> loaded = loader.load();

    assertThat(loaded).doesNotContain("# 测试用白名单", "");
    assertThat(loaded)
        .allSatisfy(
            className -> {
              assertThat(className).isNotBlank();
              assertThat(className).doesNotStartWith("#");
              assertThat(className).isEqualTo(className.trim());
            });
    assertThat(loaded).anyMatch(entry -> entry.equals(TRIMMED_ENTRY));
  }

  @Test
  @DisplayName("生命周期回调后将白名单写入内部集合并可查询")
  void shouldPopulateWhiteListOnAfterSingletonsInstantiated() {
    loader.afterSingletonsInstantiated();

    assertThat(loader.contains(MAIN_RESOURCE_ENTRY)).isTrue();
    assertThat(loader.contains(TEST_RESOURCE_ENTRY)).isTrue();
    assertThat(loader.contains("com.example.NotListed")).isFalse();
  }

  @Test
  @DisplayName("重复回调不会产生重复项，结果保持一致")
  void shouldBeIdempotent() throws IOException {
    loader.afterSingletonsInstantiated();
    Set<String> first = loader.load();
    loader.afterSingletonsInstantiated();
    Set<String> second = loader.load();

    assertThat(second).isEqualTo(first);
    assertThat(second).hasSameSizeAs(first);
  }

  @Test
  @DisplayName("contains 按全限定类名精确匹配")
  void shouldMatchExactClassName() {
    loader.afterSingletonsInstantiated();

    assertThat(loader.contains(MAIN_RESOURCE_ENTRY)).isTrue();
    assertThat(loader.contains("ArrayList")).isFalse();
    assertThat(loader.contains(MAIN_RESOURCE_ENTRY + " ")).isFalse();
  }
}
