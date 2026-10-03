import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Compra, ProveedorSelect, ProductoSelect } from './model/Compra';

const API_URL = 'http://localhost:8080/api/operaciones/compras';

export interface ComprasData {
  compras: Compra[];
  proveedores: ProveedorSelect[];
  productos: ProductoSelect[];
}

@Injectable({
  providedIn: 'root'
})
export class Comprasservice {

  constructor(private http: HttpClient) { }

  listar(): Observable<ComprasData> {
    return this.http.get<ComprasData>(API_URL, { withCredentials: true });
  }

  registrar(idProveedor: number, idProducto: number, cantidad: number, costoUnitario: number): Observable<any> {
    return this.http.post(API_URL, { idProveedor, idProducto, cantidad, costoUnitario }, { withCredentials: true });
  }
}
