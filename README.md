# 🐾 AppDoptar Chile - Taller Aplicado de Programación

![Android](https://img.shields.io/badge/Platform-Android-brightgreen.svg) 
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg) 
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg) 
![Room](https://img.shields.io/badge/Database-Room%20SQLite-lightblue.svg)

**AppDoptar Chile** es una solución móvil nativa diseñada para centralizar y facilitar el proceso de adopción de mascotas en Chile, promoviendo la tenencia responsable y creando una red comunitaria para reportar emergencias animales.

---

## 📖 Sobre el Proyecto

Este proyecto nace en el contexto del **Taller Aplicado de Programación**, con el objetivo de resolver la fragmentación de información sobre adopciones y el abandono animal. La aplicación permite a los usuarios no solo buscar a su próximo compañero, sino también reportar animales en peligro mediante geolocalización en tiempo real.

### 🎯 Objetivos
- **General:** Desarrollar una aplicación móvil funcional que gestione el ciclo de adopción y reportes comunitarios.
- **Específicos:**
  - Implementar una base de datos local robusta con Room.
  - Crear una interfaz moderna y fluida con Jetpack Compose y Material Design 3.
  - Integrar mapas interactivos para la visualización de urgencias.
  - Simular un sistema de donaciones para fundaciones.

---

## ✨ Características Principales

- **Gestión de Adopciones:** Publicación de perfiles detallados con fotos, filtros por región y tipo de mascota.
- **Mapa de Urgencias:** Reporte de mascotas perdidas o heridas usando **OSMDroid (OpenStreetMap)**.
- **Autenticación Segura:** Registro e inicio de sesión de usuarios con persistencia local.
- **Blog y Educación:** Sección informativa sobre cuidados y tenencia responsable.
- **Crowdfunding Simulado:** Interfaz para apoyar económicamente a refugios mediante una pasarela de pagos en modo Sandbox/Mock.

---

## 🛠️ Stack Tecnológico

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Base de Datos:** Room Persistence Library (SQLite)
- **Mapas:** OSMDroid
- **Inyección de Dependencias:** (Si usan Hilt o manual, especificar aquí)
- **Gestión de Hilos:** Kotlin Coroutines & Flow

---

## 📊 Diseño de la Base de Datos (MER)

El sistema utiliza una arquitectura relacional local para garantizar la integridad de los datos de usuarios y mascotas:

```mermaid
erDiagram
    USUARIO ||--o{ MASCOTA : "publica"
    USUARIO ||--o{ URGENCIA : "reporta"

    USUARIO {
        int id_usuario PK
        string nombre 
        string email 
        string password 
        string telefono 
        string region 
    }

    MASCOTA {
        int id_mascota PK
        int id_usuario FK
        string nombre 
        string tipo 
        string region 
        string ciudad 
        string foto_path 
    }

    URGENCIA {
        int id_urgencia PK
        int id_usuario FK
        string tipo_urgencia 
        float latitud 
        float longitud 
        datetime fecha_reporte 
    }
