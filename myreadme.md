# Desafío Técnico: Gestión de Tareas con Spring Boot y OpenAPI

## 1. Descripción del Desafío

El objetivo de este proyecto es desarrollar una API RESTful utilizando **Spring Boot** para la gestión de tareas. La aplicación permite a los usuarios autenticarse, crear, actualizar, eliminar y listar tareas.

Se implementaron las siguientes tecnologías:

- **Java 17**
- **Spring Boot 3.4.x**
- **Base de datos en memoria H2**
- **JWT (JSON Web Token) para autenticación**
- **Documentación con OpenAPI y Swagger**
- **Estrategia API First con OpenAPI Generator**
- **Pruebas unitarias con JUnit y MockMvc**

---

## 2. Instalación y Configuración

### 2.1 Prerrequisitos

Asegúrate de tener instalado:

- **Java 17**
- **Maven** (`mvn -version` para verificar)
- **Git** (`git --version` para verificar)

### 2.2 Clonar el Repositorio

```sh
git clone https://github.com/previred/desafio-spring-boot.git
cd desafio-spring-boot
```

### 2.3 Configurar el Proyecto

El archivo `src/main/resources/application.yml` contiene la configuración:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:tareasdb
    driverClassName: org.h2.Driver
    username: sa
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
  security:
    user:
      name: admin
      password: admin

jwt:
  secret: mySuperSecretKeyForJWT1234567890!
  expiration: 3600000

crendentila-api-key:
  email: admin@nuevo.com
  password: admin123
```

## 3. Ejecución del Proyecto

Ejecuta los siguientes comandos desde la carpeta del proyecto:

```sh
./mvnw clean install
./mvnw spring-boot:run
```

La aplicación estará disponible en:

- **API:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/h2-console` (User: `sa`, Password: `password`)

## 4. Generación de API First con OpenAPI

### 4.1 Definir el archivo OpenAPI

El archivo `src/main/resources/openapi.yaml` contiene la definición de la API.

### 4.2 Generar el Código de la API

Ejecuta el siguiente comando:

```sh
./mvnw clean generate-sources
```

Esto generará las interfaces API en `target/generated-sources/openapi/`.

### 4.3 Implementar los Controladores

Ahora puedes hacer que tus controladores implementen las interfaces generadas en lugar de definir los métodos manualmente.

## 5. Ejecución de Pruebas

Para ejecutar los tests unitarios:

```sh
./mvnw test
```

Los resultados de las pruebas se mostrarán en la consola.

## 6. Autenticación con JWT

### 6.1 Obtener un Token

Realiza una petición `POST` a `http://localhost:8080/auth/login` con el siguiente JSON:

```json
{
  "email": "admin@nuevo.com",
  "password": "admin123"
}
```

También puedes obtener el token utilizando **cURL** desde la terminal:

```sh
curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=20E7DB648D3830CF0C881D53EC7AAEC0' \
--data-raw '{"email":"admin@nuevo.com","password":"admin123"}'
```

#### 📌 **Respuesta esperada:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBudWV2by5jb20iLCJpYXQiOjE3NDE1NTE4NjcsImV4cCI6MTc0MTU1NTQ2N30.PWL4Gr7iMa62yi-sJRMtu5uJ26Rd3uoKVEfiBeWWvcA"
}
```

📌 **Nota:** El token cambiará en cada autenticación.

---

## 7. Métodos CRUD para Tareas

### 7.1 Obtener la Lista de Tareas

```sh
curl --location 'http://localhost:8080/tareas' \
--header 'Authorization: Bearer {tu-token}' \
--header 'Cookie: JSESSIONID=20E7DB648D3830CF0C881D53EC7AAEC0'
```

### 7.2 Agregar una Tarea

```sh
curl --location 'http://localhost:8080/tareas' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {tu-token}' \
--header 'Cookie: JSESSIONID=20E7DB648D3830CF0C881D53EC7AAEC0' \
--data '{
  "titulo": "Finalizar reporte mensual",
  "descripcion": "Revisar y completar el reporte de ventas",
  "fechaVencimiento": "2025-03-15T23:59:59"
}'
```

### 7.3 Actualizar una Tarea

```sh
curl --location --request PUT 'http://localhost:8080/tareas/1' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer {tu-token}' \
--header 'Cookie: JSESSIONID=20E7DB648D3830CF0C881D53EC7AAEC0' \
--data '{
  "titulo": "Actualizar reporte mensual",
  "descripcion": "Agregar datos nuevos al reporte",
  "fechaVencimiento": "2025-03-20T23:59:59"
}'
```

### 7.4 Eliminar una Tarea

```sh
curl --location --request DELETE 'http://localhost:8080/tareas/1' \
--header 'Authorization: Bearer {tu-token}' \
--header 'Cookie: JSESSIONID=20E7DB648D3830CF0C881D53EC7AAEC0'
```

```

```
