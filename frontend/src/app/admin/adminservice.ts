import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/admin';

export interface ProductoStockBajo {
  idProducto: number;
  nombre: string;
  stockActual: number;
  stockMinimo: number;
}

export interface DashboardData {
  totalProductos: number;
  stockBajo: ProductoStockBajo[];
}

export interface AuditoriaItem {
  fecha: string;
  accion: string;
  entidad: string;
}

export interface PedidoResumen {
  idPedido: number;
  fechaPedido: string;
  nombreCliente: string;
  total: number;
  estado: string;
}

export interface VentaReporte {
  idVenta: number;
  fechaVenta: string;
  nombreCliente: string;
  nombreUsuario: string;
  tipoComprobante: string;
  serie: string;
  correlativo: number;
  metodoPago: string;
  estado: string;
  total: number;
}

export interface PedidoReporte {
  idPedido: number;
  fechaPedido: string;
  nombreCliente: string;
  nombreUsuario: string;
  estado: string;
  direccionEntrega: string;
  total: number;
}

export interface Reportes {
  ventas: VentaReporte[];
  cantidadVentas: number;
  totalVentas: number;
  pedidos: PedidoReporte[];
  cantidadPedidos: number;
  totalPedidos: number;
}

@Injectable({
  providedIn: 'root'
})
export class Adminservice {

  constructor(private http: HttpClient) { }

  dashboard(): Observable<DashboardData> {
    return this.http.get<DashboardData>(`${API_URL}/dashboard`, { withCredentials: true });
  }

  auditoria(): Observable<{ auditorias: AuditoriaItem[] }> {
    return this.http.get<{ auditorias: AuditoriaItem[] }>(`${API_URL}/auditoria`, { withCredentials: true });
  }

  pedidos(): Observable<{ pedidos: PedidoResumen[] }> {
    return this.http.get<{ pedidos: PedidoResumen[] }>(`${API_URL}/pedidos`, { withCredentials: true });
  }

  reportes(ventasDesde?: string, ventasHasta?: string, pedidosDesde?: string, pedidosHasta?: string): Observable<{ reportes: Reportes }> {
    const params: string[] = [];
    if (ventasDesde) params.push(`ventasDesde=${ventasDesde}`);
    if (ventasHasta) params.push(`ventasHasta=${ventasHasta}`);
    if (pedidosDesde) params.push(`pedidosDesde=${pedidosDesde}`);
    if (pedidosHasta) params.push(`pedidosHasta=${pedidosHasta}`);
    const query = params.length ? `?${params.join('&')}` : '';
    return this.http.get<{ reportes: Reportes }>(`${API_URL}/reportes${query}`, { withCredentials: true });
  }
}
