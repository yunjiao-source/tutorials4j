# 开发问题知识库

> 记录开发过程中反复出现或排查成本较高的系统性问题及解决方法。

## 目录

- 问题4：@Cacheable + 使用流的收集方法toList() 有问题，在从缓存取数据时异常，存数据没有问题
- 问题3：service-system 启动时没有加载toolkit-core包中的banner
- 问题2：集成sa-token-1.46.0+redisson-4.7.0 时会出现异常
- 问题1：Bean 'xxx' of type [yyyy] is not eligible for getting processed by all BeanPostProcessors

### 问题4：@Cacheable + 使用流的收集方法toList() 有问题，在从缓存取数据时异常，存数据没有问题

`DictDataService.findByDictTypeIdAndEnabledIn`方法如下：

```java
  @Cacheable(value = CACHEABLE_DICT_DATA, key = "#dictTypeId")
  public List<DictDataVO> findByDictTypeIdAndEnabledIn(
      String dictTypeId, Set<YesNoEnum> allEnabled) {
    List<DictDataVO> dictDataVOList;

    List<DictDataEntity> dictDataEntities = dictDataRepository.findByDictTypeId(dictTypeId);
    if (CollectionUtils.sizeIsEmpty(allEnabled)) {
      dictDataVOList = DictDataVO.of(dictDataEntities);
    } else {
      dictDataVOList = dictDataEntities.stream()
          .filter(e -> allEnabled.contains(e.getEnabled()))
          .map(DictDataVO::of)
          .toList();
    }

    return dictDataVOList;
  }
```

这段代码在第一次，也就是存入缓存时，正常的；但第二次取缓存时，发生异常：

```text
2026-10-22T21:35:29.843+08:00 DEBUG 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.s.s.CompositeSaFilterAuthStrategy    : 执行 SaFilterAuthStrategy：OptionsMethodSaFilterAuthStrategy
2026-10-22T21:35:29.843+08:00 DEBUG 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.s.s.CompositeSaFilterAuthStrategy    : 执行 SaFilterAuthStrategy：LoggingSaFilterAuthStrategy
2026-10-22T21:35:29.843+08:00 DEBUG 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.satoken.component.SaLogForSlf4j      : 开始认证，path=/basic/dict-type/by-code/xingzqy/dict-data
2026-10-22T21:35:29.843+08:00 DEBUG 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.s.s.CompositeSaFilterAuthStrategy    : 执行 SaFilterAuthStrategy：CheckLoginSaFilterAuthStrategy
2026-10-22T21:35:29.845+08:00 DEBUG 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.s.s.CompositeSaFilterAuthStrategy    : 执行 SaFilterAuthStrategy：SameTokenSaFilterAuthStrategy
Hibernate: select dte1_0.id_,dte1_0.code_,dte1_0.create_by_,dte1_0.create_date_,dte1_0.description_,dte1_0.enabled_,dte1_0.is_reserved_,dte1_0.is_tree_,dte1_0.last_modified_by_,dte1_0.last_modified_date_,dte1_0.name_,dte1_0.parent_id_,dte1_0.rank_,dte1_0.version_ from basic_dict_type dte1_0 where dte1_0.code_=?
2026-10-22T21:35:29.858+08:00 ERROR 6316 --- [service-system] [nio-9803-exec-7] [6ada11212edbeec6be0869e6e7ae8d0e-e973b66ea3030b56] t.t.core.web.HandleException$LogHolder   : 系统端异常 : ProblemDetail [type='null', title='Internal Server Error', status=500, detail='Could not read JSON: Unexpected token (`JsonToken.START_OBJECT`), expected `JsonToken.VALUE_STRING`: need String, Number of Boolean value that contains type id (for subtype of java.lang.Object)
at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #1]', instance='null', properties='{errors=ErrorDetail(timestamp=2026-10-22T13:35:29.858022300Z, code=INTERNAL_SERVER_ERROR, traceId=6ada11212edbeec6be0869e6e7ae8d0e, className=org.springframework.data.redis.serializer.SerializationException, fieldErrors={}, stackTrace=[org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer.deserialize(GenericJacksonJsonRedisSerializer.java:209), org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer.deserialize(GenericJacksonJsonRedisSerializer.java:179), org.springframework.data.redis.serializer.DefaultRedisElementReader.read(DefaultRedisElementReader.java:46), org.springframework.data.redis.serializer.RedisSerializationContext$SerializationPair.read(RedisSerializationContext.java:276), org.springframework.data.redis.cache.RedisCache.deserializeCacheValue(RedisCache.java:375), org.springframework.data.redis.cache.RedisCache.lookup(RedisCache.java:188), org.springframework.cache.support.AbstractValueAdaptingCache.get(AbstractValueAdaptingCache.java:58), org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:78), org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:565), org.springframework.cache.interceptor.CacheAspectSupport.findCachedValue(CacheAspectSupport.java:515), org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:440), org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:406), org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:64), org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179), org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:719), tutorials4j.feature.basic.service.DictDataServiceSpringCGLIB0.findByDictTypeIdAndEnabledIn(<generated>), tutorials4j.feature.basic.controller.DictTypeBasicController.getDictDataById(DictTypeBasicController.java:102), tutorials4j.feature.basic.controller.DictTypeBasicController.getDictDataByCode(DictTypeBasicController.java:110), java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103), java.base/java.lang.reflect.Method.invoke(Method.java:580), org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:252), org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:184), org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117), org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:934), org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:853), org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:86), org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:963), org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:866), org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1000), org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:892), jakarta.servlet.http.HttpServlet.service(HttpServlet.java:633), org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:874), jakarta.servlet.http.HttpServlet.service(HttpServlet.java:723), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:132), org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:59), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), tutorials4j.toolkit.satoken.filter.SecurityContextServletFilter.doFilterInternal(SecurityContextServletFilter.java:68), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), cn.dev33.satoken.filter.SaServletFilter.doFilter(SaServletFilter.java:143), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:70), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:110), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:199), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111), org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:165), org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:77), org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:535), org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115), org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:86), org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:71), org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:347), org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:406), org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:71), org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1307), [org.apache.tomcat.util.net](https://org.apache.tomcat.util.net).NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:2036), [org.apache.tomcat.util.net](https://org.apache.tomcat.util.net).SocketProcessorBase.run(SocketProcessorBase.java:74), org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:949), org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:483), org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:74), java.base/java.lang.Thread.run(Thread.java:1583)], params={})}']
org.springframework.data.redis.serializer.SerializationException: Could not read JSON: Unexpected token (`JsonToken.START_OBJECT`), expected `JsonToken.VALUE_STRING`: need String, Number of Boolean value that contains type id (for subtype of java.lang.Object)
at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #1]
at org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer.deserialize(GenericJacksonJsonRedisSerializer.java:209) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer.deserialize(GenericJacksonJsonRedisSerializer.java:179) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.serializer.DefaultRedisElementReader.read(DefaultRedisElementReader.java:46) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.serializer.RedisSerializationContext$SerializationPair.read(RedisSerializationContext.java:276) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.cache.RedisCache.deserializeCacheValue(RedisCache.java:375) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.cache.RedisCache.lookup(RedisCache.java:188) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.cache.support.AbstractValueAdaptingCache.get(AbstractValueAdaptingCache.java:58) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.AbstractCacheInvoker.doGet(AbstractCacheInvoker.java:78) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.CacheAspectSupport.findInCaches(CacheAspectSupport.java:565) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.CacheAspectSupport.findCachedValue(CacheAspectSupport.java:515) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:440) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.CacheAspectSupport.execute(CacheAspectSupport.java:406) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.cache.interceptor.CacheInterceptor.invoke(CacheInterceptor.java:64) ~[spring-context-7.0.9.jar:7.0.9]
at org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:179) ~[spring-aop-7.0.9.jar:7.0.9]
at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:719) ~[spring-aop-7.0.9.jar:7.0.9]
at tutorials4j.feature.basic.service.DictDataServiceSpringCGLIB0.findByDictTypeIdAndEnabledIn(<generated>) ~[classes/:na]
at tutorials4j.feature.basic.controller.DictTypeBasicController.getDictDataById(DictTypeBasicController.java:102) ~[classes/:na]
at tutorials4j.feature.basic.controller.DictTypeBasicController.getDictDataByCode(DictTypeBasicController.java:110) ~[classes/:na]
at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:252) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:184) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:117) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:934) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:853) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:86) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:963) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:866) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1000) ~[spring-webmvc-7.0.9.jar:7.0.9]
at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:892) ~[spring-webmvc-7.0.9.jar:7.0.9]
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:633) ~[jakarta.servlet-api-6.1.0.jar:6.1.0]
at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:874) ~[spring-webmvc-7.0.9.jar:7.0.9]
at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:723) ~[jakarta.servlet-api-6.1.0.jar:6.1.0]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:132) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:59) ~[tomcat-embed-websocket-11.0.24.jar:11.0.24]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at tutorials4j.toolkit.satoken.filter.SecurityContextServletFilter.doFilterInternal(SecurityContextServletFilter.java:68) ~[classes/:na]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.9.jar:7.0.9]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at cn.dev33.satoken.filter.SaServletFilter.doFilter(SaServletFilter.java:143) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:70) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.9.jar:7.0.9]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.9.jar:7.0.9]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:110) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.9.jar:7.0.9]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:199) ~[spring-web-7.0.9.jar:7.0.9]
at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-7.0.9.jar:7.0.9]
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:111) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:165) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:77) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:535) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:115) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:86) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:71) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:347) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:406) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:71) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1307) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at [org.apache.tomcat.util.net](https://org.apache.tomcat.util.net).NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:2036) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at [org.apache.tomcat.util.net](https://org.apache.tomcat.util.net).SocketProcessorBase.run(SocketProcessorBase.java:74) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:949) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:483) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:74) ~[tomcat-embed-core-11.0.24.jar:11.0.24]
at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: tools.jackson.databind.exc.MismatchedInputException: Unexpected token (`JsonToken.START_OBJECT`), expected `JsonToken.VALUE_STRING`: need String, Number of Boolean value that contains type id (for subtype of java.lang.Object)
at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); byte offset: #1]
at tools.jackson.databind.exc.MismatchedInputException.from(MismatchedInputException.java:63) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.DeserializationContext.wrongTokenException(DeserializationContext.java:2019) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.DeserializationContext.reportWrongTokenException(DeserializationContext.java:1803) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.jsontype.impl.AsArrayTypeDeserializer._locateTypeId(AsArrayTypeDeserializer.java:167) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.jsontype.impl.AsArrayTypeDeserializer._deserialize(AsArrayTypeDeserializer.java:91) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.jsontype.impl.AsArrayTypeDeserializer.deserializeTypedFromArray(AsArrayTypeDeserializer.java:47) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer.deserializeTypedFromAny(AsPropertyTypeDeserializer.java:201) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.deser.jdk.UntypedObjectDeserializerNR.deserializeWithType(UntypedObjectDeserializerNR.java:98) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.deser.impl.TypeWrappedDeserializer.deserialize(TypeWrappedDeserializer.java:72) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.deser.DeserializationContextExt.readRootValue(DeserializationContextExt.java:266) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:2639) ~[jackson-databind-3.1.5.jar:3.1.5]
at tools.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:1698) ~[jackson-databind-3.1.5.jar:3.1.5]
at org.springframework.data.redis.serializer.JacksonObjectReader.lambda$create$0(JacksonObjectReader.java:52) ~[spring-data-redis-4.1.1.jar:4.1.1]
at org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer.deserialize(GenericJacksonJsonRedisSerializer.java:207) ~[spring-data-redis-4.1.1.jar:4.1.1]
... 74 common frames omitted
```

