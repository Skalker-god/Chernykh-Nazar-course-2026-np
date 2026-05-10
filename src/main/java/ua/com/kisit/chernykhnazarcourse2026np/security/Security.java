package ua.com.kisit.chernykhnazarcourse2026np.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import ua.com.kisit.chernykhnazarcourse2026np.entity.User;
import ua.com.kisit.chernykhnazarcourse2026np.repository.UserRepository;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class Security {

    private static final Logger log = LoggerFactory.getLogger(Security.class);

    private final UserRepository userRepository;

    public Security(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return phone -> {
            User user = userRepository.findByPhone(phone)
                    .orElseThrow(() -> new UsernameNotFoundException("Користувач не знайдений"));
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getPhone())
                    .password(user.getPassword())
                    .authorities(Collections.singletonList(() -> "ROLE_" + user.getRole().name()))
                    .disabled(!user.getIsActive())
                    .build();
        };
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof DisabledException) {
                response.sendRedirect("/login?blocked");
            } else {
                response.sendRedirect("/login?error");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/about", "/search",
                                "/ticket/book/**", "/cart/**", "/ticket/confirm").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/boarding/**").hasAnyRole("CASHIER", "ADMIN")
                        .requestMatchers("/profile/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("phone")
                        .successHandler((request, response, authentication) -> {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            String phone = userDetails.getUsername();
                            User user = userRepository.findByPhone(phone).orElse(null);
                            if (user != null) {
                                request.getSession().setAttribute("user", user);
                            }
                            log.info("ЛОГІН: {} увійшов у систему (IP: {})",
                                    phone, request.getRemoteAddr());
                            response.sendRedirect("/");
                        })
                        .failureHandler(authenticationFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }
}