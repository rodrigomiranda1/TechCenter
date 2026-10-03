import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/empleado';

export interface ProductoStockBajo {
  idProducto: number;
  nombre: string;
  stockActual: number;
  stockMinimo: number;
}

export interface VentaResumen {
  idVenta: number;
  fechaVenta: string;
  nombreCliente: string;
  nombreUsuario: string;
  total: number;
  estado: string;
}

export interface PanelEmpleado {
  fechaActual: string;
  ventasHoy: number;
  movimientosHoy: number;
  stockBajo: ProductoStockBajo[];
  totalVentas: number;
  totalPedidos: number;
  totalProductos: number;
  totalMovimientos: number;
  ultimasVentas: VentaResumen[];
}

export interface Movimiento {
  idMovimiento: number;
  fechaMovimiento: string;
  nombreProducto: string;
  tipoMovimiento: string;
  cantidad: number;
  observacion: string;
}

export interface ProductoSelect {
  idProducto: number;
  nombre: string;
  stockActual: number;
  stockMinimo: number;
}

export interface InventarioData {
  movimientos: Movimiento[];
  productos: ProductoSelect[];
  stockBajo: ProductoSelect[];
}

export interface PedidoResumen {
  idPedido: number;
  fechaPedido: string;
  nombreCliente: string;
  nombreUsuario: string;
  estado: string;
  direccionEntrega: string;
  total: number;
}

export interface PedidoDetalleItem {
  nombreProducto: string;
  cantidad: number;
  precioUnitario: number;
  totalLinea: number;
}

export interface PagoInfo {
  pasarela: string;
  codigoOperacion: string;
  moneda: string;
  monto: number;
  estado: string;
  respuestaPasarela: string;
}

export interface ComprobanteInfo {
  idComprobante: number;
  tipo: string;
  serie: string;
  correlativo: number;
  fechaEmision: string;
  estado: string;
}

export interface PedidoConfirmacion {
  idPedido: number;
  fechaPedido: string;
  estado: string;
  nombreCliente: string;
  documentoCliente: string;
  direccionEntrega: string;
  observacion: string;
  subtotal: number;
  igv: number;
  total: number;
  detalles: PedidoDetalleItem[];
  pago: PagoInfo | null;
  comprobante: ComprobanteInfo | null;
}

@Injectable({
  providedIn: 'root'
})
export class Empleadoservice {

  constructor(private http: HttpClient) { }

  panel(): Observable<{ panel: PanelEmpleado }> {
    return this.http.get<{ panel: PanelEmpleado }>(`${API_URL}/panel`, { withCredentials: true });
  }

  inventario(): Observable<{ inventario: InventarioData }> {
    return this.http.get<{ inventario: InventarioData }>(`${API_URL}/inventario`, { withCredentials: true });
  }

  registrarMovimiento(idProducto: number, tipoMovimiento: string, cantidad: number, observacion: string): Observable<any> {
    return this.http.post(`${API_URL}/inventario/movimiento`, { idProducto, tipoMovimiento, cantidad, observacion }, { withCredentials: true });
  }
  pedidos(): Observable<{ pedidos: PedidoResumen[] }> {
    return this.http.get<{ pedidos: PedidoResumen[] }>(`${API_URL}/pedidos`, { withCredentials: true });
  }

  pedidoDetalle(id: number): Observable<{ pedido: PedidoConfirmacion }> {
    return this.http.get<{ pedido: PedidoConfirmacion }>(`${API_URL}/pedidos/${id}`, { withCredentials: true });
  }
}