问题原因是流的收集方法toList()返回的是`java.util.Collections.UnmodifiableList<E>`这个类的实例，反序列化时会异常

解决方法：
```java
  @Cacheable(value = CACHEABLE_DICT_DATA, key = "#dictTypeId")
  public List<DictDataVO> findByDictTypeIdAndEnabledIn(
      String dictTypeId, Set<YesNoEnum> allEnabled) {
  
  // 忽略其他代码
  
  
  return new ArrayList<>(dictDataVOList);
  }
```

<details>
<summary>AI 原始回答</summary>
# 根因

`Collections.unmodifiableList()`、`Stream.toList()`（JDK16+，返回 `ImmutableCollections$ListN`）都是 **JDK内部final、无无参构造、不可变List实现类**：

- `Collections$UnmodifiableList/UnmodifiableRandomAccessList`：包装代理，**没有默认构造器**
- `ImmutableCollections$ListN`：final私有内部类，**Jackson无法直接实例化**>
> Jackson3 相比2.x：**默认不再自动支持这些内部不可变List的反序列化**，而且 default typing 做了强安全限制，不能像老版本随便开；JsonMapper是builder、build后不可变

典型报错：

```
InvalidDefinitionException: Cannot construct instance of `java.util.Collections$UnmodifiableList` (no Creators, like default constructor, exist): no default no-arguments constructor found
```

>
> ⚠️ 区分：`Collectors.toList()` 返回 `ArrayList`，默认没问题；`Stream.toList()` / `List.of()` / `List.copyOf()` 才踩坑

## 方案优先级（推荐顺序）

### ✅ 方案1【业务层规避，首选】：POJO字段声明为 `List<T>`，反序列化成可变ArrayList，getter返回不可变视图

**不要把字段类型写成 `Collections$UnmodifiableList` / `ImmutableCollections$ListN`，只面向接口 List**

```
public class Demo {
    private List<String> tags = new ArrayList<>();

    // 序列化对外返回不可变视图
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }
    // 反序列化setter接收普通List，Jackson会填充ArrayList
    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }
}
```

>
> 好处：JSON 永远是普通数组 `["a","b"]`，无类型信息，API/缓存兼容性最好，无安全风险。

### ✅ 方案2【JsonMapper + 自定义Deserializer，兼容返回不可变List】

Jackson3 包名：`tools.jackson.databind`，builder模式

```
import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdContainerDeserializer;
import java.util.*;

// 自定义：读到JSON数组，反序列为 unmodifiableList
public class UnmodifiableListDeserializer extends StdContainerDeserializer<List<?>> {
    public UnmodifiableListDeserializer() {
        super(List.class);
    }

    @Override
    public List<?> deserialize(JsonParser p, DeserializationContext ctxt) {
        List<Object> tmp = ctxt.readValue(p, new TypeReference<ArrayList<Object>>() {});
        return Collections.unmodifiableList(tmp);
    }
}
```

注册（Jackson3 builder写法）

```
JsonMapper mapper = JsonMapper.builder()
        .addModule(SimpleModule.builder()
                .addDeserializer(List.class, new UnmodifiableListDeserializer())
                .build())
        .build();
```

如果只想专门处理 `Stream.toList()` 的 `ImmutableCollections$ListN`，可以单独注册该类型的deserializer；但**ListN是私有类，不能直接import，反射拿到class风险高，不推荐**。

### ✅ 方案3【使用 @JsonCreator 构造器 + List.of / 不可变工厂】

适合不可变Record/Value Object（DDD常用）

```
public record DemoRecord(List<String> tags) {
    @JsonCreator
    public static DemoRecord of(List<String> tags) {
        // 反序列化完成后包装为不可变
        return new DemoRecord(Collections.unmodifiableList(new ArrayList<>(tags)));
    }
}
```

### ❌ 不推荐方案：开启多态TypeInfo / defaultTyping

Jackson3 对 defaultTyping 做严格白名单校验，**很容易引入安全漏洞**；序列化JSON会带上类型标记 `["java.util.Collections$UnmodifiableList", [...]]`，前端/其他语言解析报错，跨系统不兼容。

```
// 不建议业务通用，仅内部可控消息场景谨慎使用
JsonMapper mapper = JsonMapper.builder()
        .polymorphicTypeValidator(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType(List.class)
                        .build()
        )
        .activateDefaultTyping(...)
        .build();
```

### ✅ 方案4【序列化时统一转ArrayList，存储/缓存场景】

缓存（Redis）序列化场景高频踩坑：不要直接序列化 `Stream.toList()` 返回对象，序列化前先转ArrayList

```
// 序列化前转换
List<String> safeList = new ArrayList<>(streamResult.toList());
```

# 补充：Jackson3 关键注意点

1. 包名：`tools.jackson.core:jackson-databind`，注解包依旧 `com.fasterxml.jackson.annotation`
2. `JsonMapper` build() 之后不可修改，所有模块、特性必须在builder阶段配置
3. JDK `ImmutableCollections$ListN` 是**私有final内部类**，Jackson官方默认不提供creator，没有内置支持

# 快速选型建议

