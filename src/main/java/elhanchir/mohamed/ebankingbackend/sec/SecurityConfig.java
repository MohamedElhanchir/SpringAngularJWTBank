package elhanchir.mohamed.ebankingbackend.sec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import elhanchir.mohamed.ebankingbackend.sec.services.UserDetailServiceImpl;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;


import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private  PasswordEncoder passwordEncoder;
    private UserDetailServiceImpl userDetailServiceImpl;


    private  String secretKey;

    public SecurityConfig(PasswordEncoder passwordEncoder,UserDetailServiceImpl userDetailServiceImpl) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailServiceImpl = userDetailServiceImpl;
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.secretKey = dotenv.get("SECRET_KEY");
    }


      //  @Bean
        public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
            return new InMemoryUserDetailsManager(
                    User.withUsername("user")
                            .password(passwordEncoder.encode("1234"))
                            .authorities("ROLE_USER")
                            .build(),
                    User.withUsername("admin")
                            .password(passwordEncoder.encode("1234"))
                            .authorities("ROLE_ADMIN","ROLE_USER")
                            .build()
            );
        }
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                   .csrf(AbstractHttpConfigurer::disable)
                    .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/auth/login/**").permitAll()
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/h2-console/**").permitAll()
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .anyRequest().authenticated()
                    )
                   // .httpBasic(Customizer.withDefaults())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));
            return http.build();
        }


    @Bean
    JwtEncoder jwtEncoder(){
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
    }
    @Bean
    JwtDecoder jwtDecoder(){
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "RSA");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
    }

    @Bean
  //  AuthenticationManager authenticationManager(@Qualifier("inMemoryUserDetailsManager") UserDetailsService userDetailsService){
        AuthenticationManager authenticationManager(UserDetailsService userDetailsService){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(daoAuthenticationProvider);
    }


}
