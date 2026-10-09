import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, of, tap } from 'rxjs';
import { LoginRequest } from './model/LoginRequest';
import { Usuario } from './model/Usuario';
import { environment } from '../../environments/environment';


const API_URL = `${environment.apiUrl}/auth`;

interface AuthResponse {
  mensaje: string;
  usuario: Usuario;
}

@Injectable({
  providedIn: 'root',
})
export class Authservice {

  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) { }

  login(usuario: string, password: string): Observable<AuthResponse> {
    const body = new LoginRequest(usuario, password);
    return this.http.post<AuthResponse>(`${API_URL}/login`, body, { withCredentials: true }).pipe(
      tap(respuesta => this.currentUserSubject.next(respuesta.usuario))
    );
  }

  register(username: string, email: string, password: string, nombres: string, apellidos: string): Observable<AuthResponse> {
    const body = { username, email, password, nombres, apellidos };
    return this.http.post<AuthResponse>(`${API_URL}/register`, body, { withCredentials: true });
  }

  me(): Observable<Usuario | null> {
    return this.http.get<AuthResponse>(`${API_URL}/me`, { withCredentials: true }).pipe(
      map(respuesta => respuesta.usuario),
      tap(usuario => this.currentUserSubject.next(usuario)),
      catchError(() => {
        this.currentUserSubject.next(null);
        return of(null);
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${API_URL}/logout`, {}, { withCredentials: true }).pipe(
      tap(() => this.currentUserSubject.next(null))
    );
  }
}