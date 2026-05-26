-- 创建用户表（如果不存在）
CREATE TABLE IF NOT EXISTS user_mybatis_tenant (
                                           id          BIGSERIAL PRIMARY KEY,          -- 主键，自增长整型
                                           tenant_id   VARCHAR(255),                          -- 租户ID
                                           name        VARCHAR(255)                      -- 用户名
    );
