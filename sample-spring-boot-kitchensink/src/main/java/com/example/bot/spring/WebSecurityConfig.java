package com.example.bot.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/**
	 *  defines which URL paths should be secured and which should not
	 *  Specifically, the "/" and "/home" paths are configured to not require any authentication. All other paths must be authenticated.
	 * */
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
            .antMatchers("/", "/home").permitAll()
            .anyRequest().authenticated() 
            .and()
        .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
        .logout()
            .permitAll();
        http.csrf().disable();
    /* .authorizeRequests()
     * 		.anyRequest().authenticated() -- every request requires the user to be authenticated
     * .formLogin()						  -- form based authentication is supported
     * 		.loginPage("/login")		  -- when authentication is required, redirect the browser to /login && we are in charge of rendering the login page 
     * 									  -- when authentication attempt fails, redirect the browser to /login?error && we are in charge of rendering a failure page
     * 									  -- when we successfully logout, redirect the browser to /login?logout && we are in charge of rendering a logout confirmation page 
     *		.permitAll()				  -- without this, we will get stuck in the login page with an infinit loop
     *									  -- thus we need to instruct Spring Security to allow anyone to access the /login URL
     *									  -- .permitAll() with .formLogin()  allows any access to any URL (i.e. /login and /login?error) associated to formLogin()
     *
     * */	
    }

    /**
     *  Sets up an in-memory user store with a single user;
     *  The user is given a username of "user", a password of "password", and a role of "USER".
     * */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	String username = "user";
    	String pw = "pass";
    	
        auth
            .inMemoryAuthentication()
                .withUser(username).password(pw).roles("USER");
    }
}

/* HELPFUL LINKS: 
 * https://spring.io/guides/gs/securing-web/
 */
