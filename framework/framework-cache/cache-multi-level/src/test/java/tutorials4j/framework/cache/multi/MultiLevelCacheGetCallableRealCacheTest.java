package tutorials4j.framework.cache.multi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/** 针对 {@link MultiLevelCache#get(Object, Callable)} 的单元测试。 使用真实的缓存实现（基于内存 Map），避免 Mock。 */
class MultiLevelCacheGetCallableRealCacheTest {

  /** 简单的本地缓存实现（基于 ConcurrentHashMap）。 */
  static class SimpleMapCache implements Cache {
    private final String name;
    protected final ConcurrentHashMap<Object, Object> store = new ConcurrentHashMap<>();

    SimpleMapCache(String name) {
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Object getNativeCache() {
      return store;
    }

    @Override
    public ValueWrapper get(Object key) {
      Object value = store.get(key);
      return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
      Object value = store.get(key);
      if (value != null && type.isAssignableFrom(value.getClass())) {
        return type.cast(value);
      }
      return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
      return (T)
          store.computeIfAbsent(
              key,
              k -> {
                try {
                  return valueLoader.call();
                } catch (Exception e) {
                  throw new Cache.ValueRetrievalException(key, valueLoader, e);
                }
              });
    }

    @Override
    public void put(Object key, Object value) {
      if (value != null) {
        store.put(key, value);
      } else {
        store.remove(key);
      }
    }

    @Override
    public void evict(Object key) {
      store.remove(key);
    }

    @Override
    public void clear() {
      store.clear();
    }
  }

  private SimpleMapCache localCache;
  private SimpleMapCache remoteCache;
  private MultiLevelCache multiLevelCache;

  @BeforeEach
  void setUp() {
    localCache = new SimpleMapCache("local");
    remoteCache = new SimpleMapCache("remote");
    multiLevelCache = new MultiLevelCache(localCache, remoteCache);
  }

  @AfterEach
  void tearDown() {
    // 清理可能残留的锁（避免影响后续测试）
    multiLevelCache.locks.clear();
  }

  @Test
  void shouldReturnValueFromLocalWhenPresent() throws Exception {
    // given
    String key = "localKey";
    String expectedValue = "valueFromLocal";
    localCache.put(key, expectedValue);

    // when
    String result =
        multiLevelCache.get(
            key,
            () -> {
              throw new AssertionError("valueLoader should not be called");
            });

    // then
    assertThat(result).isEqualTo(expectedValue);
    // 验证远程未被读取或写入
    assertThat(remoteCache.get(key)).isNull();
  }

  @Test
  void shouldReturnValueFromRemoteAndPopulateLocalWhenLocalMissRemoteHit() throws Exception {
    // given
    String key = "remoteOnlyKey";
    String remoteValue = "valueFromRemote";
    remoteCache.put(key, remoteValue);

    // when
    String result =
        multiLevelCache.get(
            key,
            () -> {
              throw new AssertionError("valueLoader should not be called");
            });

    // then
    assertThat(result).isEqualTo(remoteValue);
    // 本地应该已被回填
    assertThat(localCache.get(key).get()).isEqualTo(remoteValue);
  }

  @Test
  void shouldLoadValueViaCallableWhenBothCachesMiss() throws Exception {
    // given
    String key = "missingKey";
    String loadedValue = "loadedValue";
    AtomicInteger loaderCallCount = new AtomicInteger(0);

    Callable<String> loader =
        () -> {
          loaderCallCount.incrementAndGet();
          return loadedValue;
        };

    // when
    String result = multiLevelCache.get(key, loader);

    // then
    assertThat(result).isEqualTo(loadedValue);
    assertThat(loaderCallCount.get()).isEqualTo(1);
    // 验证两级缓存均被写入
    assertThat(localCache.get(key).get()).isEqualTo(loadedValue);
    assertThat(remoteCache.get(key).get()).isEqualTo(loadedValue);
  }

  @Test
  void shouldPropagateRuntimeExceptionFromValueLoader() {
    // given
    String key = "exceptionKey";
    Callable<String> failingLoader =
        () -> {
          throw new RuntimeException("loader failed");
        };

    // when/then
    assertThatThrownBy(() -> multiLevelCache.get(key, failingLoader))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Value for key 'exceptionKey' could not be loaded using");

    // 缓存不应该被写入
    assertThat(localCache.get(key)).isNull();
    assertThat(remoteCache.get(key)).isNull();
  }

