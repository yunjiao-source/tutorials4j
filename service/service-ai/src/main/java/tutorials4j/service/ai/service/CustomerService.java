package tutorials4j.service.ai.service;

import org.springframework.stereotype.Service;
import tutorials4j.service.ai.model.Customer;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Service
public class CustomerService {

  public Customer findById(String id, Object tenantId) {
    Customer customer = new Customer();
    customer.setId(id);
    customer.setName("Yun Jiao");
    return customer;
  }
}
