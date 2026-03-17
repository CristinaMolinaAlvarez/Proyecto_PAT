package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;

import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelos.Usuario;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.repos.UsuarioRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracionSeguridad {
    private final UsuarioRepo usuarioRepo;

    public ConfiguracionSeguridad(UsuarioRepo usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Bean
    public SecurityFilterChain configuracion(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/pistaPadel/auth/register").permitAll()
                        .requestMatchers("/pistaPadel/auth/login").permitAll()
                        .requestMatchers("/pistaPadel/health").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                )

                // Si no está autenticado, devolver 401 y no redirigir
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value()))
                )

                // Para poder probar con MockMvc usando httpBasic(...)
                .httpBasic(httpBasic -> {})

                // Seguís manteniendo vuestro login propio
                .formLogin(form -> form
                        .loginProcessingUrl("/pistaPadel/auth/login")
                        .successHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value()))
                        .failureHandler((request, response, authenticationException) ->
                                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )

                .logout(logout -> logout
                        .logoutUrl("/pistaPadel/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.NO_CONTENT.value()))
                );

        return http.build();
    }


    //  Queremos que las contraseñas se puedan cifrar, ya que no queremos que se guarden en texto plano
    // BCrypt es el codificador esándar y seguro para passwords en Spring
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService usuarios() {
        return email -> {
            Usuario usuario = usuarioRepo.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));


            //Necesitamos transformar el Usuario de base de datos en un Usuario Spring que entienda para hacer login
            return User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .roles(usuario.getRol().name())
                    .build();
        };

    }
}





/*

// ANTES TENÍAMOS ESTO CON  InMemoryUserDetailsManager
// ESTO ERA PARA USUARIOS FIJOS: ADMIN Y USUARIO
// Ahora quitamos InMemoryUserDetailsManager y conectamos el login directamente con BaseDatos.
public class ConfiguracionSeguridad {
    @Bean
    public SecurityFilterChain configuracion(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                // Desactivamos CSRF porque estamos haciendo una API REST sin sesiones ni formularios
                // En SSR (aplicaciones web con vistas y cookies) sí se necesita CSRF para evitar ataques
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean public UserDetailsService usuarios() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("usuario")
                .password("clave")
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("clave")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}


 */