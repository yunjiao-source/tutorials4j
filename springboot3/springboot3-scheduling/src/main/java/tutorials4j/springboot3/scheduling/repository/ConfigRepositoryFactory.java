package tutorials4j.springboot3.scheduling.repository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 配置仓库工厂，根据配置选择使用哪个实现
 *
 * @author Yun Jiao
 */
@Component
@Primary
public class ConfigRepositoryFactory implements ConfigRepository {

  private final ConfigRepository activeRepository;

  @Autowired
  public ConfigRepositoryFactory(
      @Qualifier("yamlConfigRepository") ConfigRepository yamlRepository,
      @Qualifier("propertiesConfigRepository") ConfigRepository propertiesRepository) {

    // 根据配置决定使用哪个实现
    String configSource = System.getProperty("scheduled.config.source", "yaml");

    if ("properties".equalsIgnoreCase(configSource)) {
      this.activeRepository = propertiesRepository;
      System.out.println("Using PropertiesConfigRepository for task configuration");
    } else {
      this.activeRepository = yamlRepository;
      System.out.println("Using YamlConfigRepository for task configuration");
    }
  }

  @Override
  public Optional<String> getCronExpression(String taskName) {
    return activeRepository.getCronExpression(taskName);
  }

  @Override
  public Map<String, String> getAllCronExpressions() {
    return activeRepository.getAllCronExpressions();
  }

  @Override
  public boolean updateCronExpression(String taskName, String cronExpression) {
    return activeRepository.updateCronExpression(taskName, cronExpression);
  }

  @Override
  public boolean updateTaskStatus(String taskName, boolean enabled) {
    return activeRepository.updateTaskStatus(taskName, enabled);
  }

  @Override
  public void addListener(Consumer<ConfigChangeEvent> listener) {
    activeRepository.addListener(listener);
  }

  @Override
  public void removeListener(Consumer<ConfigChangeEvent> listener) {
    activeRepository.removeListener(listener);
  }

  @Override
  public Optional<TaskConfig> getTaskConfig(String taskName) {
    return activeRepository.getTaskConfig(taskName);
  }

  @Override
  public void reload() {
    activeRepository.reload();
  }
}
