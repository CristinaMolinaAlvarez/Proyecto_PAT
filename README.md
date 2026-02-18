# Proyecto API REST – Gestión de Pistas de Pádel
Proyecto Final PAT - Patricia Urquijo, Paula Díez, Carla Rodríguez y Cristina Molina

## 1. Descripción general

Este proyecto consiste en el desarrollo de una API REST para la gestión de una aplicación de reservas de pistas de pádel. El objetivo principal ha sido diseñar y construir un backend estructurado, aplicando buenas prácticas en el uso de HTTP, separación de responsabilidades, validación de datos y control de acceso.

La aplicación permite gestionar usuarios, pistas, reservas y disponibilidad. Toda la información se almacena en memoria mediante estructuras HashMap, simulando el comportamiento de una base de datos.

---

## 2. Casos de uso principales

La aplicación cubre los siguientes casos de uso:

- Un usuario puede registrarse en el sistema. Al registrarse se le asigna automáticamente el rol USER.
- Un usuario puede iniciar sesión utilizando su email y contraseña.
- Un usuario autenticado puede consultar su perfil, crear reservas, consultar sus reservas, reprogramarlas o cancelarlas.
- Un administrador (rol ADMIN) puede gestionar pistas, consultar el listado de usuarios y ver todas las reservas del sistema aplicando filtros.
- Cualquier usuario autenticado puede consultar la disponibilidad de pistas por fecha.
- Existe un endpoint de healthcheck para comprobar que el servicio está activo.

---

## 3. Estructura del código y evolución del desarrollo

### Organización por controladores

La API está organizada siguiendo el modelo REST, creando un controlador por cada tipo de recurso:

- UsersController (usuarios y autenticación)
- CourtsController (pistas)
- ReservationsController (reservas)
- AvailabilityController (disponibilidad)
- AdminController (funcionalidad administrativa)
- HealthController (healthcheck)

Cada controlador gestiona un conjunto concreto de endpoints asociados a un recurso específico. Esto permite mantener el código modular, legible y más fácil de mantener.

### Centralización del estado en BaseDatos

En una primera versión, cada controlador utilizaba su propio HashMap para almacenar datos. Esto provocaba inconsistencias, ya que cada controlador mantenía su propio estado independiente.

Para solucionar este problema se creó una clase BaseDatos anotada con @Service que contiene:

- Map de usuarios
- Map de pistas
- Map de reservas

Esta clase se inyecta en los controladores mediante inyección de dependencias. De este modo:

- Todos los controladores comparten el mismo estado.
- Las reservas creadas son visibles desde disponibilidad y administración.
- Se evita duplicación de estructuras de datos.
- Se mejora la coherencia interna de la aplicación.

Este enfoque simula una base de datos centralizada y permite que la aplicación funcione como un sistema integrado.

### Integración entre recursos

A medida que avanzó el desarrollo, se reforzó la integración entre los distintos recursos:

- AvailabilityController consulta directamente las reservas almacenadas en BaseDatos para calcular las franjas libres.
- AdminController reutiliza la información de reservas y aplica filtros por fecha, pista o usuario.
- ReservationsController valida la existencia de pistas y usuarios antes de operar.

Esto refleja correctamente las relaciones entre entidades:

- Un Usuario puede tener muchas Reservas.
- Una Pista puede tener muchas Reservas.
- Cada Reserva pertenece a un único Usuario y a una única Pista.

---

## 4. Autenticación y autorización

### Evolución del modelo de seguridad

En una fase inicial se utilizaron usuarios fijos en memoria únicamente para comprobar el funcionamiento de roles. Posteriormente se implementó el modelo definitivo:

- Los usuarios se almacenan en BaseDatos.
- El login utiliza email y contraseña.
- Spring Security valida credenciales mediante un UserDetailsService personalizado.

### Uso del email como identificador

Se utiliza el email como username porque:

- Es único dentro del sistema.
- Identifica de forma natural al usuario.
- Es el método más habitual de autenticación en aplicaciones reales.

Para facilitar las pruebas en Postman, todos los usuarios hardcodeados en BaseDatos tienen contraseña "1234".

### Gestión de sesión

La autenticación funciona mediante sesión HTTP:

- Al hacer login correcto se genera una cookie JSESSIONID.
- Postman la almacena automáticamente.
- Las siguientes peticiones autenticadas reutilizan esa sesión.
- Al hacer logout se invalida la sesión.