- REST接口、对外JSON：**方案1（getter返回不可变视图，底层存ArrayList）**
- DDD不可变值对象、Record：**方案3 @JsonCreator**
- Redis消息、内部序列化必须保留不可变实例：**方案2 自定义Deserializer**
- 简单缓存：**方案4 序列化前转ArrayList**

如果你需要，我可以给一份可直接复制的 **Jackson3 完整配置Bean（Spring Boot）+ 兼容Stream.toList/UnmodifiableList的Module**。
</details>

### 问题3：service-system 启动时没有加载toolkit-core包中的banner

没有加载自定义的banner信息，而是使用默认的

原因是服务启动时，将toolkit-core包放到了spring系统包之后加载，如：

>C:\Java\jdk-21.0.7+6\bin\java.exe -XX:TieredStopAtLevel=1 -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true "-Dmanagement.endpoints.jmx.exposure.include=*" "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA 2023.3.4\lib\idea_rt.jar=62427:C:\Program Files\JetBrains\IntelliJ IDEA 2023.3.4\bin" -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath D:\code\tutorials4j\service\service-system\target\classes;D:\code\tutorials4j\target\classes;D:\repository\common\com\mysql\mysql-connector-j\9.7.0\mysql-connector-j-9.7.0.jar;D:\repository\common\org\postgresql\postgresql\42.7.13\postgresql-42.7.13.jar;D:\repository\common\org\xerial\sqlite-jdbc\3.53.2.1\sqlite-jdbc-3.53.2.1.jar;D:\repository\common\org\springframework\spring-jdbc\7.0.9\spring-jdbc-7.0.9.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-data-jpa\4.1.1\spring-boot-starter-data-jpa-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-jdbc\4.1.1\spring-boot-starter-jdbc-4.1.1.jar;D:\repository\common\com\zaxxer\HikariCP\7.0.2\HikariCP-7.0.2.jar;D:\repository\common\org\springframework\boot\spring-boot-data-jpa\4.1.1\spring-boot-data-jpa-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-hibernate\4.1.1\spring-boot-hibernate-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-jpa\4.1.1\spring-boot-jpa-4.1.1.jar;D:\repository\common\jakarta\persistence\jakarta.persistence-api\3.2.0\jakarta.persistence-api-3.2.0.jar;D:\repository\common\org\hibernate\orm\hibernate-core\7.4.5.Final\hibernate-core-7.4.5.Final.jar;D:\repository\common\jakarta\transaction\jakarta.transaction-api\2.0.1\jakarta.transaction-api-2.0.1.jar;D:\repository\common\org\hibernate\models\hibernate-models\1.1.1\hibernate-models-1.1.1.jar;D:\repository\common\org\glassfish\jaxb\jaxb-runtime\4.0.9\jaxb-runtime-4.0.9.jar;D:\repository\common\org\glassfish\jaxb\jaxb-core\4.0.9\jaxb-core-4.0.9.jar;D:\repository\common\org\eclipse\angus\angus-activation\2.0.3\angus-activation-2.0.3.jar;D:\repository\common\org\glassfish\jaxb\txw2\4.0.9\txw2-4.0.9.jar;D:\repository\common\com\sun\istack\istack-commons-runtime\4.1.2\istack-commons-runtime-4.1.2.jar;D:\repository\common\jakarta\inject\jakarta.inject-api\2.0.1\jakarta.inject-api-2.0.1.jar;D:\repository\common\org\springframework\spring-orm\7.0.9\spring-orm-7.0.9.jar;D:\repository\common\org\springframework\data\spring-data-jpa\4.1.1\spring-data-jpa-4.1.1.jar;D:\repository\common\org\antlr\antlr4-runtime\4.13.2\antlr4-runtime-4.13.2.jar;D:\repository\common\org\springframework\spring-aspects\7.0.9\spring-aspects-7.0.9.jar;D:\repository\common\org\springframework\boot\spring-boot-jdbc\4.1.1\spring-boot-jdbc-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-sql\4.1.1\spring-boot-sql-4.1.1.jar;D:\repository\common\org\springframework\spring-context-support\7.0.9\spring-context-support-7.0.9.jar;D:\repository\common\org\springframework\spring-beans\7.0.9\spring-beans-7.0.9.jar;D:\repository\common\org\springframework\spring-context\7.0.9\spring-context-7.0.9.jar;D:\repository\common\org\springframework\spring-web\7.0.9\spring-web-7.0.9.jar;D:\repository\common\org\springframework\security\spring-security-core\7.1.1\spring-security-core-7.1.1.jar;D:\repository\common\org\springframework\security\spring-security-crypto\7.1.1\spring-security-crypto-7.1.1.jar;D:\repository\common\org\springframework\spring-aop\7.0.9\spring-aop-7.0.9.jar;D:\repository\common\org\springframework\spring-expression\7.0.9\spring-expression-7.0.9.jar;D:\repository\common\org\hibernate\validator\hibernate-validator\9.1.3.Final\hibernate-validator-9.1.3.Final.jar;D:\repository\common\jakarta\validation\jakarta.validation-api\3.1.1\jakarta.validation-api-3.1.1.jar;D:\repository\common\org\jboss\logging\jboss-logging\3.6.3.Final\jboss-logging-3.6.3.Final.jar;D:\repository\common\com\fasterxml\classmate\1.7.3\classmate-1.7.3.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-validation\4.1.1\spring-boot-starter-validation-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-validation\4.1.1\spring-boot-validation-4.1.1.jar;D:\repository\common\org\apache\tomcat\embed\tomcat-embed-el\11.0.24\tomcat-embed-el-11.0.24.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-aspectj\4.1.1\spring-boot-starter-aspectj-4.1.1.jar;D:\repository\common\org\aspectj\aspectjweaver\1.9.25.1\aspectjweaver-1.9.25.1.jar;D:\repository\common\org\springframework\boot\spring-boot-configuration-processor\4.1.1\spring-boot-configuration-processor-4.1.1.jar;D:\repository\common\org\apache\commons\commons-lang3\3.20.0\commons-lang3-3.20.0.jar;D:\repository\common\org\apache\commons\commons-collections4\4.4\commons-collections4-4.4.jar;D:\repository\common\com\google\guava\guava\33.1.0-jre\guava-33.1.0-jre.jar;D:\repository\common\com\google\guava\failureaccess\1.0.2\failureaccess-1.0.2.jar;D:\repository\common\com\google\guava\listenablefuture\9999.0-empty-to-avoid-conflict-with-guava\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;D:\repository\common\com\google\code\findbugs\jsr305\3.0.2\jsr305-3.0.2.jar;D:\repository\common\org\checkerframework\checker-qual\3.42.0\checker-qual-3.42.0.jar;D:\repository\common\com\google\errorprone\error_prone_annotations\2.26.1\error_prone_annotations-2.26.1.jar;D:\repository\common\com\google\j2objc\j2objc-annotations\3.0.0\j2objc-annotations-3.0.0.jar;D:\repository\common\jakarta\servlet\jakarta.servlet-api\6.1.0\jakarta.servlet-api-6.1.0.jar;D:\repository\common\cn\hutool\hutool-core\5.8.38\hutool-core-5.8.38.jar;D:\repository\common\cn\hutool\hutool-http\5.8.38\hutool-http-5.8.38.jar;D:\repository\common\cn\hutool\hutool-extra\5.8.38\hutool-extra-5.8.38.jar;D:\repository\common\cn\hutool\hutool-setting\5.8.38\hutool-setting-5.8.38.jar;D:\repository\common\cn\hutool\hutool-log\5.8.38\hutool-log-5.8.38.jar;D:\repository\common\io\micrometer\micrometer-tracing\1.7.1\micrometer-tracing-1.7.1.jar;D:\repository\common\io\micrometer\context-propagation\1.2.1\context-propagation-1.2.1.jar;D:\repository\common\aopalliance\aopalliance\1.0\aopalliance-1.0.jar;D:\repository\common\org\springframework\cloud\spring-cloud-bus\5.0.3\spring-cloud-bus-5.0.3.jar;D:\repository\common\org\springframework\integration\spring-integration-core\7.1.1\spring-integration-core-7.1.1.jar;D:\repository\common\org\springframework\spring-messaging\7.0.9\spring-messaging-7.0.9.jar;D:\repository\common\org\springframework\spring-tx\7.0.9\spring-tx-7.0.9.jar;D:\repository\common\org\springframework\cloud\spring-cloud-starter-bus-amqp\5.0.3\spring-cloud-starter-bus-amqp-5.0.3.jar;D:\repository\common\org\springframework\cloud\spring-cloud-starter-stream-rabbit\5.0.3\spring-cloud-starter-stream-rabbit-5.0.3.jar;D:\repository\common\org\springframework\cloud\spring-cloud-stream-binder-rabbit\5.0.3\spring-cloud-stream-binder-rabbit-5.0.3.jar;D:\repository\common\org\springframework\cloud\spring-cloud-stream-binder-rabbit-core\5.0.3\spring-cloud-stream-binder-rabbit-core-5.0.3.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-webflux\4.1.1\spring-boot-starter-webflux-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-reactor-netty\4.1.1\spring-boot-starter-reactor-netty-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-reactor-netty\4.1.1\spring-boot-reactor-netty-4.1.1.jar;D:\repository\common\io\projectreactor\netty\reactor-netty-http\1.3.7\reactor-netty-http-1.3.7.jar;D:\repository\common\io\netty\netty-codec-http\4.2.17.Final\netty-codec-http-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-http2\4.2.17.Final\netty-codec-http2-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-http3\4.2.17.Final\netty-codec-http3-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-classes-quic\4.2.17.Final\netty-codec-classes-quic-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-native-quic\4.2.17.Final\netty-codec-native-quic-4.2.17.Final-linux-x86_64.jar;D:\repository\common\io\netty\netty-codec-native-quic\4.2.17.Final\netty-codec-native-quic-4.2.17.Final-linux-aarch_64.jar;D:\repository\common\io\netty\netty-codec-native-quic\4.2.17.Final\netty-codec-native-quic-4.2.17.Final-osx-x86_64.jar;D:\repository\common\io\netty\netty-codec-native-quic\4.2.17.Final\netty-codec-native-quic-4.2.17.Final-osx-aarch_64.jar;D:\repository\common\io\netty\netty-codec-native-quic\4.2.17.Final\netty-codec-native-quic-4.2.17.Final-windows-x86_64.jar;D:\repository\common\io\netty\netty-resolver-dns-native-macos\4.2.17.Final\netty-resolver-dns-native-macos-4.2.17.Final-osx-x86_64.jar;D:\repository\common\io\netty\netty-resolver-dns-classes-macos\4.2.17.Final\netty-resolver-dns-classes-macos-4.2.17.Final.jar;D:\repository\common\io\projectreactor\netty\reactor-netty-core\1.3.7\reactor-netty-core-1.3.7.jar;D:\repository\common\io\netty\netty-handler-proxy\4.2.17.Final\netty-handler-proxy-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-socks\4.2.17.Final\netty-codec-socks-4.2.17.Final.jar;D:\repository\common\org\springframework\boot\spring-boot-webflux\4.1.1\spring-boot-webflux-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-http-codec\4.1.1\spring-boot-http-codec-4.1.1.jar;D:\repository\common\org\springframework\spring-webflux\7.0.9\spring-webflux-7.0.9.jar;D:\repository\common\org\springframework\cloud\spring-cloud-function-context\5.0.4\spring-cloud-function-context-5.0.4.jar;D:\repository\common\org\springframework\boot\spring-boot-restclient\4.1.1\spring-boot-restclient-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-http-client\4.1.1\spring-boot-http-client-4.1.1.jar;D:\repository\common\org\springframework\cloud\spring-cloud-function-core\5.0.4\spring-cloud-function-core-5.0.4.jar;D:\repository\common\tools\jackson\datatype\jackson-datatype-joda\3.1.5\jackson-datatype-joda-3.1.5.jar;D:\repository\common\joda-time\joda-time\2.12.7\joda-time-2.12.7.jar;D:\repository\common\org\springframework\boot\spring-boot-amqp\4.1.1\spring-boot-amqp-4.1.1.jar;D:\repository\common\org\springframework\amqp\spring-rabbit\4.1.1\spring-rabbit-4.1.1.jar;D:\repository\common\org\springframework\amqp\spring-amqp\4.1.1\spring-amqp-4.1.1.jar;D:\repository\common\com\rabbitmq\amqp-client\5.30.0\amqp-client-5.30.0.jar;D:\repository\common\io\netty\netty-codec\4.2.17.Final\netty-codec-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-compression\4.2.17.Final\netty-codec-compression-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-protobuf\4.2.17.Final\netty-codec-protobuf-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-marshalling\4.2.17.Final\netty-codec-marshalling-4.2.17.Final.jar;D:\repository\common\org\springframework\cloud\spring-cloud-stream\5.0.3\spring-cloud-stream-5.0.3.jar;D:\repository\common\org\springframework\boot\spring-boot-integration\4.1.1\spring-boot-integration-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-amqp\4.1.1\spring-boot-starter-amqp-4.1.1.jar;D:\repository\common\org\springframework\integration\spring-integration-amqp\7.1.1\spring-integration-amqp-7.1.1.jar;D:\repository\common\org\springframework\amqp\spring-rabbitmq-client\4.1.1\spring-rabbitmq-client-4.1.1.jar;D:\repository\common\com\rabbitmq\client\amqp-client\1.1.0\amqp-client-1.1.0.jar;D:\repository\common\io\netty\netty-transport-native-epoll\4.2.17.Final\netty-transport-native-epoll-4.2.17.Final-linux-x86_64.jar;D:\repository\common\io\netty\netty-transport-classes-epoll\4.2.17.Final\netty-transport-classes-epoll-4.2.17.Final.jar;D:\repository\common\org\springframework\integration\spring-integration-jmx\7.1.1\spring-integration-jmx-7.1.1.jar;D:\repository\common\org\springdoc\springdoc-openapi-starter-webmvc-ui\3.1.1\springdoc-openapi-starter-webmvc-ui-3.1.1.jar;D:\repository\common\org\springdoc\springdoc-openapi-starter-webmvc-api\3.1.1\springdoc-openapi-starter-webmvc-api-3.1.1.jar;D:\repository\common\org\springdoc\springdoc-openapi-starter-common\3.1.1\springdoc-openapi-starter-common-3.1.1.jar;D:\repository\common\io\swagger\core\v3\swagger-core-jakarta\2.2.55\swagger-core-jakarta-2.2.55.jar;D:\repository\common\io\swagger\core\v3\swagger-annotations-jakarta\2.2.55\swagger-annotations-jakarta-2.2.55.jar;D:\repository\common\io\swagger\core\v3\swagger-models-jakarta\2.2.55\swagger-models-jakarta-2.2.55.jar;D:\repository\common\com\fasterxml\jackson\dataformat\jackson-dataformat-yaml\2.21.5\jackson-dataformat-yaml-2.21.5.jar;D:\repository\common\com\fasterxml\jackson\datatype\jackson-datatype-jsr310\2.21.5\jackson-datatype-jsr310-2.21.5.jar;D:\repository\common\org\springframework\boot\spring-boot-web-server\4.1.1\spring-boot-web-server-4.1.1.jar;D:\repository\common\org\webjars\swagger-ui\5.32.14\swagger-ui-5.32.14.jar;D:\repository\common\org\webjars\webjars-locator-lite\1.1.4\webjars-locator-lite-1.1.4.jar;D:\repository\common\org\springframework\spring-webmvc\7.0.9\spring-webmvc-7.0.9.jar;D:\repository\common\cn\dev33\sa-token-core\1.46.0\sa-token-core-1.46.0.jar;D:\repository\common\cn\dev33\sa-token-jackson3\1.46.0\sa-token-jackson3-1.46.0.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-cache\4.1.1\spring-boot-starter-cache-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-cache\4.1.1\spring-boot-cache-4.1.1.jar;D:\repository\common\com\github\ben-manes\caffeine\caffeine\3.2.4\caffeine-3.2.4.jar;D:\repository\common\com\google\code\gson\gson\2.13.2\gson-2.13.2.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-json\4.1.1\spring-boot-starter-json-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-data-redis-reactive\4.1.1\spring-boot-starter-data-redis-reactive-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-data-redis\4.1.1\spring-boot-data-redis-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-data-commons\4.1.1\spring-boot-data-commons-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-persistence\4.1.1\spring-boot-persistence-4.1.1.jar;D:\repository\common\org\springframework\data\spring-data-commons\4.1.1\spring-data-commons-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-transaction\4.1.1\spring-boot-transaction-4.1.1.jar;D:\repository\common\io\lettuce\lettuce-core\7.5.2.RELEASE\lettuce-core-7.5.2.RELEASE.jar;D:\repository\common\redis\clients\authentication\redis-authx-core\0.1.1-beta2\redis-authx-core-0.1.1-beta2.jar;D:\repository\common\io\netty\netty-common\4.2.17.Final\netty-common-4.2.17.Final.jar;D:\repository\common\io\netty\netty-handler\4.2.17.Final\netty-handler-4.2.17.Final.jar;D:\repository\common\io\netty\netty-resolver\4.2.17.Final\netty-resolver-4.2.17.Final.jar;D:\repository\common\io\netty\netty-buffer\4.2.17.Final\netty-buffer-4.2.17.Final.jar;D:\repository\common\io\netty\netty-transport-native-unix-common\4.2.17.Final\netty-transport-native-unix-common-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-base\4.2.17.Final\netty-codec-base-4.2.17.Final.jar;D:\repository\common\io\netty\netty-transport\4.2.17.Final\netty-transport-4.2.17.Final.jar;D:\repository\common\io\netty\netty-resolver-dns\4.2.17.Final\netty-resolver-dns-4.2.17.Final.jar;D:\repository\common\io\netty\netty-codec-dns\4.2.17.Final\netty-codec-dns-4.2.17.Final.jar;D:\repository\common\org\springframework\data\spring-data-redis\4.1.1\spring-data-redis-4.1.1.jar;D:\repository\common\org\springframework\data\spring-data-keyvalue\4.1.1\spring-data-keyvalue-4.1.1.jar;D:\repository\common\org\springframework\spring-oxm\7.0.9\spring-oxm-7.0.9.jar;D:\repository\common\org\springframework\boot\spring-boot-netty\4.1.1\spring-boot-netty-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-reactor\4.1.1\spring-boot-reactor-4.1.1.jar;D:\repository\common\io\projectreactor\reactor-core\3.8.7\reactor-core-3.8.7.jar;D:\repository\common\org\reactivestreams\reactive-streams\1.0.4\reactive-streams-1.0.4.jar;D:\repository\common\cn\dev33\sa-token-spring-boot4-starter\1.46.0\sa-token-spring-boot4-starter-1.46.0.jar;D:\repository\common\cn\dev33\sa-token-spring-boot-webmvc-v3v4-common\1.46.0\sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar;D:\repository\common\cn\dev33\sa-token-jakarta-servlet\1.46.0\sa-token-jakarta-servlet-1.46.0.jar;D:\repository\common\cn\dev33\sa-token-spring-boot-webmvc-reactor-v2v3v4-common\1.46.0\sa-token-spring-boot-webmvc-reactor-v2v3v4-common-1.46.0.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-web\4.1.1\spring-boot-starter-web-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-jackson\4.1.1\spring-boot-starter-jackson-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-jackson\4.1.1\spring-boot-jackson-4.1.1.jar;D:\repository\common\tools\jackson\core\jackson-databind\3.1.5\jackson-databind-3.1.5.jar;D:\repository\common\tools\jackson\core\jackson-core\3.1.5\jackson-core-3.1.5.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-tomcat\4.1.1\spring-boot-starter-tomcat-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-tomcat-runtime\4.1.1\spring-boot-starter-tomcat-runtime-4.1.1.jar;D:\repository\common\org\apache\tomcat\embed\tomcat-embed-core\11.0.24\tomcat-embed-core-11.0.24.jar;D:\repository\common\org\apache\tomcat\embed\tomcat-embed-websocket\11.0.24\tomcat-embed-websocket-11.0.24.jar;D:\repository\common\org\springframework\boot\spring-boot-tomcat\4.1.1\spring-boot-tomcat-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-http-converter\4.1.1\spring-boot-http-converter-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-webmvc\4.1.1\spring-boot-webmvc-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-servlet\4.1.1\spring-boot-servlet-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-actuator\4.1.1\spring-boot-starter-actuator-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-micrometer-metrics\4.1.1\spring-boot-starter-micrometer-metrics-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-micrometer-metrics\4.1.1\spring-boot-micrometer-metrics-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-actuator-autoconfigure\4.1.1\spring-boot-actuator-autoconfigure-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-actuator\4.1.1\spring-boot-actuator-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-health\4.1.1\spring-boot-health-4.1.1.jar;D:\repository\common\io\micrometer\micrometer-observation\1.17.1\micrometer-observation-1.17.1.jar;D:\repository\common\io\micrometer\micrometer-commons\1.17.1\micrometer-commons-1.17.1.jar;D:\repository\common\io\micrometer\micrometer-jakarta9\1.17.1\micrometer-jakarta9-1.17.1.jar;D:\repository\common\io\micrometer\micrometer-registry-prometheus\1.17.1\micrometer-registry-prometheus-1.17.1.jar;D:\repository\common\io\micrometer\micrometer-core\1.17.1\micrometer-core-1.17.1.jar;D:\repository\common\org\hdrhistogram\HdrHistogram\2.2.2\HdrHistogram-2.2.2.jar;D:\repository\common\io\prometheus\prometheus-metrics-core\1.7.0\prometheus-metrics-core-1.7.0.jar;D:\repository\common\io\prometheus\prometheus-metrics-model\1.7.0\prometheus-metrics-model-1.7.0.jar;D:\repository\common\io\prometheus\prometheus-metrics-config\1.7.0\prometheus-metrics-config-1.7.0.jar;D:\repository\common\io\prometheus\prometheus-metrics-tracer-common\1.7.0\prometheus-metrics-tracer-common-1.7.0.jar;D:\repository\common\io\prometheus\prometheus-metrics-exposition-formats\1.7.0\prometheus-metrics-exposition-formats-1.7.0.jar;D:\repository\common\io\prometheus\prometheus-metrics-exposition-textformats\1.7.0\prometheus-metrics-exposition-textformats-1.7.0.jar;D:\repository\common\org\springframework\boot\spring-boot-micrometer-tracing-brave\4.1.1\spring-boot-micrometer-tracing-brave-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot\4.1.1\spring-boot-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-micrometer-observation\4.1.1\spring-boot-micrometer-observation-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-micrometer-tracing\4.1.1\spring-boot-micrometer-tracing-4.1.1.jar;D:\repository\common\io\micrometer\micrometer-tracing-bridge-brave\1.7.1\micrometer-tracing-bridge-brave-1.7.1.jar;D:\repository\common\io\zipkin\brave\brave\6.3.1\brave-6.3.1.jar;D:\repository\common\io\zipkin\brave\brave-context-slf4j\6.3.1\brave-context-slf4j-6.3.1.jar;D:\repository\common\io\zipkin\brave\brave-instrumentation-http\6.3.1\brave-instrumentation-http-6.3.1.jar;D:\repository\common\io\zipkin\aws\brave-propagation-aws\1.4.0\brave-propagation-aws-1.4.0.jar;D:\repository\common\io\zipkin\contrib\brave-propagation-w3c\brave-propagation-tracecontext\0.2.0\brave-propagation-tracecontext-0.2.0.jar;D:\repository\common\com\alibaba\cloud\spring-cloud-starter-alibaba-nacos-discovery\2025.1.0.0\spring-cloud-starter-alibaba-nacos-discovery-2025.1.0.0.jar;D:\repository\common\com\alibaba\cloud\spring-cloud-alibaba-commons\2025.1.0.0\spring-cloud-alibaba-commons-2025.1.0.0.jar;D:\repository\common\com\alibaba\nacos\nacos-client\3.1.1\nacos-client-3.1.1.jar;D:\repository\common\com\alibaba\nacos\nacos-client-basic\3.1.1\nacos-client-basic-3.1.1.jar;D:\repository\common\com\alibaba\nacos\nacos-auth-plugin\3.1.1\nacos-auth-plugin-3.1.1.jar;D:\repository\common\com\alibaba\nacos\nacos-encryption-plugin\3.1.1\nacos-encryption-plugin-3.1.1.jar;D:\repository\common\com\alibaba\nacos\logback-adapter\1.1.4\logback-adapter-1.1.4.jar;D:\repository\common\com\alibaba\nacos\nacos-log4j2-adapter\3.1.1\nacos-log4j2-adapter-3.1.1.jar;D:\repository\common\com\fasterxml\jackson\core\jackson-core\2.21.5\jackson-core-2.21.5.jar;D:\repository\common\com\fasterxml\jackson\core\jackson-databind\2.21.5\jackson-databind-2.21.5.jar;D:\repository\common\com\fasterxml\jackson\core\jackson-annotations\2.21\jackson-annotations-2.21.jar;D:\repository\common\org\apache\httpcomponents\client5\httpclient5\5.6.4\httpclient5-5.6.4.jar;D:\repository\common\org\apache\httpcomponents\core5\httpcore5-h2\5.4.3\httpcore5-h2-5.4.3.jar;D:\repository\common\org\apache\httpcomponents\core5\httpcore5\5.4.3\httpcore5-5.4.3.jar;D:\repository\common\io\prometheus\simpleclient\0.16.0\simpleclient-0.16.0.jar;D:\repository\common\io\prometheus\simpleclient_tracer_otel\0.16.0\simpleclient_tracer_otel-0.16.0.jar;D:\repository\common\io\prometheus\simpleclient_tracer_common\0.16.0\simpleclient_tracer_common-0.16.0.jar;D:\repository\common\io\prometheus\simpleclient_tracer_otel_agent\0.16.0\simpleclient_tracer_otel_agent-0.16.0.jar;D:\repository\common\org\springframework\cloud\spring-cloud-commons\5.0.3\spring-cloud-commons-5.0.3.jar;D:\repository\common\org\springframework\cloud\spring-cloud-context\5.0.3\spring-cloud-context-5.0.3.jar;D:\repository\common\com\alibaba\cloud\spring-cloud-starter-alibaba-nacos-config\2025.1.0.0\spring-cloud-starter-alibaba-nacos-config-2025.1.0.0.jar;D:\repository\common\com\alibaba\cloud\spring-alibaba-nacos-config\2025.1.0.0\spring-alibaba-nacos-config-2025.1.0.0.jar;D:\repository\common\jakarta\annotation\jakarta.annotation-api\3.0.0\jakarta.annotation-api-3.0.0.jar;D:\repository\common\org\springframework\cloud\spring-cloud-starter-openfeign\5.0.3\spring-cloud-starter-openfeign-5.0.3.jar;D:\repository\common\org\springframework\cloud\spring-cloud-starter\5.0.3\spring-cloud-starter-5.0.3.jar;D:\repository\common\org\bouncycastle\bcprov-jdk18on\1.85.2\bcprov-jdk18on-1.85.2.jar;D:\repository\common\org\springframework\cloud\spring-cloud-openfeign-core\5.0.3\spring-cloud-openfeign-core-5.0.3.jar;D:\repository\common\io\github\openfeign\feign-form-spring\13.6.1\feign-form-spring-13.6.1.jar;D:\repository\common\org\apache\commons\commons-text\1.14.0\commons-text-1.14.0.jar;D:\repository\common\io\github\openfeign\feign-form\13.6.1\feign-form-13.6.1.jar;D:\repository\common\commons-fileupload\commons-fileupload\1.6.0\commons-fileupload-1.6.0.jar;D:\repository\common\io\github\openfeign\feign-core\13.6.1\feign-core-13.6.1.jar;D:\repository\common\io\github\openfeign\feign-slf4j\13.6.1\feign-slf4j-13.6.1.jar;D:\repository\common\org\slf4j\slf4j-api\2.0.18\slf4j-api-2.0.18.jar;D:\repository\common\org\projectlombok\lombok\1.18.46\lombok-1.18.46.jar;D:\repository\common\org\springframework\boot\spring-boot-starter\4.1.1\spring-boot-starter-4.1.1.jar;D:\repository\common\org\springframework\boot\spring-boot-starter-logging\4.1.1\spring-boot-starter-logging-4.1.1.jar;D:\repository\common\ch\qos\logback\logback-classic\1.5.38\logback-classic-1.5.38.jar;D:\repository\common\ch\qos\logback\logback-core\1.5.38\logback-core-1.5.38.jar;D:\repository\common\org\apache\logging\log4j\log4j-to-slf4j\2.25.5\log4j-to-slf4j-2.25.5.jar;D:\repository\common\org\apache\logging\log4j\log4j-api\2.25.5\log4j-api-2.25.5.jar;D:\repository\common\org\slf4j\jul-to-slf4j\2.0.18\jul-to-slf4j-2.0.18.jar;D:\repository\common\org\springframework\boot\spring-boot-autoconfigure\4.1.1\spring-boot-autoconfigure-4.1.1.jar;D:\repository\common\org\yaml\snakeyaml\2.6\snakeyaml-2.6.jar;D:\repository\common\jakarta\xml\bind\jakarta.xml.bind-api\4.0.5\jakarta.xml.bind-api-4.0.5.jar;D:\repository\common\jakarta\activation\jakarta.activation-api\2.1.4\jakarta.activation-api-2.1.4.jar;D:\repository\common\net\bytebuddy\byte-buddy\1.18.11\byte-buddy-1.18.11.jar;D:\repository\common\org\springframework\spring-core\7.0.9\spring-core-7.0.9.jar;D:\repository\common\commons-logging\commons-logging\1.3.6\commons-logging-1.3.6.jar;D:\repository\common\org\jspecify\jspecify\1.0.1\jspecify-1.0.1.jar tutorials4j.service.system.SystemServiceApplication


