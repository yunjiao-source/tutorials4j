package tutorials4j.springboot3.scheduling.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Properties文件配置仓库实现 从 tasks.properties 文件读取配置
 *
 * @author Yun Jiao
 */
@Component("propertiesConfigRepository")
@Slf4j
public class PropertiesConfigRepository implements ConfigRepository {

  private static final String TASKS_FILE = "tasks.properties";
  private static final String ENABLED_SUFFIX = ".enabled";
  private static final String CRON_SUFFIX = ".cron";
  private static final String DESC_SUFFIX = ".desc";

  private final Map<String, TaskConfig> tasks = new ConcurrentHashMap<>();
  private final List<Consumer<ConfigChangeEvent>> listeners = new CopyOnWriteArrayList<>();
  private final ApplicationEventPublisher eventPublisher;

  @Value("classpath:${scheduled.tasks.file:" + TASKS_FILE + "}")
  private Resource tasksResource;

  private Path filePath;
  private long lastModified = 0;
  private WatchService watchService;

  public PropertiesConfigRepository(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @PostConstruct
  public void init() throws IOException {
    loadFromFile();

    // 尝试获取文件路径用于监听
    try {
      filePath = Paths.get(tasksResource.getURI());
      startFileWatcher();
    } catch (Exception e) {
      log.warn("Cannot watch tasks file, hot reload disabled: {}", e.getMessage());
    }

    log.info("Properties ConfigRepository initialized with {} tasks", tasks.size());
  }

  private void loadFromFile() {
    try (InputStream inputStream = tasksResource.getInputStream()) {
      Properties props = new Properties();
      props.load(inputStream);
      processProperties(props);

      if (filePath != null) {
        lastModified = Files.getLastModifiedTime(filePath).toMillis();
      }

      log.debug("Loaded {} task configurations from {}", tasks.size(), TASKS_FILE);
    } catch (IOException e) {
      log.error("Failed to load tasks.properties", e);
    }
  }

  private void processProperties(Properties props) {
    Map<String, Map<String, String>> taskData = new HashMap<>();

    // 按任务名分组所有属性
    props
        .stringPropertyNames()
        .forEach(
            key -> {
              int dotIndex = key.indexOf('.');
              if (dotIndex > 0) {
                String taskName = key.substring(0, dotIndex);
                String property = key.substring(dotIndex + 1);
                String value = props.getProperty(key);

                taskData.computeIfAbsent(taskName, k -> new HashMap<>()).put(property, value);
              }
            });

    // 转换为TaskConfig对象
    tasks.clear();
    taskData.forEach(
        (taskName, properties) -> {
          TaskConfig config = createTaskConfig(taskName, properties);
          tasks.put(taskName, config);
        });
  }

  private TaskConfig createTaskConfig(String taskName, Map<String, String> properties) {
    String cron = properties.get("cron");
    boolean enabled = Boolean.parseBoolean(properties.getOrDefault("enabled", "true"));
    String description = properties.get("desc");

    Map<String, Object> metadata = new HashMap<>();
    metadata.put("source", "properties");
    metadata.put("loadedAt", new Date());

    // 将所有属性作为metadata
    properties.forEach(
        (key, value) -> {
          if (!Arrays.asList("cron", "enabled", "desc").contains(key)) {
            metadata.put(key, value);
          }
        });

    return new TaskConfig(taskName, cron, enabled, description, metadata);
  }

  @Override
  public Optional<String> getCronExpression(String taskName) {
    TaskConfig config = tasks.get(taskName);
    return Optional.ofNullable(config)
        .filter(TaskConfig::isEnabled)
        .map(TaskConfig::getCronExpression);
  }

  @Override
  public Map<String, String> getAllCronExpressions() {
    return tasks.values().stream()
        .filter(TaskConfig::isEnabled)
        .filter(config -> config.getCronExpression() != null)
        .collect(Collectors.toMap(TaskConfig::getTaskName, TaskConfig::getCronExpression));
  }

  @Override
  public Optional<TaskConfig> getTaskConfig(String taskName) {
    return Optional.ofNullable(tasks.get(taskName));
  }

  @Override
  public boolean updateCronExpression(String taskName, String cronExpression) {
    try {
      Properties props = loadCurrentProperties();

      // 更新属性
      props.setProperty(taskName + CRON_SUFFIX, cronExpression);

      // 确保任务已存在
      if (!props.containsKey(taskName + ENABLED_SUFFIX)) {
        props.setProperty(taskName + ENABLED_SUFFIX, "true");
      }

      // 保存到文件
      saveProperties(props);

      // 更新内存缓存
      reload();

      notifyListeners(new ConfigChangeEvent(taskName, ConfigChangeEvent.ConfigChangeType.UPDATED));

      log.info("Updated cron expression for task {} to {}", taskName, cronExpression);
      return true;

    } catch (IOException e) {
      log.error("Failed to update cron expression for task {}", taskName, e);
      return false;
    }
  }

  @Override
  public boolean updateTaskStatus(String taskName, boolean enabled) {
    try {
      Properties props = loadCurrentProperties();
      props.setProperty(taskName + ENABLED_SUFFIX, String.valueOf(enabled));

      saveProperties(props);
      reload();

      notifyListeners(
          new ConfigChangeEvent(
              taskName,
              enabled
                  ? ConfigChangeEvent.ConfigChangeType.ENABLED
                  : ConfigChangeEvent.ConfigChangeType.DISABLED));

      log.info("Updated task {} status to enabled={}", taskName, enabled);
      return true;

    } catch (IOException e) {
      log.error("Failed to update task status for {}", taskName, e);
      return false;
    }
  }

  @Override
  public void addListener(Consumer<ConfigChangeEvent> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(Consumer<ConfigChangeEvent> listener) {
    listeners.remove(listener);
  }

  @Override
  public void reload() {
    loadFromFile();
    log.info("Reloaded properties configurations, total {} tasks", tasks.size());
  }

  private Properties loadCurrentProperties() throws IOException {
    Properties props = new Properties();
    try (InputStream inputStream = tasksResource.getInputStream()) {
      props.load(inputStream);
    }
    return props;
  }

  private void saveProperties(Properties props) throws IOException {
    if (filePath != null) {
      try (OutputStream outputStream = Files.newOutputStream(filePath)) {
        // 添加文件头注释
        String header =
            "# Auto-generated by application\n"
                + "# Do not modify manually unless you know what you're doing\n"
                + "# Last modified: "
                + new Date()
                + "\n\n";
        outputStream.write(header.getBytes());

        // 按任务名排序输出，提高可读性
        List<String> taskNames =
            props.stringPropertyNames().stream()
                .map(key -> key.contains(".") ? key.substring(0, key.indexOf('.')) : key)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        for (String taskName : taskNames) {
          // 按固定顺序输出属性：enabled, cron, desc, 其他
          outputProperty(props, outputStream, taskName + ENABLED_SUFFIX);
          outputProperty(props, outputStream, taskName + CRON_SUFFIX);
          outputProperty(props, outputStream, taskName + DESC_SUFFIX);

          // 输出其他属性
          props.stringPropertyNames().stream()
              .filter(key -> key.startsWith(taskName + "."))
              .filter(
                  key ->
                      !key.endsWith(ENABLED_SUFFIX)
                          && !key.endsWith(CRON_SUFFIX)
                          && !key.endsWith(DESC_SUFFIX))
              .sorted()
              .forEach(key -> outputProperty(props, outputStream, key));

          outputStream.write("\n".getBytes()); // 任务间空行
        }
      }
    } else {
      log.warn("Cannot save properties, file path not available");
      throw new IOException("File path not available");
    }
  }

  private void outputProperty(Properties props, OutputStream outputStream, String key) {
    if (props.containsKey(key)) {
      String value = props.getProperty(key);
      String line = key + "=" + value.replace("\n", "\\n") + "\n";
      try {
        outputStream.write(line.getBytes());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  /** 启动文件监听器，实现热更新 */
  private void startFileWatcher() {
    if (filePath == null) return;

    try {
      watchService = FileSystems.getDefault().newWatchService();
      Path parentDir = filePath.getParent();

      parentDir.register(
          watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

      Thread watcherThread =
          new Thread(
              () -> {
                try {
                  while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key = watchService.take();

                    for (WatchEvent<?> event : key.pollEvents()) {
                      Path changed = (Path) event.context();
                      if (changed.equals(filePath.getFileName())) {
                        handleFileChange();
                      }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                      log.warn("Watch key no longer valid");
                      break;
                    }
                  }
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  log.info("File watcher interrupted");
                } catch (ClosedWatchServiceException e) {
                  log.info("File watcher closed");
                } catch (Exception e) {
                  log.error("File watcher error", e);
                }
              });

      watcherThread.setName("tasks-file-watcher");
      watcherThread.setDaemon(true);
      watcherThread.start();

      log.info("Started file watcher for {}", filePath);

    } catch (IOException e) {
      log.warn("Failed to start file watcher", e);
    }
  }

  private void handleFileChange() {
    try {
      long currentModified = Files.getLastModifiedTime(filePath).toMillis();
      if (currentModified > lastModified + 1000) { // 1秒防抖
        Thread.sleep(500); // 等待文件写入完成
        reload();
        lastModified = currentModified;

        log.info("Tasks file changed, reloaded configurations");

        // 通知所有配置已更新
        tasks
            .keySet()
            .forEach(
                taskName -> {
                  notifyListeners(
                      new ConfigChangeEvent(taskName, ConfigChangeEvent.ConfigChangeType.UPDATED));
                });
      }
    } catch (Exception e) {
      log.error("Error handling file change", e);
    }
  }

  /** 定时检查文件变更（备用方案） */
  @Scheduled(fixedDelay = 10000) // 每10秒检查一次
  public void checkFileChange() {
    if (filePath != null && watchService == null) {
      try {
        long currentModified = Files.getLastModifiedTime(filePath).toMillis();
        if (currentModified > lastModified) {
          handleFileChange();
        }
      } catch (IOException e) {
        log.warn("Cannot check file modification time", e);
      }
    }
  }

  private void notifyListeners(ConfigChangeEvent event) {
    listeners.forEach(
        listener -> {
          try {
            listener.accept(event);
          } catch (Exception e) {
            log.error("Error notifying listener for event: {}", event.getTaskName(), e);
          }
        });
  }

  @PreDestroy
  public void cleanup() {
    if (watchService != null) {
      try {
        watchService.close();
      } catch (IOException e) {
        log.warn("Error closing watch service", e);
      }
    }
  }
}
