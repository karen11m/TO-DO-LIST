# 📋 To-Do List App

**Aplicación móvil nativa Android para gestión de tareas personales con sincronización en tiempo real usando Firebase Firestore.**

---

## 📌 **Descripción del Proyecto**

Aplicación desarrollada en **Android (Java)** que permite a los usuarios gestionar sus tareas diarias con **persistencia en la nube** mediante **Firebase Firestore**. La app cuenta con **sincronización en tiempo real**, **CRUD completo** y un **diseño moderno y profesional**.

---

## 🎯 **Características Principales**

### 📱 **Funcionalidades**
- ✅ **Crear tareas** con título, descripción, prioridad y categoría
- ✅ **Leer tareas** en tiempo real con actualización automática
- ✅ **Actualizar tareas** (editar, marcar como completadas)
- ✅ **Eliminar tareas** con confirmación
- ✅ **Sincronización en tiempo real** con Firebase Firestore
- ✅ **Contador de progreso** de tareas completadas
- ✅ **Calendario** para ver tareas por fecha
- ✅ **Estadísticas** de productividad

### 🎨 **Diseño UI/UX**
- ✅ **Material Design** con colores profesionales
- ✅ **CardViews** con sombras y bordes redondeados
- ✅ **Bottom Navigation** con 4 secciones
- ✅ **FAB** flotante para agregar tareas
- ✅ **TextInputLayout** en formularios
- ✅ **Spinners** para prioridad y categoría

### 🔧 **Tecnologías**
- ✅ **Firebase Firestore** (Base de datos NoSQL en la nube)
- ✅ **Firebase Authentication** (opcional)
- ✅ **RecyclerView** con ViewHolder optimizado
- ✅ **SQLite** (solo para compatibilidad, migrado a Firestore)

---

## 📸 **Capturas de Pantalla**

| Pantalla Principal | Agregar Tarea | Calendario |
|--------------------|---------------|------------|
|(![WhatsApp Image 2026-06-17 at 1.23.50 PM.jpeg](screenshots/WhatsApp%20Image%202026-06-17%20at%201.23.50%20PM.jpeg)) | ![Add](![WhatsApp Image 2026-06-17 at 1.26.51 PM.jpeg](screenshots/WhatsApp%20Image%202026-06-17%20at%201.26.51%20PM.jpeg)) | ![Calendar](![WhatsApp Image 2026-06-17 at 1.26.52 PM.jpeg](screenshots/WhatsApp%20Image%202026-06-17%20at%201.26.52%20PM.jpeg)) |

| Estadísticas | Configuración | Firebase Console |
|--------------|---------------|------------------|
| ![Stats](![WhatsApp Image 2026-06-17 at 1.26.52 PM.jpeg](screenshots/WhatsApp%20Image%202026-06-17%20at%201.26.52%20PM.jpeg)) | ![Settings](![WhatsApp Image 2026-06-17 at 1.26.52 PM.jpeg](screenshots/WhatsApp%20Image%202026-06-17%20at%201.26.52%20PM.jpeg)) | ![Firebase](![img.png](img.png)![{C49CCEE3-FD30-4272-9ABA-066C63F70E59}.png](screenshots/%7BC49CCEE3-FD30-4272-9ABA-066C63F70E59%7D.png)) |

---

## 🛠️ **Requisitos Técnicos (Momento 2)**

### ✅ **1. Sincronización en Tiempo Real**
- El RecyclerView está conectado a Firestore mediante **SnapshotListener**
- Los cambios se reflejan **automáticamente** en todos los dispositivos

### ✅ **2. CRUD Completo en NoSQL**
| Operación | Método | Estado |
|-----------|--------|--------|
| **Create** | `FirebaseHelper.agregarTarea()` | ✅ |
| **Read** | `FirebaseHelper.obtenerTareasEnTiempoReal()` | ✅ |
| **Update** | `FirebaseHelper.actualizarTarea()` | ✅ |
| **Delete** | `FirebaseHelper.eliminarTarea()` | ✅ |

### ✅ **3. Diseño UI/UX**
- **CardViews** con sombras y bordes redondeados
- **TextInputLayout** de Material Design
- Íconos vectoriales (Vector Assets)

### ✅ **4. Validaciones y Rendimiento**
- ✅ Campos críticos no pueden enviarse vacíos
- ✅ TextWatcher en tiempo real (mínimo 3 caracteres)
- ✅ Prevención de doble envío (botón deshabilitado)
- ✅ Liberación de memoria con `ListenerRegistration.remove()`

---

## 📂 **Estructura del Proyecto**
