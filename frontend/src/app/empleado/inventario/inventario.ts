import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Empleadoservice, InventarioData } from '../empleadoservice';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-inventario',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './inventario.html',
  styleUrl: './inventario.css',
})
export class Inventario {

  datos: InventarioData = { movimientos: [], productos: [], stockBajo: [] };
  cargando = true;
  filtro = '';

  idProducto?: number;
  tipoMovimiento = 'ENTRADA';
  cantidad?: number;
  observacion = '';

  constructor(private empleadoservice: Empleadoservice, private cdr: ChangeDetectorRef, private toastservice: Toastservice) {
    this.cargar();
  }

  cargar() {
    this.empleadoservice.inventario().subscribe(respuesta => {
      this.datos = respuesta.inventario;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get movimientosFiltrados() {
    const t = this.filtro.trim().toLowerCase();
    if (!t) return this.datos.movimientos;
    return this.datos.movimientos.filter(m => m.nombreProducto.toLowerCase().includes(t));
  }

  registrar() {
    if (!this.idProducto || !this.cantidad) {
      this.toastservice.error('Completa producto y cantidad');
      return;
    }
    this.empleadoservice.registrarMovimiento(this.idProducto, this.tipoMovimiento, this.cantidad, this.observacion).subscribe({
      next: () => {
        this.toastservice.exito('Movimiento registrado correctamente');
        this.idProducto = undefined;
        this.cantidad = undefined;
        this.observacion = '';
        this.cargar();
      },
      error: (err) => this.toastservice.error(err.error?.mensaje || 'Error al registrar el movimiento')
    });
  }
}
