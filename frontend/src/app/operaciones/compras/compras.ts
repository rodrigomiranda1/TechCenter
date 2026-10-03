import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Comprasservice, ComprasData } from './comprasservice';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-compras',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './compras.html',
  styleUrl: './compras.css',
})
export class Compras {

  datos: ComprasData = { compras: [], proveedores: [], productos: [] };
  cargando = true;
  filtro = '';

  idProveedor?: number;
  idProducto?: number;
  cantidad?: number;
  costoUnitario?: number;

  constructor(private comprasservice: Comprasservice, private cdr: ChangeDetectorRef, private toastservice: Toastservice) {
    this.cargar();
  }

  cargar() {
    this.comprasservice.listar().subscribe(datos => {
      this.datos = datos;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get comprasFiltradas() {
    const t = this.filtro.trim().toLowerCase();
    if (!t) return this.datos.compras;
    return this.datos.compras.filter(c => c.nombreProveedor.toLowerCase().includes(t));
  }

  registrar() {
    if (!this.idProveedor || !this.idProducto || !this.cantidad || this.costoUnitario == null) {
      this.toastservice.error('Completa todos los campos');
      return;
    }
    this.comprasservice.registrar(this.idProveedor, this.idProducto, this.cantidad, this.costoUnitario).subscribe({
      next: () => {
        this.toastservice.exito('Compra registrada y stock actualizado');
        this.idProveedor = undefined;
        this.idProducto = undefined;
        this.cantidad = undefined;
        this.costoUnitario = undefined;
        this.cargar();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al registrar la compra')
    });
  }
}