Si se intenta acceder a un endpoint protegido sin estar autenticado, se devuelve 401.  
Si el usuario está autenticado pero no tiene el rol adecuado, se devuelve 403.

Se diferencia claramente entre:

- Autenticación: comprobar la identidad del usuario.
- Autorización: comprobar si tiene permisos para realizar una acción concreta.

---

## 5. Validaciones y reglas de negocio

### Validaciones automáticas

Se utilizan anotaciones como @NotBlank, @Email, @Positive, @Pattern, etc., en los records.  
En los controladores se usa @Valid para activar la validación automática del body.

Cuando un dato no cumple las restricciones definidas:

- Se genera una excepción de validación.
- Se responde con código 400.
- Se incluye información detallada sobre los campos incorrectos.

Esto garantiza que la API no procese datos inconsistentes o incompletos.

### Reglas de negocio

Además de las validaciones estructurales, se implementaron reglas propias del dominio:

- No permitir emails duplicados.
- No permitir reservas en pistas inexistentes.
- No permitir reservas en pistas inactivas.
- No permitir reservar un slot ya ocupado.
- No permitir acceder o modificar recursos inexistentes.
- No permitir que un usuario modifique recursos que no le pertenecen.

En estos casos se lanza una ResponseStatusException con el código adecuado:

- 404 cuando el recurso no existe.
- 409 cuando existe conflicto.
- 403 cuando no hay permisos suficientes.

---

## 6. Manejador global de errores

Se implementó un @ControllerAdvice para centralizar la gestión de excepciones y garantizar que todas las respuestas de error tengan un formato uniforme en JSON.

Se gestionan:

- ResponseStatusException: errores de lógica de negocio.
- MethodArgumentNotValidException: errores de validación (400).
- AccessDeniedException: errores de autorización (403).
- Exception genérica: error interno (500).

Esto permite:

- Unificar el formato de respuesta.
- Evitar código repetido en los controladores.
- Separar la lógica de negocio del tratamiento de errores.
- No exponer detalles internos del servidor.

---

## 7. Reflexión técnica

Durante el desarrollo se comprendió la importancia de:

- Centralizar el estado compartido en una única clase.
- Utilizar inyección de dependencias en lugar de instanciar objetos manualmente.
- Separar claramente recursos en distintos controladores.
- Aplicar correctamente los métodos HTTP (GET, POST, PATCH, DELETE).
- Utilizar códigos de estado coherentes con la operación realizada.
- Gestionar las excepciones de forma centralizada.

También fue clave probar cada endpoint en distintos escenarios:

- Usuario autenticado.
- Usuario no autenticado.
- Usuario sin permisos suficientes.
- Datos inválidos.
- Conflictos de negocio.

Esto permitió verificar que la API responde correctamente en todas las situaciones.

---

## 8. Manual de usuario (Postman)

Enlace a la colección Postman:  
[Ver colección en Postman](https://crismo5-882828.postman.co/workspace/Cristina's-Workspace~0a90e8fb-3202-4457-87ca-878bb8d28539/folder/51822749-54b9f981-f2ec-4bb5-a639-14adbbe22093?action=share&source=copy-link&creator=51822749&ctx=documentation)
Flujo de prueba recomendado:

1. Registrar un usuario o utilizar uno existente.
2. Hacer login con email y contraseña.
3. Probar endpoints protegidos según el rol.
4. Crear pistas como ADMIN.
5. Crear reservas como USER.
6. Consultar disponibilidad.
7. Probar cancelaciones y modificaciones.
8. Hacer logout y comprobar que los endpoints protegidos devuelven 401.

Es obligatorio realizar login antes de acceder a endpoints protegidos, ya que la API funciona mediante sesión.

---

## 9. Conclusión

El proyecto ha dado como resultado una API REST funcional para la gestión de una aplicación de reservas de pistas de pádel, estructurada de forma coherente y con un diseño consistente entre recursos. La arquitectura permite gestionar usuarios, pistas, reservas y disponibilidad de manera integrada, utilizando una única fuente de datos en memoria compartida entre los controladores. La autenticación se realiza mediante sesión HTTP y la autorización se basa en roles, asegurando que cada usuario solo pueda acceder a los recursos permitidos. Además, se han implementado validaciones automáticas y reglas de negocio específicas, junto con un sistema global de manejo de errores que garantiza respuestas uniformes en formato JSON. La API responde adecuadamente ante situaciones correctas y ante errores, utilizando los códigos de estado HTTP apropiados y manteniendo un comportamiento predecible en todos los endpoints.
