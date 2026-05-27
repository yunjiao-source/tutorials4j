package tutorials4j.springboot3.integration.jasperreports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.springframework.core.io.ClassPathResource;

/**
 * 报表生成工具类
 *
 * @author yangyunjiao
 */
public class ReportGenerator {

  public static byte[] generate(List<User> users, String format) throws Exception {
    // 1. 加载并编译报表模板
    ClassPathResource resource = new ClassPathResource("jasperreports/user.jrxml");
    JasperDesign jasperDesign = JRXmlLoader.load(resource.getInputStream());
    JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

    // 2. 准备数据源和参数
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("title", "用户列表");

    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

    // 3. 导出不同格式
    return switch (format) {
      case "pdf" -> JasperExportManager.exportReportToPdf(jasperPrint);
      case "xml" -> JasperExportManager.exportReportToXml(jasperPrint).getBytes();
      case "html" -> exportHtml(jasperPrint);
      default -> throw new IllegalArgumentException("不支持的格式: " + format);
    };
  }

  private static byte[] exportHtml(JasperPrint jasperPrint) throws IOException, JRException {
    Path tempFile = null;
    try {
      // 创建临时文件
      tempFile = Files.createTempFile("jasper_", ".html");

      // 导出报告
      JasperExportManager.exportReportToHtmlFile(jasperPrint, tempFile.toString());

      return Files.readAllBytes(tempFile);

    } finally {
      // 确保临时文件被清理
      if (tempFile != null) {
        Files.deleteIfExists(tempFile);
      }
    }
  }
}
