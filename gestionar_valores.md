# Guía de Gestión de Valores Económicos y Contables - Flowly

Este documento explica cómo se calculan, interpretan y gestionan los diferentes valores financieros dentro de la aplicación para mantener una contabilidad limpia y exacta.

---

## 1. Inventario y Materia Prima

### Registrar un Producto en Inventario
Cuando registras un producto en el inventario (por ejemplo, vasos, harina, leche), debes ingresar los siguientes datos:
*   **Cantidad:** La cantidad física de empaques o unidades de compra que tienes (ej. `1` paquete).
*   **Precio Unitario:** El costo del empaque o unidad de compra entera (ej. `${'$'}5.70` por el paquete).
*   **Cantidad por empaque (Unit Size):** La cantidad de unidades individuales que contiene cada empaque (ej. `50` unidades por paquete).
*   **Unidad de medida:** La unidad en la que compraste el producto (ej. `paq`, `kg`, `L`, `unidades`).

### Conversión a Subunidades de Receta
Para facilitar la creación de recetas, la aplicación traduce automáticamente las unidades del inventario a unidades de medida más pequeñas (subunidades) al importarlas:
*   `kg` (Kilogramo) $\rightarrow$ `g` (Gramo) (Relación: 1000)
*   `L` (Litro) $\rightarrow$ `mL` (Mililitro) (Relación: 1000)
*   `paq` (Paquete) $\rightarrow$ `unidades` (Relación: según Unit Size)
*   `unidades` $\rightarrow$ `unidades` (Relación: según Unit Size)

**Cálculo del Costo por Subunidad:**
$$\text{Costo por Subunidad} = \frac{\text{Precio Unitario del Inventario}}{\text{Cantidad por empaque (Unit Size)}}$$
*Ejemplo:* Si compras un paquete de 50 vasos por ${'$'}5.70:
$$\text{Costo por Vaso (unidad)} = \frac{5.70}{50} = 0.114 \text{ por unidad}$$

---

## 2. Recetas y Porciones

### Estructura de una Receta
*   **Ingredientes:** Cada ingrediente tiene una cantidad usada en la receta (en la subunidad correspondiente) y se multiplica por su Costo por Subunidad.
*   **Costo Total de la Receta:** Es la suma del costo de todos los ingredientes utilizados:
    $$\text{Costo Total} = \sum (\text{Cantidad Usada} \times \text{Costo por Subunidad})$$
*   **Porciones (Rendimiento):** El número de unidades o porciones que produce la receta completa (ej. `10` porciones).
*   **Precio de Venta:** Es el precio al que vendes **cada porción individual** del producto.

### Ganancia y Margen de la Receta
*   **Costo por Porción:**
    $$\text{Costo por Porción} = \frac{\text{Costo Total de la Receta}}{\text{Porciones}}$$
*   **Ganancia por Porción:**
    $$\text{Ganancia por Porción} = \text{Precio de Venta} - \text{Costo por Porción}$$
*   **Margen de Ganancia (%):** Representa el porcentaje del precio de venta que es ganancia neta antes de gastos operativos:
    $$\text{Margen (\%)} = \left( \frac{\text{Ganancia por Porción}}{\text{Precio de Venta}} \right) \times 100$$

---

## 3. Ventas y Costo de Ventas (COGS)

*   **Ingresos por Ventas (Revenue):** Es el total facturado en una venta:
    $$\text{Total Venta} = \text{Cantidad Vendida} \times \text{Precio Unitario de Venta}$$
*   **Costo de la Venta (COGS - Cost of Goods Sold):** Es el costo real que conllevó producir los artículos que se acaban de vender:
    $$\text{Costo de la Venta} = \text{Cantidad Vendida} \times \text{Costo por Porción}$$
    *(Este costo real se calcula dinámicamente consultando los ingredientes de la receta, no con una estimación del 50%).*

---

## 4. Reporte de Utilidades y Estado de Resultados (P&L)

Para evaluar la salud financiera del negocio en un periodo de tiempo, la aplicación calcula los siguientes totales:

1.  **Ingresos Totales (Revenue):** Suma de todos los montos de las ventas realizadas.
2.  **Costo de Ventas Total (COGS):** Suma del costo real de fabricación de todos los productos vendidos.
3.  **Utilidad Bruta (Gross Profit):**
    $$\text{Utilidad Bruta} = \text{Ingresos Totales} - \text{Costo de Ventas Total}$$
4.  **Gastos Operativos (Expenses):** Suma de los gastos fijos o variables registrados (ej. luz, agua, alquiler, publicidad) en el periodo.
5.  **Compras de Materia Prima (Purchases):** El monto total gastado en reabastecer el inventario en el periodo. *(Nota: Las compras no disminuyen directamente la utilidad neta de ventas ya que la materia prima queda como activo en el inventario hasta que se vende).*
6.  **Utilidad Neta (Net Profit):** La ganancia real final del negocio tras descontar los gastos operativos:
    $$\text{Utilidad Neta} = \text{Utilidad Bruta} - \text{Gastos Operativos}$$