解决方法

在assembly-resource-service-starter项目pom中，显示依赖toolkit-core包

```xml

    <dependencies>
        <dependency>
            <groupId>yunjiao-source.tutorials4j.toolkit</groupId>
            <artifactId>toolkit-core</artifactId>
            <version>${revision}</version>
        </dependency>
      
      
      <!-- 其他包不变 -->
    </dependencies>  

```


### 问题2：集成sa-token-1.46.0+redisson-4.7.0 时会出现异常

```text
org.springframework.dao.InvalidDataAccessApiUsageException: ERR invalid expire time in 'set' command. channel: [id: 0x18116530, L:/127.0.0.1:52893 - R:localhost/127.0.0.1:6379] command: (SET), params: [[115, 97, 45, 116, 111, 107, 101, 110, 58, 108, ...], [123, 34, 64, 99, 108, 97, 115, 115, 34, 58, ...], PX, -2000, XX], promise: java.util.concurrent.CompletableFuture@562efc1e[Not completed, 1 dependents]
	at org.redisson.spring.data.connection.RedissonExceptionConverter.convert(RedissonExceptionConverter.java:52) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.redisson.spring.data.connection.RedissonExceptionConverter.convert(RedissonExceptionConverter.java:35) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.springframework.data.redis.PassThroughExceptionTranslationStrategy.translate(PassThroughExceptionTranslationStrategy.java:40) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at org.redisson.spring.data.connection.RedissonConnection.transform(RedissonConnection.java:224) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.redisson.spring.data.connection.RedissonConnection.syncFuture(RedissonConnection.java:219) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.redisson.spring.data.connection.RedissonConnection.sync(RedissonConnection.java:390) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.redisson.spring.data.connection.RedissonConnection.write(RedissonConnection.java:772) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.redisson.spring.data.connection.RedissonConnection.set(RedissonConnection.java:565) ~[redisson-spring-data-35-4.2.0.jar:4.2.0]
	at org.springframework.data.redis.connection.DefaultStringRedisConnection.set(DefaultStringRedisConnection.java:780) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
	at org.springframework.data.redis.core.CloseSuppressingInvocationHandler.invoke(CloseSuppressingInvocationHandler.java:61) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at jdk.proxy2/jdk.proxy2.$Proxy128.set(Unknown Source) ~[na:na]
	at cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate.lambda$setStringAndKeepTTL$1(SaTokenDaoForRedisTemplate.java:177) ~[sa-token-redis-template-1.46.0.jar:na]
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:414) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:381) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:368) ~[spring-data-redis-3.5.13.jar:3.5.13]
	at cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate.setStringAndKeepTTL(SaTokenDaoForRedisTemplate.java:176) ~[sa-token-redis-template-1.46.0.jar:na]
	at cn.dev33.satoken.dao.SaTokenDaoForRedisTemplate.update(SaTokenDaoForRedisTemplate.java:108) ~[sa-token-redis-template-1.46.0.jar:na]
	at cn.dev33.satoken.dao.auto.SaTokenDaoByObjectFollowString.updateObject(SaTokenDaoByObjectFollowString.java:74) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject.updateSession(SaTokenDaoBySessionFollowObject.java:55) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.session.SaSession.update(SaSession.java:412) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.session.SaSession.addTerminal(SaSession.java:343) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.stp.StpLogic.createLoginSession(StpLogic.java:507) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.stp.StpLogic.login(StpLogic.java:465) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.stp.StpLogic.login(StpLogic.java:424) ~[sa-token-core-1.46.0.jar:na]
	at cn.dev33.satoken.stp.StpUtil.login(StpUtil.java:176) ~[sa-token-core-1.46.0.jar:na]
	at tutorials4j.framework.examples.satoken.simple.TestController.login(TestController.java:35) ~[classes/:na]
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103) ~[na:na]
	at java.base/java.lang.reflect.Method.invoke(Method.java:580) ~[na:na]
	at org.springframework.web.method.support.InvocableHandlerMethod.doInvoke(InvocableHandlerMethod.java:258) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:191) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564) ~[tomcat-embed-core-10.1.55.jar:6.0]
	at org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885) ~[spring-webmvc-6.2.19.jar:6.2.19]
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658) ~[tomcat-embed-core-10.1.55.jar:6.0]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51) ~[tomcat-embed-websocket-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at cn.dev33.satoken.filter.SaServletFilter.doFilter(SaServletFilter.java:143) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:70) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40) ~[sa-token-spring-boot-webmvc-v3v4-common-1.46.0.jar:na]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.19.jar:6.2.19]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.19.jar:6.2.19]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.springframework.session.web.http.SessionRepositoryFilter.doFilterInternal(SessionRepositoryFilter.java:142) ~[spring-session-core-3.5.7.jar:3.5.7]
	at org.springframework.session.web.http.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:82) ~[spring-session-core-3.5.7.jar:3.5.7]
	at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:362) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:278) ~[spring-web-6.2.19.jar:6.2.19]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:110) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.19.jar:6.2.19]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201) ~[spring-web-6.2.19.jar:6.2.19]
	at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116) ~[spring-web-6.2.19.jar:6.2.19]
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:165) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:88) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:492) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:113) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:83) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:72) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1272) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1797) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:973) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:491) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63) ~[tomcat-embed-core-10.1.55.jar:10.1.55]
	at java.base/java.lang.Thread.run(Thread.java:1583) ~[na:na]
Caused by: org.redisson.client.RedisException: ERR invalid expire time in 'set' command. channel: [id: 0x18116530, L:/127.0.0.1:52893 - R:localhost/127.0.0.1:6379] command: (SET), params: [[115, 97, 45, 116, 111, 107, 101, 110, 58, 108, ...], [123, 34, 64, 99, 108, 97, 115, 115, 34, 58, ...], PX, -2000, XX], promise: java.util.concurrent.CompletableFuture@562efc1e[Not completed, 1 dependents]
	at org.redisson.client.handler.CommandDecoder.decode(CommandDecoder.java:439) ~[redisson-4.2.0.jar:4.2.0]
	at org.redisson.client.handler.CommandDecoder.decodeCommand(CommandDecoder.java:220) ~[redisson-4.2.0.jar:4.2.0]
	at org.redisson.client.handler.CommandDecoder.decode(CommandDecoder.java:148) ~[redisson-4.2.0.jar:4.2.0]
	at org.redisson.client.handler.CommandDecoder.decode(CommandDecoder.java:124) ~[redisson-4.2.0.jar:4.2.0]
	at io.netty.handler.codec.ByteToMessageDecoder.decodeRemovalReentryProtection(ByteToMessageDecoder.java:545) ~[netty-codec-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.handler.codec.ReplayingDecoder.callDecode(ReplayingDecoder.java:366) ~[netty-codec-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:296) ~[netty-codec-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:444) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:412) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1357) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:440) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:420) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:868) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:171) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:796) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:732) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:658) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:562) ~[netty-transport-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:998) ~[netty-common-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.135.Final.jar:4.1.135.Final]
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.135.Final.jar:4.1.135.Final]
	... 1 common frames omitted

2026-09-08T08:31:49.867+08:00 ERROR 97092 --- [nio-8080-exec-2] t.f.c.spring.web.BaseExceptionHandler    : 服务器异常: Result(timestamp=2026-09-08T00:31:49.866477Z, message=服务内部异常, path=/.well-known/appspecific/com.chrome.devtools.json, data=null, code=INTERNAL_SERVER_ERROR, traceId=null, error=Result.Error(detail=No static resource .well-known/appspecific/com.chrome.devtools.json., fieldErrors=null, stackTrace=[org.springframework.web.servlet.resource.ResourceHttpRequestHandler.handleRequest(ResourceHttpRequestHandler.java:585), org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter.handle(HttpRequestHandlerAdapter.java:52), org.springframework.web.servlet.DispatcherServlet.doDispatch(DispatcherServlet.java:1089), org.springframework.web.servlet.DispatcherServlet.doService(DispatcherServlet.java:979), org.springframework.web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:1014), org.springframework.web.servlet.FrameworkServlet.doGet(FrameworkServlet.java:903), jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564), org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:885), jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:51), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), cn.dev33.satoken.filter.SaServletFilter.doFilter(SaServletFilter.java:143), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), cn.dev33.satoken.filter.SaFirewallCheckFilterForJakartaServlet.doFilter(SaFirewallCheckFilterForJakartaServlet.java:70), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), cn.dev33.satoken.filter.SaTokenCorsFilterForJakartaServlet.doFilter(SaTokenCorsFilterForJakartaServlet.java:52), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet.doFilter(SaTokenContextFilterForJakartaServlet.java:40), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.springframework.web.filter.RequestContextFilter.doFilterInternal(RequestContextFilter.java:100), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.springframework.web.filter.FormContentFilter.doFilterInternal(FormContentFilter.java:93), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.springframework.session.web.http.SessionRepositoryFilter.doFilterInternal(SessionRepositoryFilter.java:142), org.springframework.session.web.http.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:82), org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:362), org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:278), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.springframework.web.filter.ServerHttpObservationFilter.doFilterInternal(ServerHttpObservationFilter.java:110), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.springframework.web.filter.CharacterEncodingFilter.doFilterInternal(CharacterEncodingFilter.java:201), org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:116), org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:162), org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:138), org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:165), org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:88), org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:492), org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:113), org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:83), org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:72), org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:342), org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:399), org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:63), org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:1272), org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1797), org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:52), org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:973), org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:491), org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:63), java.base/java.lang.Thread.run(Thread.java:1583)], params=null))
```

