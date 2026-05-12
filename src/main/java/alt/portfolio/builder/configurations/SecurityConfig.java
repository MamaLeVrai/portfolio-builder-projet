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

	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests((req) -> req
						.requestMatchers(PathPatternRequestMatcher.withDefaults().matcher("/"),
								PathPatternRequestMatcher.withDefaults().matcher("/css/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/js/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/styles/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/register"),
								PathPatternRequestMatcher.withDefaults().matcher("/users/register/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/img/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/profiles/register/**"),
								PathPatternRequestMatcher.withDefaults().matcher("/public/**"))
						.permitAll().anyRequest().authenticated())
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