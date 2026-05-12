package alt.portfolio.builder.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import alt.portfolio.builder.Portfolio1Application;
import alt.portfolio.builder.services.DbProfileService;
import alt.portfolio.builder.services.DbUserServices;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	SecurityConfig(Portfolio1Application portfolio1Application) {
	}

	/**
	 * Définit les règles de sécurité de l'application.
	 *
	 * C'est comme un videur à l'entrée d'une boîte de nuit :
	 * - Certaines pages sont accessibles à tout le monde (liste blanche ci-dessous)
	 * - Toutes les autres pages nécessitent d'être connecté
	 *
	 * Modifié dans Epic 4 (US-027) : ajout de "/public/**" dans la liste blanche
	 * pour que les pages CV et Portfolio publiques soient visibles sans compte.
	 */
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((req) -> req
						.requestMatchers(
								PathPatternRequestMatcher.withDefaults().matcher("/"),               // page d'accueil
								PathPatternRequestMatcher.withDefaults().matcher("/css/**"),          // feuilles de style
								PathPatternRequestMatcher.withDefaults().matcher("/js/**"),           // scripts JS
								PathPatternRequestMatcher.withDefaults().matcher("/styles/**"),       // styles
								PathPatternRequestMatcher.withDefaults().matcher("/register"),        // inscription
								PathPatternRequestMatcher.withDefaults().matcher("/users/register/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/img/**"),          // images statiques
								PathPatternRequestMatcher.withDefaults().matcher("/profiles/register/**"),
								// (Epic 4 - US-027) Pages publiques CV et Portfolio — accessibles sans connexion
								PathPatternRequestMatcher.withDefaults().matcher("/public/**"))
						.permitAll().anyRequest().authenticated()) // tout le reste nécessite d'être connecté
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin((form) -> form.loginPage("/login").defaultSuccessUrl("/profiles/my-profiles", true).permitAll())
				.logout((logout) -> logout.logoutUrl("/logout").logoutSuccessUrl("/login").permitAll());
		return http.build();
	}

	@Primary
	@Bean
	UserDetailsService getUserDetailsService() {
		return new DbUserServices();
	}

	@Primary
	@Bean
	DbProfileService getProfileService() {
		return new DbProfileService();
	}

	@Bean
	PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	DaoAuthenticationProvider authenticationProvider(UserDetailsService userService) {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
		auth.setPasswordEncoder(getPasswordEncoder());
		return auth;
	}
}