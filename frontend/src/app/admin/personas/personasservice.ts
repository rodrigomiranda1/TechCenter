import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, Proveedor, Rol, UsuarioAdmin } from './model/Persona';

const API_URL = 'http://localhost:8080/api/admin';

@Injectable({
  providedIn: 'root'
})
export class Personasservice {

  constructor(private http: HttpClient) { }

  listarRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(`${API_URL}/roles`, { withCredentials: true });
  }


  listarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${API_URL}/clientes`, { withCredentials: true });
  }
  crearCliente(c: Cliente): Observable<any> {
    return this.http.post(`${API_URL}/clientes`, c, { withCredentials: true });
  }
  actualizarCliente(id: number, c: Cliente): Observable<any> {
    return this.http.put(`${API_URL}/clientes/${id}`, c, { withCredentials: true });
  }
  eliminarCliente(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/clientes/${id}`, { withCredentials: true });
  }


  listarProveedores(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(`${API_URL}/proveedores`, { withCredentials: true });
  }
  crearProveedor(p: Proveedor): Observable<any> {
    return this.http.post(`${API_URL}/proveedores`, p, { withCredentials: true });
  }
  actualizarProveedor(id: number, p: Proveedor): Observable<any> {
    return this.http.put(`${API_URL}/proveedores/${id}`, p, { withCredentials: true });
  }
  eliminarProveedor(id: number): Observable<any> {
    return this.http.delete(`${API_URL}/proveedores/${id}`, { withCredentials: true });
  }


  listarUsuarios(): Observable<UsuarioAdmin[]> {
    return this.http.get<UsuarioAdmin[]>(`${API_URL}/usuarios`, { withCredentials: true });
  }
  crearUsuario(u: UsuarioAdmin): Observable<any> {
    return this.http.post(`${API_URL}/usuarios`, u, { withCredentials: true });
  }
  actualizarUsuario(id: number, u: UsuarioAdmin): Observable<any> {
    return this.http.put(`${API_URL}/usuarios/${id}`, u, { withCredentials: true });
  }
  activarUsuario(id: number): Observable<any> {
    return this.http.put(`${API_URL}/usuarios/${id}/activar`, {}, { withCredentials: true });
  }
  desactivarUsuario(id: number): Observable<any> {
    return this.http.put(`${API_URL}/usuarios/${id}/desactivar`, {}, { withCredentials: true });
  }
}
