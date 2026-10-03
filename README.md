# Tech Center
> Plataforma fullstack desarrollada para la gestión centralizada de productos y usuarios.

 Construida bajo una **arquitectura monolítica organizada por capas y estructurado como un Monorepo**, integrando un backend en Spring Boot y un frontend en Angular.
 

## Funcionalidades Principales

### Asistente virtual ("Asesor IA")
* **Recomendaciones Personalizadas por Procesamiento de Texto:**
    * Algoritmo de asesoría que detecta palabras clave e intención del usuario (ej. *"placa madre"*, *"mouse"*, *"ram"*) para sugerir los mejores productos según relación calidad/precio.
* **Sugerencias Rápidas mediante Prompt Chips**
    * Botones interactivos de accesibilidad rápida para lanzar consultas predefinidas con un solo clic (*"Componentes para mi PC"*, *"Laptops"*, *"Audífonos o Parlantes"*, *"Consolas y Controles"*).
* **Historial de Conversaciones:**
    * Persistencia de chats para mantener el contexto de las consultas anteriores del usuario.

### Flujo completo del Cliente
* **Catálogo y Selección de Productos:**
    * Exploración dinámica de productos con búsqueda en tiempo real y paginación.
    * Agregado directo al carrito de compras con interfaz reactiva.

* **Gestión del Carrito y Proceso de Checkout:**
    * Revisión detallada de ítems seleccionados, cantidades y cálculo automático de montos.
    * Múltiples métodos de pago integrados: **Tarjeta de Crédito/Débito, Yape/QR y Efectivo** (simulaciones).

* **Simulador de Compra y Generación de Comprobantes (PDF):**
    * Desglose de la orden con cálculo oficial del **IGV**.
    * Generación y descarga (opcional) de **Comprobante de Pago en PDF**, el cual incluye:
    * Datos del cliente (Nombre, DNI y Dirección).
    * Metadatos de transacción (Numero de pedido, fecha exacta y estado del pago).
    * Detalle itemizado de los productos adquiridos.
---

### Gestión de Usuarios y Control de Acceso por Roles (RBAC)

* **Autenticación y Autorización basada en Roles:**
    * **Cliente (Usuario Final):** Acceso al Asesor IA, catálogo, carrito, pasarela de pago y descarga de comprobantes.
    * **Módulo POS y Ventas (Empleado):** Registro de ventas presenciales (Boleta/Factura), control de pedidos web, gestión de inventario (entradas, salidas, devoluciones) y panel operativo de stock crítico.
    * **Gestión Global (Administrador):** Control total de usuarios y roles (RBAC), gestión integral del catálogo/marcas, auditoría de eventos del sistema y reportes consolidados.
  
  

 
