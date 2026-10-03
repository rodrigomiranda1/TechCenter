import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { CarritoResumen } from './model/CarritoItem';

const API_URL = 'http://localhost:8080/api/carrito';

@Injectable({
  providedIn: 'root'
})
export class Carritoservice {

  private carritoSubject = new BehaviorSubject<CarritoResumen>({ items: [], total: 0 });
  carrito$ = this.carritoSubject.asObservable();

  constructor(private http: HttpClient) { }

  cargar(): Observable<CarritoResumen> {
  return this.http.get<{ carrito: CarritoResumen }>(API_URL, { withCredentials: true }).pipe(
    map(respuesta => respuesta.carrito),
    tap(resumen => this.carritoSubject.next(resumen))
    );
  }

  agregar(idProducto: number, cantidad: number = 1): Observable<{ mensaje: string, carrito: CarritoResumen }> {
    return this.http.post<{ mensaje: string, carrito: CarritoResumen }>(`${API_URL}/agregar`,
      { idProducto, cantidad }, { withCredentials: true }
    ).pipe(
      tap(respuesta => this.carritoSubject.next(respuesta.carrito))
    );
  }

  actualizarCantidad(idProducto: number, accion: 'sumar' | 'restar'): Observable<{ mensaje: string, carrito: CarritoResumen }> {
    return this.http.put<{ mensaje: string, carrito: CarritoResumen }>(`${API_URL}/cantidad`,
      { idProducto, accion }, { withCredentials: true }
    ).pipe(
      tap(respuesta => this.carritoSubject.next(respuesta.carrito))
    );
  }

  quitar(idProducto: number): Observable<{ mensaje: string, carrito: CarritoResumen }> {
    return this.http.delete<{ mensaje: string, carrito: CarritoResumen }>(`${API_URL}/quitar/${idProducto}`,
      { withCredentials: true }
    ).pipe(
      tap(respuesta => this.carritoSubject.next(respuesta.carrito))
    );
  }

  vaciar(): Observable<{ mensaje: string, carrito: CarritoResumen }> {
    return this.http.delete<{ mensaje: string, carrito: CarritoResumen }>(`${API_URL}/vaciar`,
      { withCredentials: true }
    ).pipe(
      tap(respuesta => this.carritoSubject.next(respuesta.carrito))
    );
  }
}
