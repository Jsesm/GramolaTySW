# Gramola Virtual 🎶

Este proyecto consiste en el desarrollo de una **gramola virtual** (jukebox) diseñada como un servicio para establecimientos de hostelería. La aplicación permite que los clientes de un bar busquen canciones a través de la API de Spotify y, tras realizar un pago, las añadan a la cola de reproducción actual del local.



## 🚀 Tecnologías Utilizadas

El sistema se basa en una arquitectura desacoplada utilizando las siguientes tecnologías:

* **Backend:** Java con el framework **Spring**.
* **Frontend:** **Angular** para una interfaz dinámica.
* **Base de Datos:** **MySQL**.
* **Pruebas Funcionales:** Automatización con **Selenium**.
* **Integraciones:** API de Spotify para música, servicios de correo electrónico (confirmación de cuenta) y pasarela de pagos.

---

## 🏗️ Arquitectura del Sistema

El sistema está estructurado en dos bloques principales de funcionalidad:

1. **Gestión de Cuentas (Bares):** Registro de establecimientos, inicio de sesión y recuperación de contraseñas.
2. **Funcionalidades de Gramola:** Herramientas para que los clientes finales interactúen con la música (búsqueda, pago e inserción de temas).

### Organización del Código
* **Front-end:** Organizado en vistas, modelos y servicios.
* **Back-end:** Estructurado en controladores, servicios y repositorios.

---

## 👥 Actores y Casos de Uso

### Propietario del Bar
* **Registro y Suscripción:** Creación de cuenta con validación por correo electrónico y elección de suscripciones mensuales o anuales (precios gestionados desde la BD).
* **Seguridad:** Autenticación mediante login y sistema de recuperación de contraseña con tokens de seguridad.

### Cliente del Bar
* **Búsqueda de Música:** Localización de canciones en tiempo real a través de Spotify.
* **Sistema de Cola:** Capacidad de insertar una canción para que suene inmediatamente después de la actual tras el pago de una tarifa.

---

## 🧪 Pruebas Funcionales (Selenium)

Se incluyen pruebas automatizadas para garantizar la estabilidad del servicio:
1. **Flujo de Éxito:** Verificación de que una canción pagada se añade correctamente a la lista del backend y se registra el pago.
2. **Control de Errores:** Validación de la respuesta del sistema ante datos de pago incorrectos.

---

## 🌟 Características Avanzadas
* **Geolocalización:** El sistema detecta las coordenadas del bar mediante un servicio externo.
* **Restricción de Proximidad:** La aplicación solo permite a los clientes añadir canciones si se encuentran dentro de un radio de 100 metros del establecimiento.
* **Firma en Canvas:** Los propietarios deben firmar digitalmente en un lienzo (Canvas) al registrarse, almacenando dicha firma para mostrarla en el login.
* **Diseño Responsivo:** Interfaz adaptada para un uso cómodo en tablets y dispositivos móviles.

---

## 🛠️ Instalación y Ejecución

1. Clonar el repositorio.
2. Configurar el esquema de MySQL y actualizar las credenciales en el archivo `application.properties`.
3. **Iniciar el Servidor (Backend):** `./mvnw spring-boot:run`
4. **Iniciar el Cliente (Frontend):** `npm install` y posteriormente `ng serve --host 127.0.0.1`

---
**Curso:** 2025-2026 | **Asignatura:** Tecnologías y Sistemas Web
