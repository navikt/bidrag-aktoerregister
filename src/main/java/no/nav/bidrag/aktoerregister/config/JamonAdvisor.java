package no.nav.bidrag.aktoerregister.config;

import java.lang.reflect.Method;
import no.nav.bidrag.aktoerregister.api.AktoerregisterController;
import no.nav.bidrag.aktoerregister.service.TPSService;
import no.nav.bidrag.aktoerregister.service.TSSService;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.stereotype.Component;

@Component
public class JamonAdvisor extends AbstractPointcutAdvisor {
  private final StaticMethodMatcherPointcut pointcut =
      new StaticMethodMatcherPointcut() {
        @Override
        public boolean matches(Method method, Class<?> targetClass) {
          return AktoerregisterController.class.isAssignableFrom(targetClass)
              || TSSService.class.isAssignableFrom(targetClass)
              || TPSService.class.isAssignableFrom(targetClass);
        }
      };

  @Override
  public Pointcut getPointcut() {
    return pointcut;
  }

  @Override
  public Advice getAdvice() {
    return new JamonPerformanceMonitorInterceptor(false, true);
  }
}
