// package tutorials4j.feature.upms.component;
//
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.cloud.bus.BusProperties;
// import org.springframework.context.event.EventListener;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;
// import tutorials4j.feature.bus.event.remote.AuthRemoteEvent;
// import tutorials4j.feature.core.event.local.AuthEvent;
// import tutorials4j.feature.upms.service.AuthHistoryService;
// import tutorials4j.toolkit.core.util.SpringUtils;
//
/// **
// * TODO
// *
// * @author YunJiao Yang
// */
// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class UpmsEventListenerComponent {
//  private final BusProperties busProperties;
//  private final AuthHistoryService authHistoryService;
//
//  @Async
//  @EventListener
//  public void loginEventHandler(AuthEvent authEvent) {
//    if (log.isDebugEnabled()) {
//      log.debug("发布事件：{}", authEvent);
//    }
//
//    authHistoryService.createBy(authEvent);
//
//    AuthRemoteEvent are = new AuthRemoteEvent(this, busProperties.getId(), authEvent);
//    SpringUtils.getApplicationContext().publishEvent(are);
//  }
// }
