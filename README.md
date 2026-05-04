# 🧩 AJA Forum API

API REST desarrollada con **Spring Boot** para la gestión de un foro online de la empresa **AJA (Empresa ficticia por estudios IOC, DAM)**. Proporciona funcionalidades de autenticación, gestión de usuarios, foros, temas, publicaciones y mensajería directa, además de un sistema en tiempo real para visualizar actividad de usuarios mediante WebSockets.

---

## 🚀 Tecnologías utilizadas

- Java 21+
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- WebSockets (STOMP)
- HTTPS (TLS)

---

## 📦 Features

### 🔐 Auth
- Registro de usuarios
- Login con generación de JWT
- Prefiltro JWT antes del `AuthenticationManager`
- Autenticación stateless

### 👤 User
- Gestión de usuarios
- Roles:
  - `ADMIN`
  - `USER`
- Control de acceso basado en roles con Spring Security

### 💬 DirectMessage
- Envío de mensajes privados entre usuarios
- Historial de conversaciones

### 🧵 Forum
- Creación y gestión de foros
- Organización por categorías

### 📊 ForumStatus (WebSocket)
- Sistema en tiempo real
- Permite ver:
  - Qué usuarios están activos en un foro
  - Quién está escribiendo
  - En qué sección se encuentran
- Implementado con WebSockets (STOMP)

### 📝 Topic
- Creación de temas dentro de un foro
- Relación con usuarios y posts

### 📄 Post
- Publicación de mensajes dentro de un tema
- Edición y eliminación de posts

---

## 🔐 Seguridad

- Implementación con **Spring Security**
- Autenticación basada en **JWT**
- Filtro personalizado previo al `AuthenticationManager`
- Autorización por roles (`ADMIN`, `USER`)
- Protección de rutas según permisos

---

## 🗄️ Base de datos

- Motor: **PostgreSQL**
- ORM: **Spring Data JPA**
- Persistencia de:
  - Usuarios
  - Mensajes
  - Foros
  - Temas
  - Posts

---

## 🧱 Arquitectura

- Feature-based structure

---

## ⚙️ Configuración

### 1. Requisitos

- Java 21+
- Apache Maven 3.9.14+
- Base de datos relacional (SQL)

### 2. Clonar el repositorio

git clone https://github.com/Mel0nABC/aja_api_server.git

### 3. Configurar la base de datos

Editar método dataSource() en ApplicationConfig.java

### 4. Ejecutar aplicación

cd aja_api_server
mvn spring-boot:run


## 🔌 Endpoints principales (ejemplos)

| Método | Endpoint           | Descripción         |
|--------|--------------------|---------------------|
| GET    | /api/apidocs       | Java Docs           |
| POST   | /api/auth/login    | Autenticación       |
| POST   | /api/auth/logout   | Logout              |
| GET    | /api/forum         | Listar foros        |
| GET    | /api/post          | Listar post         |
| GET    | /api/topic         | Listar topics       |
| GET    | /api/user          | Listar foros        |
| GET    | /api/dm            | Mensajes directos   |


---

## 📡 WebSocket

| Método | Endpoint           | Descripción         |
|--------|--------------------|---------------------|
| wss    | /api/ws-connection | Handshake           |
| wss    | /api/notify        | Notificar estado    |
| wss    | /api/finish        | Lista de estados    |
| wss    | /status            | Suscripción         |


Permite recibir eventos en tiempo real sobre la actividad de usuarios en el foro.

Si están escribiendo un post y notificar a la suscripción la lista de todos los que lo hacen.

---

## 🧪 Testing

Se recomienda usar herramientas como:

- Postman  
- Insomnia  

Para probar endpoints REST y autenticación con JWT.

Están implementados todos los test mediante MockMVC.

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT.

Copyright (c) 2026 Mel0nABC en GitHub

Se concede permiso, de forma gratuita, a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados aja-forum-api, para utilizar el Software sin restricción, incluyendo sin limitación los derechos a usar, copiar, modificar, fusionar, publicar, distribuir, sublicenciar y/o vender copias del Software, y permitir a las personas a las que se les proporcione el Software hacer lo mismo, sujeto a las siguientes condiciones:

- El aviso de copyright anterior y este aviso de permiso deberán incluirse en todas las copias o partes sustanciales del Software.
- Se deberá reconocer al autor original en cualquier distribución o uso público del Software.

EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O IMPLÍCITA, INCLUYENDO PERO NO LIMITADO A GARANTÍAS DE COMERCIALIZACIÓN, IDONEIDAD PARA UN PROPÓSITO PARTICULAR Y NO INFRACCIÓN. EN NINGÚN CASO LOS AUTORES O TITULARES DEL COPYRIGHT SERÁN RESPONSABLES DE NINGUNA RECLAMACIÓN, DAÑO U OTRA RESPONSABILIDAD, YA SEA EN UNA ACCIÓN CONTRACTUAL, AGRAVIO O DE OTRO TIPO, DERIVADA DE, O EN CONEXIÓN CON EL SOFTWARE O EL USO U OTRAS ACCIONES EN EL SOFTWARE.

---

**Autor:** Mel0nABC