参考issue：
https://github.com/dromara/Sa-Token/issues/970
https://github.com/redisson/redisson/issues/7308


最终解决方案: 降级

```xml
<dependency>
  <groupId>cn.dev33</groupId>
  <artifactId>sa-token-redis-template</artifactId>
  <version>1.45.0</version>
</dependency>
```


### 问题1：Bean 'xxx' of type [yyyy] is not eligible for getting processed by all BeanPostProcessors

> WARN 16344 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration' of type [tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [projectingArgumentResolverBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE.

<details>
<summary>AI 原始回答</summary>

> WARN 16344 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration' of type [tutorials4j.framework.cache.redis.autoconfigure.RedisCacheConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying). Is this bean getting eagerly injected/applied to a currently created BeanPostProcessor [projectingArgumentResolverBeanPostProcessor]? Check the corresponding BeanPostProcessor declaration and its dependencies/advisors. If this bean does not have to be post-processed, declare it with ROLE_INFRASTRUCTURE. 这是啥问题

这是一个 Spring 容器的常见警告，**本身通常不会导致应用启动失败，但可能影响某些 Bean 的后置处理（比如 AOP 代理、事务增强等）**。

## 问题原因

Spring 中的 `BeanPostProcessor` 是用于对普通 Bean 进行加工（如代理、依赖注入检查等）的特殊 Bean。它们必须在普通 Bean 实例化**之前**就完成初始化，这样才能处理后续所有普通 Bean。

