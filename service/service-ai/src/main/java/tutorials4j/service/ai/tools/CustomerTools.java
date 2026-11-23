package tutorials4j.service.ai.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import tutorials4j.service.ai.model.Customer;
import tutorials4j.service.ai.service.CustomerService;

/**
 * TODO
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class CustomerTools {
  private final CustomerService customerService;

  @Tool(description = "Retrieve customer information")
  Customer getCustomerInfo(String id, ToolContext toolContext) {
    return customerService.findById(id, toolContext.getContext().get("tenantId"));
  }

  @Tool(description = "Update customer information")
  void updateCustomerInfo(
      Long id,
      String name,
      @ToolParam(description = "Email address, RFC 5322 format", required = false) String email) {
    // ...
  }
}
