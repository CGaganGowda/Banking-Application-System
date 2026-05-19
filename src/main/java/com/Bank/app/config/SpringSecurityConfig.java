package com.Bank.app.config;

import com.Bank.app.security.JwtAuthenticationEntryPoint;
import com.Bank.app.security.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SpringSecurityConfig(@Lazy JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf((csrf)->csrf.disable())
                .authorizeHttpRequests( (authorize) -> {
//                    authorize.requestMatchers(HttpMethod.POST,"/api/**").hasRole("admin");
//                    authorize.requestMatchers(HttpMethod.PUT,"/api/**").hasRole("admin");
//                    authorize.requestMatchers(HttpMethod.DELETE,"/api/**").hasRole("admin");
//                    authorize.requestMatchers(HttpMethod.PATCH,"/api/**").hasAnyRole("admin","student");
//                    authorize.requestMatchers(HttpMethod.GET,"/api/**").hasAnyRole("admin","user");
                    authorize.requestMatchers("/api/auth/**").permitAll();
                    authorize.anyRequest().authenticated();
                }).httpBasic(Customizer.withDefaults());


        //Un-Authorized users gets exception message from here
        http.exceptionHandling(
                exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        );
        //Authentication-Filter class to execute before spring security filters(UsernamePasswordAuthenticationFilter)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /*
    @Bean
    public UserDetailsService  userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("admin")
                .build();

        UserDetails student = User.builder()
                .username("student")
                .password(passwordEncoder().encode("password"))
                .roles("student")
                .build();

        return new InMemoryUserDetailsManager(admin,student);
    }
     */

}
