import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClienteBusqueda, VentaPagina, VentaComprobante } from './model/Venta';

const API_URL = 'http://localhost:8080/api/ventas';

@Injectable({
  providedIn: 'root'
})
export class Ventaservice {

  constructor(private http: HttpClient) { }

  buscarClientePorDni(dni: string): Observable<ClienteBusqueda> {
    return this.http.get<ClienteBusqueda>(`${API_URL}/clientes/buscar?dni=${dni}`, { withCredentials: true });
  }

  crearClienteRapido(datos: { dni: string, nombres: string, apellidos: string, telefono: string, direccion: string }): Observable<ClienteBusqueda> {
    return this.http.post<ClienteBusqueda>(`${API_URL}/clientes`, datos, { withCredentials: true });
  }

  correlativo(serie: string): Observable<string> {
    return this.http.get(`${API_URL}/correlativo?serie=${serie}`, { withCredentials: true, responseType: 'text' });
  }

  registrar(body: any): Observable<any> {
    return this.http.post(API_URL, body, { withCredentials: true });
  }

  listado(page: number): Observable<{ pagina: VentaPagina }> {
    return this.http.get<{ pagina: VentaPagina }>(`${API_URL}/listado?page=${page}`, { withCredentials: true });
  }

  comprobante(id: number): Observable<{ comprobante: VentaComprobante }> {
    return this.http.get<{ comprobante: VentaComprobante }>(`${API_URL}/comprobante/${id}`, { withCredentials: true });
  }
}
