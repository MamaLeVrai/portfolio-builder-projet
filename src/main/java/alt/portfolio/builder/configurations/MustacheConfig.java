package alt.portfolio.builder.configurations;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import com.samskivert.mustache.Mustache;

/**
 * Configuration du moteur de templates Mustache.
 *
 * Par défaut, si une variable est manquante ou nulle dans un template Mustache,
 * le serveur affiche une erreur. Cette configuration change ce comportement :
 * une variable manquante affiche juste une chaîne vide "" au lieu de planter.
 *
 * C'est très pratique quand certains champs sont optionnels (par exemple,
 * la description d'un profil qui peut ne pas encore être renseignée).
 */
@Configuration
public class MustacheConfig {

	/**
	 * Ce "post-processeur" s'applique après la création du compilateur Mustache
	 * pour lui dire : "si tu vois null ou une valeur manquante, affiche juste du vide".
	 */
	static class MustacheCompilerPostProcessor implements BeanPostProcessor {
		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			// On cherche le bean qui compile les templates Mustache
			if (ClassUtils.isAssignable(Mustache.Compiler.class, bean.getClass())
					|| "mustacheCompiler".equals(beanName)) {
				Mustache.Compiler compiler = (Mustache.Compiler) bean;
				// defaultValue("") : valeur manquante → chaîne vide
				// nullValue("") : valeur nulle → chaîne vide
				return compiler.defaultValue("").nullValue("");
			}
			return bean;
		}
	}

	/**
	 * Déclare notre post-processeur comme un bean Spring pour qu'il soit automatiquement actif.
	 */
	@Bean
	public static BeanPostProcessor mutacheHackerBeanPostProcessor() {
		return new MustacheCompilerPostProcessor();
	}
}
