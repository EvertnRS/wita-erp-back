package org.wita.erp.infra.config.audit.revision;

import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContext implements ApplicationContextAware {

    private static org.springframework.context.ApplicationContext context;

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}

