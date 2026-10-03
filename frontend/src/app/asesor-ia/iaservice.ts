import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConsultaIa } from './model/ConsultaIa';

const API_URL = 'http://localhost:8080/api/cliente/ia';

interface RespuestaIa {
  pregunta: string;
  respuesta: string;
}

interface HistorialIa {
  consultas: ConsultaIa[];
  historialPersistente: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class Iaservice {

  constructor(private http: HttpClient) { }

  preguntar(pregunta: string): Observable<RespuestaIa> {
    return this.http.post<RespuestaIa>(`${API_URL}/preguntar`, { pregunta }, { withCredentials: true });
  }

  historial(): Observable<HistorialIa> {
    return this.http.get<HistorialIa>(`${API_URL}/historial`, { withCredentials: true });
  }
}
