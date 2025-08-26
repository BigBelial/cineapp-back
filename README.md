# 🎬 CineApp - Backend

CineApp es una API REST desarrollada en Spring Boot para gestionar usuarios, películas y la compra de entradas de cine.

Este backend utiliza **cookies** para guardar el token JWT y verificar el tipo de usuario (normal o superusuario) en cada solicitud.

---

## 🚀 Tecnologías utilizadas

- Java 
- Spring Boot 
- Spring Security + JWT
- PostgreSQL
- Maven

---

## 📦 Módulos implementados

### 👤 Gestión de usuarios

- Registro y login con JWT
- Creación y administración de usuarios
- Eliminación y reactivación lógica (`active = false/true`)
- Solo superusuarios pueden acceder a información sensible

### 🎥 Gestión de películas

- CRUD completo de películas
- Solo superusuarios podrán gestionarlas
- Usuarios normales solo podrán ver cartelera activa

---

## ⚙️ Cómo ejecutar el proyecto

### 1. Clona el repositorio

```bash
git clone https://github.com/BigBelial/cineapp-backend.git
cd cineapp-backend
```

### 2. Configura la base de datos

Crea una base de datos PostgreSQL y configura el archivo, si la base de datos aun no esta creada, la aplicacion no iniciara:

📄 `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cineapp
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

### 3. Ejecuta la aplicación

Desde IntelliJ o con terminal:

```bash
./mvnw spring-boot:run
```

La API estará disponible en:  
📍 `http://localhost:8080`

### 4. Configura las credenciales de AWS S3

Para permitir la subida de imágenes a Amazon S3, debes configurar tus credenciales localmente en tu sistema operativo.

#### En Mac/Linux:
Crea o edita el archivo `~/.aws/credentials`:

```
[default]
aws_access_key_id = TU_ACCESS_KEY
aws_secret_access_key = TU_SECRET_KEY
```

Y en `~/.aws/config`:

```
[default]
region = us-east-1
```

#### En Windows:
Crea la carpeta y archivos en `C:\Users\TU_USUARIO\.aws\credentials` y `config` con el mismo contenido anterior.

> Estas credenciales se usan automáticamente por el SDK de AWS cuando ejecutas la app localmente. No se deben incluir en el repositorio.

---

## 🔐 Autenticación

- La autenticación se realiza con JWT y cookies
- Endpoints protegidos requieren login previo
- Los roles (`isSuperUser`) se verifican dentro del token JWT guardado en la cookie

---

## 📬 Endpoints principales

| Método | Ruta                             | Acceso           | Descripción                                         |
|--------|----------------------------------|------------------|-----------------------------------------------------|
| POST   | /auth/register                   | Público          | Registro de usuarios                                |
| POST   | /auth/login                      | Público          | Login con JWT + cookie                              |
| GET    | /usuarios                        | Superusuario     | Ver todos los usuarios                              |
| PATCH  | /usuarios/{id}                   | Usuario logueado | Editar su información personal                      |
| PATCH  | /usuarios/{id}/reactivate        | Superusuario     | Reactivar un usuario inactivo                       |
| PATCH  | /usuarios/{id}/deactivate        | Superusuario     | Desactivar un usuario                               |
| GET    | /peliculas                       | Público          | Ver cartelera activa (películas con active=true)    |
| GET    | /peliculas/admin                 | Superusuario     | Ver todas las películas (activas e inactivas)       |
| POST   | /peliculas                       | Superusuario     | Crear nueva película                                |
| PATCH  | /peliculas/{id}                  | Superusuario     | Editar datos de una película                        |
| PATCH  | /peliculas/{id}/inhabilitar      | Superusuario     | Marcar película como inactiva                       |

---

## 📘 Documentación de la API (Swagger UI)

Puedes acceder a la documentación interactiva de Swagger UI en la siguiente ruta:

🔗 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

> Asegúrate de tener la aplicación corriendo antes de acceder.


---

## 🧪 Pruebas recomendadas con Postman

1. Registrar un usuario
2. Convertirlo en superusuario directamente en la base de datos
3. Loguearse y obtener el token
4. Probar los endpoints protegidos

---

## 👨‍💻 Autor

Juan José Ospina Sánchez  
Ingeniero de sistemas
