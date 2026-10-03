import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = 'http://localhost:8080/api/checkout';

export interface DatosCheckout {
  direccionEntrega: string;
  tipoComprobante: string;
  metodoPago: string;
}

export interface ConfirmarPagoBody {
  direccionEntrega: string;
  tipoComprobante: string;
  metodoPago: string;
  numeroTarjeta?: string;
  codigoOperacion?: string;
}

@Injectable({
  providedIn: 'root'
})
export class Checkoutservice {

  private datos: DatosCheckout = { direccionEntrega: '', tipoComprobante: 'BOLETA', metodoPago: 'TARJETA_DEMO' };

  constructor(private http: HttpClient) { }

  guardarDatos(datos: DatosCheckout) {
    this.datos = datos;
  }

  obtenerDatos(): DatosCheckout {
    return this.datos;
  }

  confirmar(body: ConfirmarPagoBody): Observable<{ mensaje: string, idPedido: number }> {
    return this.http.post<{ mensaje: string, idPedido: number }>(`${API_URL}/confirmar`, body, { withCredentials: true });
  }

  obtenerConfirmacion(idPedido: number): Observable<{ pedido: any }> {
    return this.http.get<{ pedido: any }>(`${API_URL}/confirmacion/${idPedido}`, { withCredentials: true });
  }
}