你的日志里：

- **被创建的 Bean**：`RedisCacheConfiguration`（普通配置类 Bean）
- **正在创建的 BeanPostProcessor**：`projectingArgumentResolverBeanPostProcessor`

当 Spring 初始化 `projectingArgumentResolverBeanPostProcessor` 时，发现它直接或间接依赖了 `RedisCacheConfiguration`，于是不得不**提前实例化**这个普通 Bean。而此刻其他一些 `BeanPostProcessor`（例如处理 `@Transactional`、`@Cacheable` 的代理后置处理器）还没有被注册。

**结果**：`RedisCacheConfiguration` 这个 Bean 将**不会经过那些后来才注册的 `BeanPostProcessor` 的处理**。因此它可能无法被 AOP 代理，也无法应用某些增强（比如缓存、异步、事务等），甚至在某些场景下会出现类型不匹配或功能缺失。

## 是否严重？

- 如果 `RedisCacheConfiguration` **不需要被 AOP 增强**（比如没有 `@Transactional`、`@Cacheable` 等方法拦截需求），那么**这个警告可以忽略**，不影响业务。
- 如果它需要被代理（比如你在它内部方法上加了切面注解），则这些增强**可能失效**。

## 如何解决？

### 1. 标记为基础设施（最简单、推荐）
如果你确定这个 Bean 不需要后置处理，可以在其定义处添加 `@Role(BeanDefinition.ROLE_INFRASTRUCTURE)`（或通过 `BeanDefinitionBuilder` 设置），Spring 就不会再报警告：
```java
@Bean
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public RedisCacheConfiguration redisCacheConfiguration() { ... }
```

