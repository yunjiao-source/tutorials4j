package tutorials4j.framework.examples;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.data.hibernate.tenant.TenantEntity;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_user_tenant")
public class UserTenant extends TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name ;
    private String password ;
    private String email;
    private Integer age;
    private String secretKey ;

    public static UserTenant of(String name, String email) {
        UserTenant user = new UserTenant();
        user.name = name;
        user.email = email;
        return user;
    }
}
