# Gramola Virtual 🎶

Este proyecto consiste en el desarrollo de una **gramola virtual** como servicio para establecimientos (bares). Permite que los clientes de un bar busquen canciones a través de una API de música en streaming (Spotify) y, previo pago de un importe, las "cuelen" en la lista de reproducción actual del local.

## 🚀 Tecnologías Utilizadas

El sistema sigue una arquitectura moderna desacoplada:

* [cite_start]**Backend:** Java con el framework **Spring**[cite: 92].
* [cite_start]**Frontend:** **Angular** para una interfaz de usuario dinámica y responsiva[cite: 92, 110].
* [cite_start]**Base de Datos:** **MySQL**[cite: 93].
* [cite_start]**Pruebas Funcionales:** **Selenium** para la automatización de escenarios de uso[cite: 101].
* [cite_start]**Integraciones:** API de Spotify (música) y servicios externos para pagos y correo electrónico[cite: 6, 49, 67].

---

## 🏗️ Arquitectura del Sistema

[cite_start]El sistema se divide en dos grandes bloques de funcionalidades[cite: 45, 65]:

1.  [cite_start]**Gestión de Cuentas (Bares):** Registro de establecimientos, inicio de sesión, recuperación de contraseñas mediante correo electrónico y gestión de suscripciones[cite: 46, 47, 67].
2.  [cite_start]**Funcionalidades de Gramola:** Búsqueda de canciones, gestión de la cola de reproducción y procesamiento de pagos[cite: 48, 49, 68].


### Estructura de Desarrollo
* [cite_start]**Front-end:** Organizado en vistas, modelos y servicios[cite: 95].
* [cite_start]**Back-end:** Estructurado en controladores, servicios y repositorios[cite: 96].

---

## 👥 Actores y Casos de Uso

### Propietario del Bar
* [cite_start]**Registro y Pago:** Creación de cuenta con validación por correo y pago de suscripción mensual/anual (precios gestionados desde BD)[cite: 72, 75, 77].
* [cite_start]**Autenticación:** Login, recuperación de contraseña con token y cierre de sesión[cite: 78, 79, 81].

### Cliente del Bar
* [cite_start]**Búsqueda:** Localizar canciones mediante la API de Spotify[cite: 83].
* [cite_start]**Interserción en Cola:** Añadir una canción a la cola de reproducción actual previo pago de la tarifa establecida[cite: 84, 85].
* [cite_start]**Reproducción:** La canción seleccionada se reproduce inmediatamente después de la actual[cite: 86].

---

## 🧪 Pruebas Funcionales (Selenium)

[cite_start]Se han definido los siguientes escenarios de prueba obligatorios[cite: 101]:
1.  [cite_start]**Flujo Positivo:** Búsqueda de canción, pago correcto y verificación de inserción en la cola del backend[cite: 102, 103].
2.  [cite_start]**Flujo Negativo:** Error controlado al introducir datos de pago incorrectos[cite: 104].

---

## 🌟 Características Opcionales Implementadas
* [cite_start]**Geolocalización:** Registro de coordenadas del bar y restricción de uso de la app a un radio de 100 metros[cite: 113, 114].
* [cite_start]**Firma Digital:** Firma en Canvas durante el registro y visualización de la misma en el login[cite: 115, 116].
* [cite_start]**Interfaz Responsiva:** Diseño adaptable a diferentes dispositivos (tablets, móviles, ordenadores)[cite: 110].

---

## 🛠️ Instalación y Ejecución

*(Instrucciones genéricas para completar por el alumno)*

1.  Clonar el repositorio.
2.  Configurar la base de datos MySQL y las credenciales en `application.properties`.
3.  Ejecutar el backend: `./mvnw spring-boot:run`.
4.  Instalar dependencias del frontend: `npm install`.
5.  Ejecutar el frontend: `ng serve`.

---
**Curso:** 2025-2026 | [cite_start]**Asignatura:** Tecnologías y Sistemas Web [cite: 1]
