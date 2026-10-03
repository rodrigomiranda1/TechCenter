import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categoria, Marca, ProductoAdmin } from './model/CatalogoAdmin';

const API_URL = 'http://localhost:8080/api/operaciones';

export interface CatalogoAdminData {
  productos: ProductoAdmin[];
  categorias: Categoria[];
  marcas: Marca[];
  paginaActual: number;
  totalPaginas: number;
  totalProductos: number;
}

@Injectable({
  providedIn: 'root'
})
export class Catalogoadminservice {

  constructor(private http: HttpClient) { }

  catalogo(pagina: number = 1): Observable<CatalogoAdminData> {
    return this.http.get<CatalogoAdminData>(`${API_URL}/catalogo?pagina=${pagina}`, { withCredentials: true });
  }

  crearProducto(p: ProductoAdmin): Observable<any> {
    return this.http.post(`${API_URL}/productos`, p, { withCredentials: true });
  }
  actualizarProducto(id: number, p: ProductoAdmin): Observable<any> {
    return this.http.put(`${API_URL}/productos/${id}`, p, { withCredentials: true });
  }
  activarProducto(id: number): Observable<any> {
    return this.http.put(`${API_URL}/productos/${id}/activar`, {}, { withCredentials: true });
  }
  desactivarProducto(id: number): Observable<any> {
    return this.http.put(`${API_URL}/productos/${id}/desactivar`, {}, { withCredentials: true });
  }
  eliminarProducto(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/productos/${id}`, { withCredentials: true });
  }

  crearCategoria(c: Categoria): Observable<any> {
    return this.http.post(`${API_URL}/categorias`, c, { withCredentials: true });
  }
  activarCategoria(id: number): Observable<any> {
    return this.http.put(`${API_URL}/categorias/${id}/activar`, {}, { withCredentials: true });
  }
  desactivarCategoria(id: number): Observable<any> {
    return this.http.put(`${API_URL}/categorias/${id}/desactivar`, {}, { withCredentials: true });
  }

  crearMarca(m: Marca): Observable<any> {
    return this.http.post(`${API_URL}/marcas`, m, { withCredentials: true });
  }
  activarMarca(id: number): Observable<any> {
    return this.http.put(`${API_URL}/marcas/${id}/activar`, {}, { withCredentials: true });
  }
  desactivarMarca(id: number): Observable<any> {
    return this.http.put(`${API_URL}/marcas/${id}/desactivar`, {}, { withCredentials: true });
  }
  eliminarMarca(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/marcas/${id}`, { withCredentials: true });
  }
}
