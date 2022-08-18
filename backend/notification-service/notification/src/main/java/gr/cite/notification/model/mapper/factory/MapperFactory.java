package gr.cite.notification.model.mapper.factory;

import gr.cite.notification.model.mapper.Mapper;
import gr.cite.notification.model.mapper.NotificationEntityPersistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

@Component
@RequestScope
public class MapperFactory {

    private ApplicationContext applicationContext;

    @Autowired
    public MapperFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Mapper findMapper(Class<?> sourceClass, Class<?> targetClass) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Mapper.class));
        Set<BeanDefinition> beanDefinitionSet = scanner.findCandidateComponents("gr.cite.notification");
        for (BeanDefinition beanDefinition: beanDefinitionSet) {
            try {
                Class<?> aSourceClass = (Class<?>) Arrays.stream(Class.forName(beanDefinition.getBeanClassName()).getDeclaredMethods()).filter(method -> method.getName().equals("getSourceClass")).findFirst().get().invoke(null);
                Class<?> aTargetClass = (Class<?>) Arrays.stream(Class.forName(beanDefinition.getBeanClassName()).getDeclaredMethods()).filter(method -> method.getName().equals("getTargetClass")).findFirst().get().invoke(null);
                if (sourceClass.equals(aSourceClass) && targetClass.equals(aTargetClass)) {
                    return (Mapper) applicationContext.getBean(Class.forName(beanDefinition.getBeanClassName()));
                }
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
