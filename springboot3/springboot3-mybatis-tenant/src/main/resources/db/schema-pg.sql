-- 创建用户表（如果不存在）
CREATE TABLE IF NOT EXISTS user_mybatis_tenant (
                                           id          BIGSERIAL PRIMARY KEY,          -- 主键，自增长整型
                                           tenant_id   VARCHAR(255),                          -- 租户ID
                                           name        VARCHAR(255)                      -- 用户名
    );

-- 添加表注释（从实体类注释获取）
COMMENT ON TABLE user_mybatis_tenant IS '租户用戶';

-- （可选）添加字段注释，可根据字段含义补充
COMMENT ON COLUMN user_mybatis_tenant.id IS '主键ID';
COMMENT ON COLUMN user_mybatis_tenant.tenant_id IS '租户ID';
COMMENT ON COLUMN user_mybatis_tenant.name IS '姓名';