package com.chornyi.spring.boot.thymeleaf.config;

import com.chornyi.spring.boot.thymeleaf.service.UserService;
import com.chornyi.spring.boot.thymeleaf.web.LoggingAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private UserService userService;

    @Autowired
    private LoggingAccessDeniedHandler accessDeniedHandler;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers(
                        "/",
                        "/js/**",
                        "/css/**",
                        "/img/**",
                        "/webjars/**").permitAll()
                    .antMatchers("/h2-console/**").access("hasRole('ADMIN')")
                    .antMatchers("/swagger-ui.html/**").access("hasRole('USER') or hasRole('ADMIN')")
                    .antMatchers("/user/**").fullyAuthenticated()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                    .and()
                .logout()
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")
                    .permitAll()
                    .and()
                    .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler);

        http.csrf().disable();
        http.headers().frameOptions().disable();
    }


    @Bean
    public LdapContextSource contextSource () {
        final ApplicationProperties.Ldap ldap = properties.getLdap();
        LdapContextSource contextSource= new LdapContextSource();
        contextSource.setUrl(ldap.getUrl());
        contextSource.setBase(ldap.getBaseDn());
        contextSource.setUserDn(ldap.getUsername());
        contextSource.setPassword(ldap.getPassword());
        return contextSource;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        final ApplicationProperties.Ldap ldap = properties.getLdap();
        if (ldap.isEnable()) {
            auth.ldapAuthentication()
                .ldapAuthoritiesPopulator(new CustomLdapAuthoritiesPopulator(contextSource(), ""))
                    .contextSource()
                    .url(ldap.getUrl() + ldap.getBaseDn())
                    .managerDn(ldap.getUsername())
                    .managerPassword(ldap.getPassword())
                    .and()
                    .groupSearchBase("ou=Groups")
                    .groupSearchFilter("uniqueMember={0}")
                    .userDnPatterns(ldap.getUserDnPattern());
        } else {
            auth.userDetailsService((UserDetailsService)userService);
        }
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
