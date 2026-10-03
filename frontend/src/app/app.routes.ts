import { Routes } from '@angular/router';
import { Login } from './auth/login/login';
import { Register } from './auth/register/register';
import { Catalogo } from './catalogo/catalogo';
import { ProductoDetalle } from './catalogo/producto-detalle/producto-detalle';
import { Perfil } from './perfil/perfil';
import { AsesorIa } from './asesor-ia/asesor-ia';
import { Carrito } from './carrito/carrito';
import { Pago } from './checkout/pago/pago';
import { Confirmacion } from './checkout/confirmacion/confirmacion';
import { Dashboard } from './admin/dashboard/dashboard';
import { Personas } from './admin/personas/personas';
import { Auditoria } from './admin/auditoria/auditoria';
import { Pedidos } from './admin/pedidos/pedidos';
import { CatalogoAdmin } from './operaciones/catalogo-admin/catalogo-admin';
import { Compras } from './operaciones/compras/compras';
import { ReportesComponent } from './operaciones/reportes/reportes';
import { Panel } from './empleado/panel/panel';
import { Inventario } from './empleado/inventario/inventario';
import { Pedidos as PedidosEmpleado } from './empleado/pedidos/pedidos';
import { PedidoDetalle as PedidoDetalleEmpleado } from './empleado/pedido-detalle/pedido-detalle';
import { Registrar } from './ventas/registrar/registrar';
import { Listado } from './ventas/listado/listado';
import { Comprobante } from './ventas/comprobante/comprobante';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'inicio', component: Catalogo },
  { path: 'producto/:id', component: ProductoDetalle },
  { path: 'perfil', component: Perfil },
  { path: 'asesor-ia', component: AsesorIa },
  { path: 'carrito', component: Carrito },
  { path: 'checkout/pago', component: Pago },
  { path: 'checkout/confirmacion/:id', component: Confirmacion },
  { path: 'admin/panel', component: Dashboard },
  { path: 'admin/personas', component: Personas },
  { path: 'admin/auditoria', component: Auditoria },
  { path: 'admin/pedidos', component: Pedidos },
  { path: 'operaciones/catalogo', component: CatalogoAdmin },
  { path: 'operaciones/compras', component: Compras },
  { path: 'operaciones/reportes', component: ReportesComponent },
  { path: 'empleado/panel', component: Panel },
  { path: 'empleado/inventario', component: Inventario },
  { path: 'empleado/pedidos', component: PedidosEmpleado },
  { path: 'empleado/pedidos/:id', component: PedidoDetalleEmpleado },
  { path: 'ventas', component: Registrar },
  { path: 'ventas/listado', component: Listado },
  { path: 'ventas/comprobante/:id', component: Comprobante },
];
