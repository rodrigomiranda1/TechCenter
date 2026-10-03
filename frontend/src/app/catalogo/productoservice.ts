import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from './model/Producto';

const API_URL = 'http://localhost:8080/api/productos';

@Injectable({
  providedIn: 'root'
})
export class Productoservice {

  constructor(private http: HttpClient) { }

  listar(): Observable<Producto[]> {
    return this.http.get<Producto[]>(API_URL, { withCredentials: true });
  }

  buscarPorId(id: number): Observable<{ producto: Producto }> {
  return this.http.get<{ producto: Producto }>(`${API_URL}/${id}`, { withCredentials: true });
}
}
