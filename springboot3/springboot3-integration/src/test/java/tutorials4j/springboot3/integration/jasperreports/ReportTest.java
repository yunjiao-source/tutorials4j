package tutorials4j.springboot3.integration.jasperreports;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * 测试用例
 *
 * @author yangyunjiao
 */
public class ReportTest {
  @Test
  public void validate() {

    try {
      ClassPathResource jrxml = new ClassPathResource("jasperreports/user.jrxml");
      ClassPathResource xsd = new ClassPathResource("jasperreport.xsd");
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = factory.newSchema(new StreamSource(xsd.getInputStream()));
      Validator validator = schema.newValidator();

      validator.validate(new StreamSource(jrxml.getInputStream()));
      System.out.println("✅ XML 符合 XSD 架构");

    } catch (Exception e) {
      System.err.println("❌ XML 验证失败: " + e.getMessage());
    }
  }

  @Test
  public void compile() throws IOException, JRException {
    try {
      ClassPathResource jrxml = new ClassPathResource("jasperreports/user.jrxml");
      JasperDesign jasperDesign = JRXmlLoader.load(jrxml.getInputStream());
      JasperCompileManager.compileReport(jasperDesign);
      System.out.println("✅ 编译成功");
    } catch (Exception e) {
      System.err.println("❌ 编译失败: " + e.getMessage());
    }
  }
}
