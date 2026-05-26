package tutorials4j.springboot3.batch.simple.user;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityManagerFactory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import tutorials4j.springboot3.batch.simple.BatchJobProperties;
import tutorials4j.springboot3.batch.simple.LogJobExecutionListener;
import tutorials4j.springboot3.batch.simple.LogStepExecutionListener;
import tutorials4j.springboot3.batch.simple.MetricsJobExecutionListener;
import tutorials4j.springboot3.common.jpa.User;

/**
 * 用户批处理任务配置
 *
 * @author Yun Jiao
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserBatchJobConfig {
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final UserBatchJobProperties properties;
  private final MeterRegistry meterRegistry;

  // 读取器（Extract（抽取））
  @Bean
  public FlatFileItemReader<UserCsvRecord> reader() {
    FlatFileItemReader<UserCsvRecord> reader = new FlatFileItemReader<>();
    reader.setResource(properties.getInputPath());
    reader.setLinesToSkip(properties.getLinesToSkip()); // 跳过标题行
    reader.setStrict(false); // 文件不存在时不抛出异常
    reader.setSaveState(true);

    DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
    tokenizer.setDelimiter(properties.getDelimiter());
    tokenizer.setNames("name", "email");
    tokenizer.setStrict(false); // 允许列数不匹配

    BeanWrapperFieldSetMapper<UserCsvRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(UserCsvRecord.class);
    fieldSetMapper.setStrict(false); // 允许字段映射失败

    DefaultLineMapper<UserCsvRecord> lineMapper = new DefaultLineMapper<>();
    lineMapper.setLineTokenizer(tokenizer);
    lineMapper.setFieldSetMapper(fieldSetMapper);
    reader.setLineMapper(lineMapper);
    return reader;
  }

  // 处理器（Transform（转换））
  @Bean
  public CompositeItemProcessor<UserCsvRecord, User> compositeProcessor() {
    UserCsvValidator validator =
        new UserCsvValidator(
            properties.getNameMinLength(),
            properties.getNameMaxLength(),
            properties.getEmailPattern());
    ValidatorItemProcessor validatorItemProcessor = new ValidatorItemProcessor(validator);
    TransferItemProcessor transferItemProcessor = new TransferItemProcessor();
    CleanItemProcessor cleanItemProcessor = new CleanItemProcessor();
    LogItemProcessor logItemProcessor = new LogItemProcessor();

    CompositeItemProcessor<UserCsvRecord, User> compositeProcessor = new CompositeItemProcessor<>();
    compositeProcessor.setDelegates(
        Arrays.asList(
            validatorItemProcessor, transferItemProcessor, cleanItemProcessor, logItemProcessor));
    return compositeProcessor;
  }

  /** 主处理步骤 */
  @Bean
  public Step importUserStep() {
    BatchJobProperties jobProperties = properties.getJob();
    return new StepBuilder(properties.getName() + "-Step", jobRepository)
        .<UserCsvRecord, User>chunk(jobProperties.getChunkSize(), transactionManager)
        .reader(reader())
        .processor(compositeProcessor())
        .writer(writer())
        .faultTolerant()
        .skipLimit(jobProperties.getSkipLimit())
        .skip(Exception.class) // 跳过所有异常
        .noSkip(ValidationException.class) // 验证异常不跳过，直接失败
        .retryLimit(jobProperties.getRetryLimit())
        .retry(Exception.class)
        .retry(ValidationException.class)
        .listener(new LogStepExecutionListener())
        .build();
  }

  // 写入器
  @Bean
  public JpaItemWriter<User> writer() {
    JpaItemWriter<User> writer = new JpaItemWriter<>();
    writer.setEntityManagerFactory(entityManagerFactory);
    return writer;
  }

  // 主作业
  @Bean
  public Job importUserJob() {
    return new JobBuilder(properties.getName() + "-Job", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(new LogJobExecutionListener())
        .listener(new MetricsJobExecutionListener(meterRegistry))
        .start(importUserStep())
        .build();
  }
}
