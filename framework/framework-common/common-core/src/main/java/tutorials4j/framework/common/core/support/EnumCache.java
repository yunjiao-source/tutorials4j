package tutorials4j.framework.common.core.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 枚举缓存工具类。
 *
 * <p>提供基于枚举名称（{@link Enum#name()}）和自定义值的双向缓存查询功能。 使用时需要在枚举类的静态代码块中手动注册缓存（{@link
 * #registerByName(Class, Enum[])} 或 {@link #registerByValue(Class, Enum[],
 * EnumMapping)}），否则首次查询会尝试通过类加载触发注册， 但建议显式注册以避免运行时异常。
 *
 * <p>注意：基于值的缓存要求每个值在枚举中唯一，否则注册时会抛出异常。
 *
 * @author Yun Jiao
 * @see #registerByName(Class, Enum[])
 * @see #registerByValue(Class, Enum[], EnumMapping)
 * @see #findByName(Class, String)
 * @see #findByValue(Class, Object)
 */
public class EnumCache {
  /** 以枚举任意值构建的缓存结构，键为用户定义的值（如 code），值为枚举实例 */
  static final Map<Class<? extends Enum<?>>, Map<Object, Enum<?>>> CACHE_BY_VALUE =
      new ConcurrentHashMap<>();

  /** 以枚举名称构建的缓存结构，键为枚举常量名（{@link Enum#name()}），值为枚举实例 */
  static final Map<Class<? extends Enum<?>>, Map<Object, Enum<?>>> CACHE_BY_NAME =
      new ConcurrentHashMap<>();

  /** 枚举静态块加载标识缓存结构，用于避免重复触发类初始化 */
  static final Map<Class<? extends Enum<?>>, Boolean> LOADED = new ConcurrentHashMap<>();

  /**
   * 注册基于枚举名称的缓存。
   *
   * <p>将枚举常量的 {@code name()} 作为键存入缓存，通常用于 {@link #findByName(Class, String)} 查询。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param es 枚举常量数组（通常为 {@code clazz.getEnumConstants()} 或 {@code clazz.values()}）
   */
  public static <E extends Enum<?>> void registerByName(Class<E> clazz, E[] es) {
    Map<Object, Enum<?>> map = new ConcurrentHashMap<>();
    for (E e : es) {
      map.put(e.name(), e);
    }
    CACHE_BY_NAME.put(clazz, map);
  }

  /**
   * 注册基于自定义值的缓存。
   *
   * <p>通过 {@link EnumMapping} 提取每个枚举常量的自定义值作为键存入缓存， 用于 {@link #findByValue(Class, Object)} 查询。
   *
   * <p>如果同一枚举类已经注册过值缓存，会抛出异常。同时会检查值的唯一性，重复值也会抛出异常。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param es 枚举常量数组
   * @param enumMapping 值提取函数，将枚举映射到对应的自定义值（如 getCode()）
   * @throws IllegalStateException 如果该类已注册值缓存，或存在重复的自定义值
   */
  public static <E extends Enum<?>> void registerByValue(
      Class<E> clazz, E[] es, EnumMapping<E> enumMapping) {
    if (CACHE_BY_VALUE.containsKey(clazz)) {
      throw new IllegalStateException(
          String.format("枚举%s已经构建过value缓存,不允许重复构建", clazz.getSimpleName()));
    }
    Map<Object, Enum<?>> map = new ConcurrentHashMap<>();
    for (E e : es) {
      Object value = enumMapping.value(e);
      if (map.containsKey(value)) {
        throw new IllegalStateException(
            String.format(
                "枚举%s存在相同的值%s映射同一个枚举%s.%s",
                clazz.getSimpleName(), value, clazz.getSimpleName(), e));
      }
      map.put(value, e);
    }
    CACHE_BY_VALUE.put(clazz, map);
  }

  /**
   * 根据枚举名称查找枚举实例，找不到时返回 {@code null}。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param name 枚举常量名（区分大小写）
   * @return 匹配的枚举实例，如果未找到则返回 {@code null}
   * @throws IllegalStateException 如果枚举类未注册名称缓存且无法自动触发注册
   */
  public static <E extends Enum<?>> E findByName(Class<E> clazz, String name) {
    return find(clazz, name, CACHE_BY_NAME, null);
  }

  /**
   * 根据枚举名称查找枚举实例，找不到时返回指定的默认值。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param name 枚举常量名（区分大小写）
   * @param defaultEnum 默认枚举值（可为 null）
   * @return 匹配的枚举实例，如果未找到则返回 {@code defaultEnum}
   * @throws IllegalStateException 如果枚举类未注册名称缓存且无法自动触发注册
   */
  public static <E extends Enum<?>> E findByName(Class<E> clazz, String name, E defaultEnum) {
    return find(clazz, name, CACHE_BY_NAME, defaultEnum);
  }

