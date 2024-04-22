package org.zerock.boardex.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.boardex.security.CustomUserDetailsService;
import org.zerock.boardex.security.handler.Custom403Handler;
import org.zerock.boardex.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;

@Log4j2
@Configuration  //환경설정파일을 만듦
@RequiredArgsConstructor    //final이 붙거나 @NotNull이 붙은 필드의 생성자를 자동 생성해주는 lombok annotation

//권한 설정
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
    @Bean //수동으로 Bean 등록
    //SecurityFilterChain : 스프링 부트에서 지원하는 기본 로그인 화면으로 접속하지 못하게 함.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("******** configure ********");
        //사용자 인증 처리
        http.formLogin().loginPage("/member/login");   //사용자 인증 처리
        //csrf토큰 비활성화(아이디와 비밀번호 사용하여 로그인)
        http.csrf().disable();
        //rememberMe() : 자동로그인
        http.rememberMe()
                //key() : 인증받은 사용자의 정보로 token생성, key값은 임의로 설정함.
                // (token이란 서버가 각각의 사용자를 구별하도록 고유한 정보를 담은 암호화 데이터)
                .key("12345678")
                //tokenRepository() : rememberMe의 토큰 저장소
                .tokenRepository(persistentTokenRepository())
                //userDetailsService() : 유저의 정보를 가져오는 인터페이스
                .userDetailsService(customUserDetailsService)
                //tokenvaliditySeconds() : 생성된 token의 만료시간 (30일)(쿠키)
                .tokenValiditySeconds(60 * 60 * 24 * 30);
        //exceptionHandling() : Error 발생시 후처리 설정
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        //카카오 로그인하기
        http.oauth2Login().loginPage("/member/login")
                .successHandler(authenticationSuccessHandler());

        //로그아웃
        http.logout()
                .logoutUrl("/member/logout")
//                .logoutRequestMatcher(new AntPathRequestMatcher("/member/login?logout"))
                .logoutSuccessUrl("/member/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID");

        return http.build();

    }
    @Bean
    //WebSecurityCustomizer : 적용하지 않을 리소스를 설정
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("****** web configure ******");
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    //PasswordEncoder : 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        //쿠키 정보를 테이블로 저장하도록 설정
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }
    //정식 사용자가 아닌데 게시글 수정하려고 접근했을 때 서버에서 접근 거부하면 예외처리
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    //인증된 사용자의 mid(email)값 가져오기
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
