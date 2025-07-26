
## ✅ Funcionalidades

- Recolección automática de datos GPS cada 30 segundos.
- Almacenamiento local con SQLite.
- Exposición de datos vía HTTP en tiempo real.
- Endpoints protegidos mediante token.
- Consulta remota del estado del dispositivo (batería, red, almacenamiento, modelo).
- Interfaz de usuario profesional y minimalista.

---

## 🧱 Requisitos

- Android Studio Flamingo o superior
- SDK mínimo: 24 (Android 7.0)
- Librerías:
  - `NanoHTTPD 2.3.1`
---

## ⚙️ Instalación

1. Clona el repositorio o descarga el ZIP.
2. Abre el proyecto en Android Studio.
3. 
4. Asegúrate de que el archivo `build.gradle.kts` incluya:

```kotlin
implementation("org.nanohttpd:nanohttpd:2.3.1")
````

---
## Prueba
1. Ejecutar la app en un dispositivo físico.
2. Conceder permisos de ubicación.
3. Presionar 'Iniciar Servicio'.
4. Copiar IP local mostrada en pantalla.
5. Realizar peticiones HTTP desde otro dispositivo o Postman:
GET http://<ip>:8080/api/device_status
Header: Authorization: BearerToken123
GET http://<ip>:8080/api/sensor_data?start_time=YYYY-MM-DD HH:mm:ss&end_time=YYYY-MM-DD HH:mm:ss
Header: Authorization: BearerToken123

