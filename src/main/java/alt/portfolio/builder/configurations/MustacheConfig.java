package alt.portfolio.builder.configurations;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.samskivert.mustache.Mustache;

@Configuration
public class MustacheConfig {
	@Bean
	public static BeanPostProcessor mutacheHackerBeanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (ClassUtils.isAssignable(Mustache.Compiler.class, bean.getClass())
						|| "mustacheCompiler".equals(beanName)) {
					Mustache.Compiler compiler = (Mustache.Compiler) bean;
					return compiler.defaultValue("").nullValue("");
				}

				return bean;
			}
		};
	}
}
