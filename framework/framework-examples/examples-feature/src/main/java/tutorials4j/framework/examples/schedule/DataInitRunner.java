package tutorials4j.framework.examples.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tutorials4j.framework.feature.schedule.domain.JobEntity;
import tutorials4j.framework.feature.schedule.domain.JobService;

/**
 * 测试数据
 *
 * @author Yun Jiao
 */
@Component
@RequiredArgsConstructor
public class DataInitRunner implements CommandLineRunner {
  private final JobService jobService;

  @Override
  public void run(String... args) throws Exception {
    if (jobService.count() > 0) {
      return;
    }

    saveJob("Demo1TaskRunner", "demo1TaskRunner", "0/7 * * * * ?");
    saveJob("Demo2ExceptionTaskRunner", "demo2ExceptionTaskRunner", "0/8 * * * * ?");
    saveJob("Demo3LongTimeTaskRunner", "demo3LongTimeTaskRunner", "0/9 * * * * ?");
    saveJob("Demo4AutoRenewalTaskRunner", "demo4AutoRenewalTaskRunner", "0/10 * * * * ?");
    saveJob("Demo5FixedLeaseTaskRunner", "demo5FixedLeaseTaskRunner", "0/11 * * * * ?");
    saveJob("Demo6AutoRenewalBlockTaskRunner", "demo6AutoRenewalBlockTaskRunner", "0/12 * * * * ?");
    saveJob(
        "Demo7AutoRenewalReentrantTaskRunner",
        "demo7AutoRenewalReentrantTaskRunner",
        "0/13 * * * * ?");
    saveJob("Demo8FixedLeaseBlockTaskRunner", "demo8FixedLeaseBlockTaskRunner", "0/14 * * * * ?");
    saveJob(
        "Demo9FixedLeaseReentrantTaskRunner",
        "demo9FixedLeaseReentrantTaskRunner",
        "0/15 * * * * ?");
  }

  private void saveJob(String taskCode, String classSimpleName, String cron) {
    JobEntity job = new JobEntity();
    job.setTaskCode(taskCode);
    job.setClassSimpleName(classSimpleName);
    job.setCron(cron);
    jobService.save(job);
  }
}