### 2. 延迟注入
修改 `projectingArgumentResolverBeanPostProcessor`，让它不要直接依赖 `RedisCacheConfiguration`。比如改用 `ObjectProvider` 或 `@Lazy`：
```java
public class ProjectingArgumentResolverBeanPostProcessor implements BeanPostProcessor {
    @Lazy
    @Autowired
    private RedisCacheConfiguration redisCacheConfiguration;
    // ...
}
```

### 3. 拆分配置类
把 `RedisCacheConfiguration` 的创建和 `BeanPostProcessor` 的创建放到不同的 `@Configuration` 类中，并且确保 `BeanPostProcessor` 所在的配置类不直接依赖普通配置 Bean。可以通过 `@AutoConfigureAfter` / `@AutoConfigureBefore` 调整顺序。

### 4. 检查自定义 BeanPostProcessor
如果 `projectingArgumentResolverBeanPostProcessor` 是你自己写的，检查它是否真的需要依赖 `RedisCacheConfiguration`。很多时候 `BeanPostProcessor` 应该尽量无状态，或者只依赖其他 `BeanPostProcessor` 或基础设施 Bean（如 `ApplicationContext`）。

## 快速判断

你可以先观察应用运行时，`RedisCacheConfiguration` 中定义的方法（如缓存管理器、序列化器等）是否正常工作。如果缓存功能正常，且没有与代理相关的异常，那就不用处理这个警告。