  /**
   * 根据自定义值查找枚举实例，找不到时返回 {@code null}。
   *
   * <p>需要先通过 {@link #registerByValue(Class, Enum[], EnumMapping)} 注册值缓存。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param value 自定义值（类型应与注册时提取的值类型一致）
   * @return 匹配的枚举实例，如果未找到则返回 {@code null}
   * @throws IllegalStateException 如果枚举类未注册值缓存且无法自动触发注册
   */
  public static <E extends Enum<?>> E findByValue(Class<E> clazz, Object value) {
    return find(clazz, value, CACHE_BY_VALUE, null);
  }

  /**
   * 根据自定义值查找枚举实例，找不到时返回指定的默认值。
   *
   * <p>需要先通过 {@link #registerByValue(Class, Enum[], EnumMapping)} 注册值缓存。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param value 自定义值
   * @param defaultEnum 默认枚举值（可为 null）
   * @return 匹配的枚举实例，如果未找到则返回 {@code defaultEnum}
   * @throws IllegalStateException 如果枚举类未注册值缓存且无法自动触发注册
   */
  public static <E extends Enum<?>> E findByValue(Class<E> clazz, Object value, E defaultEnum) {
    return find(clazz, value, CACHE_BY_VALUE, defaultEnum);
  }

  /**
   * 通用缓存查找方法。
   *
   * <p>如果缓存中不存在指定枚举类的映射，会尝试通过 {@link #executeEnumStatic(Class)} 触发枚举类的静态初始化，
   * 期望静态块中完成注册。若仍未找到缓存则抛出运行时异常。
   *
   * @param <E> 枚举类型
   * @param clazz 枚举的 Class 对象
   * @param obj 查找键（名称或值）
   * @param cache 使用的缓存（{@link #CACHE_BY_NAME} 或 {@link #CACHE_BY_VALUE}）
   * @param defaultEnum 默认值
   * @return 查找到的枚举实例或默认值
   * @throws IllegalStateException 当缓存缺失且无法通过类加载补救时抛出，并附带注册引导提示
   */
  @SuppressWarnings("unchecked")
  private static <E extends Enum<?>> E find(
      Class<E> clazz,
      Object obj,
      Map<Class<? extends Enum<?>>, Map<Object, Enum<?>>> cache,
      E defaultEnum) {
    Map<Object, Enum<?>> map = null;
    if ((map = cache.get(clazz)) == null) {
      // 触发枚举静态块执行
      executeEnumStatic(clazz);
      // 执行枚举静态块后重新获取缓存
      map = cache.get(clazz);
    }
    if (map == null) {
      String msg = null;
      if (cache == CACHE_BY_NAME) {
        msg =
            String.format(
                "枚举%s还没有注册到枚举缓存中，请在%s.static代码块中加入如下代码 : EnumCache.registerByName(%s.class, %s.values());",
                clazz.getSimpleName(),
                clazz.getSimpleName(),
                clazz.getSimpleName(),
                clazz.getSimpleName());
      }
      if (cache == CACHE_BY_VALUE) {
        msg =
            String.format(
                "枚举%s还没有注册到枚举缓存中，请在%s.static代码块中加入如下代码 : EnumCache.registerByValue(%s.class, %s.values(), %s::getXxx);",
                clazz.getSimpleName(),
                clazz.getSimpleName(),
                clazz.getSimpleName(),
                clazz.getSimpleName(),
                clazz.getSimpleName());
      }
      throw new IllegalStateException(msg);
    }
    if (obj == null) {
      return defaultEnum;
    }
    Enum<?> result = map.get(obj);
    return result == null ? defaultEnum : (E) result;
  }

  /**
   * 强制触发枚举类的静态初始化块。
   *
   * <p>通过 {@link Class#forName(String)} 确保枚举类的 static 代码块被执行（通常用于注册缓存）。 该方法线程安全，使用双重检查锁避免重复触发。
   *
   * @param <E> 枚举类型
   * @param clazz 需要初始化的枚举类
   * @throws IllegalStateException 如果类加载失败
   */
  private static <E extends Enum<?>> void executeEnumStatic(Class<E> clazz) {
    if (!LOADED.containsKey(clazz)) {
      synchronized (clazz) {
        if (!LOADED.containsKey(clazz)) {
          try {
            // 目的是让枚举类的static块运行，static块没有执行完是会阻塞在此的
            Class.forName(clazz.getName());
            LOADED.put(clazz, true);
          } catch (Exception e) {
            throw new IllegalStateException(e);
          }
        }
      }
    }
  }

  /**
   * 枚举值提取函数式接口。
   *
   * <p>用于 {@link #registerByValue(Class, Enum[], EnumMapping)} 中定义如何从枚举常量获取自定义值。
   *
   * @param <E> 枚举类型
   */
  @FunctionalInterface
  public interface EnumMapping<E extends Enum<?>> {
    Object value(E e);
  }
}