  @Test
  void shouldWrapCheckedExceptionFromValueLoaderInRuntimeException() {
    // given
    String key = "checkedExceptionKey";
    Callable<String> checkedExceptionLoader =
        () -> {
          throw new Exception("checked exception");
        };

    // when/then
    assertThatThrownBy(() -> multiLevelCache.get(key, checkedExceptionLoader))
        .isInstanceOf(RuntimeException.class)
        .hasCauseInstanceOf(Exception.class)
        .hasMessageContaining("Value for key 'checkedExceptionKey' could not be");
  }

  @Test
  void shouldInvokeValueLoaderOnlyOnceUnderConcurrentAccess() throws Exception {
    // given
    String key = "concurrentKey";
    String loadedValue = "concurrentValue";
    AtomicInteger loaderCallCount = new AtomicInteger(0);

    Callable<String> countingLoader =
        () -> {
          loaderCallCount.incrementAndGet();
          // 模拟耗时加载
          Thread.sleep(50);
          return loadedValue;
        };

    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CyclicBarrier barrier = new CyclicBarrier(threadCount);

    @SuppressWarnings("unchecked")
    Future<String>[] futures = new Future[threadCount];

    // when
    for (int i = 0; i < threadCount; i++) {
      futures[i] =
          executor.submit(
              () -> {
                startLatch.await();
                barrier.await(); // 尽量同时进入
                return multiLevelCache.get(key, countingLoader);
              });
    }
    startLatch.countDown();

    // then
    for (Future<String> future : futures) {
      assertThat(future.get()).isEqualTo(loadedValue);
    }
    assertThat(loaderCallCount.get()).isEqualTo(1);

    // 验证缓存只被写入一次
    assertThat(localCache.get(key).get()).isEqualTo(loadedValue);
    assertThat(remoteCache.get(key).get()).isEqualTo(loadedValue);

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
  }

  @Test
  void shouldInvokeValueLoaderOnlyOnceUnderConcurrentAccess1() throws Exception {
    // given
    String loadedValue = "concurrentValue";

    Callable<String> countingLoader = () -> loadedValue;

    int threadCount = 20;
    int keySize = 3;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CyclicBarrier barrier = new CyclicBarrier(threadCount);

    @SuppressWarnings("unchecked")
    Future<String>[] futures = new Future[threadCount];

    // when
    for (int i = 0; i < threadCount; i++) {
      String key = "key" + ThreadLocalRandom.current().nextInt(keySize);
      futures[i] =
          executor.submit(
              () -> {
                startLatch.await();
                barrier.await(); // 尽量同时进入
                return multiLevelCache.get(key, countingLoader);
              });
    }
    startLatch.countDown();

    // then
    for (Future<String> future : futures) {
      assertThat(future.get()).isEqualTo(loadedValue);
    }

    assertThat(multiLevelCache.locks.size()).isEqualTo(0);
    assertThat(localCache.store.size()).isLessThanOrEqualTo(keySize);
    assertThat(remoteCache.store.size()).isLessThanOrEqualTo(keySize);

    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
  }

  @Test
  void shouldNotCallValueLoaderIfAnotherThreadPopulatedCacheDuringWait() throws Exception {
    // 此测试验证双重检查锁定：线程 A 加载值，线程 B 在锁外等待，
    // 当线程 A 释放锁后，线程 B 进入锁内二次检查时发现缓存已存在，不再调用 loader。
    String key = "doubleCheckKey";
    String loadedValue = "doubleCheckValue";
    AtomicInteger loaderCallCount = new AtomicInteger(0);

    Callable<String> loader =
        () -> {
          loaderCallCount.incrementAndGet();
          return loadedValue;
        };

    // 先清空缓存
    localCache.evict(key);
    remoteCache.evict(key);

    // 线程 A 调用 get（会触发加载）
    String resultA = multiLevelCache.get(key, loader);
    assertThat(resultA).isEqualTo(loadedValue);
    assertThat(loaderCallCount.get()).isEqualTo(1);

    // 此时缓存已存在，线程 B 再次调用应该直接命中本地，不调用 loader
    String resultB =
        multiLevelCache.get(
            key,
            () -> {
              throw new AssertionError("loader should not be called");
            });
    assertThat(resultB).isEqualTo(loadedValue);
    assertThat(loaderCallCount.get()).isEqualTo(1);
  }
}
