package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT;


import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers.BaseDatos;
import edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.modelo.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ConfiguracionSeguridad {
    private final BaseDatos baseDatos;

    public ConfiguracionSeguridad(BaseDatos baseDatos) {
        this.baseDatos = baseDatos;
    }

    @Bean
    public SecurityFilterChain configuracion(HttpSecurity http) throws Exception {

        // Reglas de acceso a rutas
        http.authorizeHttpRequests(authorize -> authorize
                        // Registro y login no requieren estar autenticado
                        .requestMatchers("/pistaPadel/auth/register").permitAll()
                        .requestMatchers("/pistaPadel/auth/login").permitAll()
                        // El resto sí requiere autenticación
                        .anyRequest().authenticated()
                )

                // Configuración del login
                .formLogin(form -> form
                        // URL que Spring usa para procesar el login
                        .loginProcessingUrl("/pistaPadel/auth/login")
                        // 200 OK si login correcto
                        .successHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.OK.value()))
                        // 401 si credenciales incorrectas
                        .failureHandler((request, response, authenticationException) ->
                                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )

                // Configuración del logout
                .logout(logout -> logout
                        // URL para cerrar sesión
                        .logoutUrl("/pistaPadel/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        // 204 No Content si logout correcto
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpStatus.NO_CONTENT.value()))
                )
                // Evita redirección HTML y devuelve 401 si no autenticado
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )
                // Desactivamos CSRF porque estamos haciendo una API REST sin sesiones ni formularios
                // En SSR (aplicaciones web con vistas y cookies) sí se necesita CSRF para evitar ataques
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService usuarios() {

        return email -> {

            Usuario usuario = baseDatos.usuarios().values().stream()
                    .filter(u -> u.email().equalsIgnoreCase(email))
                    .findFirst()
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return User.withDefaultPasswordEncoder()
                    .username(usuario.email())
                    .password(usuario.password())
                    .roles(usuario.rol().name())
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