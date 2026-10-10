package br.com.autocarehub.infrastructure.config;

import java.util.Properties;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class UseCaseTransactionConfig {
    @Bean
    @org.springframework.context.annotation.Role(
            org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE)
    static Advisor useCaseTransactionAdvisor(BeanFactory beanFactory) {
        var pointcut = new JdkRegexpMethodPointcut();
        pointcut.setPattern("br\\.com\\.autocarehub\\.application\\.usecase\\..*UseCase\\.execute");
        var attributes = new Properties();
        attributes.setProperty("execute", "PROPAGATION_REQUIRED");
        var interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributes(attributes);
        interceptor.setTransactionManagerBeanName("transactionManager");
        interceptor.setBeanFactory(beanFactory);
        return new DefaultPointcutAdvisor(pointcut, interceptor);
    }
}
