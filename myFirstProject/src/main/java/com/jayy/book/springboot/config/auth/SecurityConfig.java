package com.jayy.book.springboot.config.auth;

import com.jayy.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정 활성
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOauth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            // 1. h2-console 화면을 사용하기 위해 해당 옵션을 disable
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    .authorizeRequests() // 2. URL별 권한 설정
                    .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                    .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                    .anyRequest().authenticated()
                .and()
                    .logout()           // 3. logout 기능 설정
                        .logoutSuccessUrl("/")
                .and()
                    .oauth2Login()      // 4. oauth2 로그인 기능 설정
                        .userInfoEndpoint()
                            .userService(customOauth2UserService);
    }
}
