import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientePerfil } from './model/ClientePerfil';

const API_URL = 'http://localhost:8080/api/cliente';

@Injectable({
  providedIn: 'root'
})
export class Perfilservice {

  constructor(private http: HttpClient) { }

  obtener(): Observable<{ perfil: ClientePerfil }> {
    return this.http.get<{ perfil: ClientePerfil }>(`${API_URL}/perfil`, { withCredentials: true });
  }

  actualizar(perfil: ClientePerfil): Observable<{ mensaje: string, perfil: ClientePerfil }> {
    return this.http.put<{ mensaje: string, perfil: ClientePerfil }>(`${API_URL}/perfil`, perfil, { withCredentials: true });
  }
}