如果需要严格消除警告，使用 **方案1** 是最简单且安全的。

> public class SimpleCachingConfigurer implements CachingConfigurer {
private final RedisCacheManagerCreator redisCacheManagerCreator;
@Override
public CacheManager cacheManager() {
return redisCacheManagerCreator.getInstance();
}
} 测试了下，是因为实现CachingConfigurer 接口造成的，不知道为啥

你的测试结论很关键：**一旦 `SimpleCachingConfigurer` 实现了 `CachingConfigurer` 接口，警告就出现**；去掉接口警告消失。这说明问题的根源确实是 `CachingConfigurer` 触发了 Spring 缓存基础架构的早期初始化行为。

## 为什么会这样？

### 1. `CachingConfigurer` 的特殊作用
`CachingConfigurer` 是 Spring Cache 抽象中的一个配置接口，允许你自定义：
- `CacheManager`
- `KeyGenerator`
- `CacheResolver`
- `CacheErrorHandler`

当 Spring 容器中出现了 `CachingConfigurer` 的 Bean 时，**缓存相关的 `BeanPostProcessor`（例如 `CacheAnnotationBeanPostProcessor`）会在启动阶段就主动获取这个 Bean**，以便在后续处理 `@Cacheable`、`@CacheEvict` 等注解时使用你提供的定制组件。

### 2. 早期获取导致依赖链提前实例化
- `CacheAnnotationBeanPostProcessor` 本身是一个 `BeanPostProcessor`。它需要在普通 Bean 实例化之前就完成初始化。
- 在初始化过程中，它会查找容器中所有的 `CachingConfigurer` 实现，并调用其方法（如 `cacheManager()`）来获得 `CacheManager`。
- 这导致你的 `SimpleCachingConfigurer` Bean **被强制提前创建**（早于其他许多普通 Bean）。
- 进一步地，`SimpleCachingConfigurer` 依赖的 `RedisCacheManagerCreator` 也被提前创建，而 `RedisCacheManagerCreator` 又依赖了 `RedisCacheConfiguration`（就是你之前警告中的那个 Bean）。

### 3. 与 `projectingArgumentResolverBeanPostProcessor` 的冲突
你的日志中还提到了 `projectingArgumentResolverBeanPostProcessor`。很可能它也是另一个 `BeanPostProcessor`，并且在它被初始化的过程中，Spring 的上下文状态还处于“某些 `BeanPostProcessor` 尚未完全注册”的阶段。  
当 `projectingArgumentResolverBeanPostProcessor` 创建时，它直接或间接依赖了 `RedisCacheConfiguration`（或者依赖了依赖它的组件），导致 `RedisCacheConfiguration` 被提前创建，而此时其他一些 `BeanPostProcessor`（例如用于 AOP 代理的那些）还没准备好。于是出现了你看到的警告。

**简单来说**：`CachingConfigurer` 的存在，让 Spring 在 `BeanPostProcessor` 启动阶段就“急切地”创建了你的配置类及其整个依赖链，包括那个 `RedisCacheConfiguration`。

## 解决方案

既然你需要实现 `CachingConfigurer` 来自定义缓存行为，又想消除这个警告（或者避免潜在的 AOP 失效），可以采用下面几种方式：

### 1. 将 `CachingConfigurer` 标记为基础设施角色（推荐）
告诉 Spring 这个 Bean 不需要被其他 `BeanPostProcessor` 处理：
```java
@Bean
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public SimpleCachingConfigurer cachingConfigurer(RedisCacheManagerCreator creator) {
    return new SimpleCachingConfigurer(creator);
}
```
或者在 `SimpleCachingConfigurer` 类上使用 `@Role`（Spring 5.2+）。

### 2. 将 `CacheManager` 的创建与 `CachingConfigurer` 分离
不要直接让 `CachingConfigurer` 依赖 `RedisCacheManagerCreator`，而是通过 `@Lazy` 或 `ObjectProvider` 延迟获取：
```java
public class SimpleCachingConfigurer implements CachingConfigurer {
    private final ObjectProvider<RedisCacheManagerCreator> creatorProvider;

    public SimpleCachingConfigurer(ObjectProvider<RedisCacheManagerCreator> creatorProvider) {
        this.creatorProvider = creatorProvider;
    }

    @Override
    public CacheManager cacheManager() {
        return creatorProvider.getObject().getInstance();
    }
}
```
这样可以避免在 `CachingConfigurer` 实例化时就立即创建 `RedisCacheManagerCreator` 和 `RedisCacheConfiguration`。

### 3. 自行声明 `CacheManager` Bean，而非通过 `CachingConfigurer`
如果你仅仅是为了提供 `CacheManager`，可以直接用 `@Bean` 方法声明 `CacheManager`，而不用实现 `CachingConfigurer`：
```java
@Configuration
public class MyCacheConfig {
    @Bean
    public CacheManager cacheManager(RedisCacheManagerCreator creator) {
        return creator.getInstance();
    }
}
```
Spring 依然能识别这个 `CacheManager` Bean 并使用它。这种方式不会触发 `CachingConfigurer` 的早期加载行为，警告很可能消失。

### 4. 检查 `projectingArgumentResolverBeanPostProcessor` 的依赖
如果你自己写了这个后置处理器，尽量让它不要依赖任何可能被提前创建的普通 Bean。可以使用 `@Lazy` 或 `ObjectProvider`。

## 总结

- **原因**：`CachingConfigurer` 导致 Spring 缓存后置处理器在启动早期就强行实例化你的配置类及其依赖链，造成 `RedisCacheConfiguration` 在部分 `BeanPostProcessor` 尚未注册时就被创建。
- **影响**：通常是良性警告，但如果 `RedisCacheConfiguration` 需要 AOP 代理（比如内部有 `@Transactional` 方法），那些增强会失效。
- **推荐解决**：使用 `@Role(ROLE_INFRASTRUCTURE)` 或改用直接声明 `CacheManager` Bean 的方式。

你可以尝试方案 3，最简单直接，而且通常也是 Spring Boot 缓存配置的标准做法。



</details>