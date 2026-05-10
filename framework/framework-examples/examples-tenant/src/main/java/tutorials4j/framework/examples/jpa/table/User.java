package tutorials4j.framework.examples.jpa.table;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import tutorials4j.framework.tenant.hibernate.TenantEntity;

/**
 * 用戶
 *
 * @author Yun Jiao
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_user_tenant")
public class User extends TenantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name ;
    private String password ;
    private String email;
    private Integer age;
    private String secretKey ;

    public static User of(String name, String email) {
        User user = new User();
        user.name = name;
        user.email = email;
        return user;
    }
}
