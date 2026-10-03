import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Adminservice, Reportes } from '../../admin/adminservice';

@Component({
  selector: 'app-reportes',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reportes.html',
  styleUrl: './reportes.css',
})
export class ReportesComponent {

  reportes: Reportes = { ventas: [], cantidadVentas: 0, totalVentas: 0, pedidos: [], cantidadPedidos: 0, totalPedidos: 0 };
  cargando = true;

  ventasDesde = '';
  ventasHasta = '';
  pedidosDesde = '';
  pedidosHasta = '';

  constructor(private adminservice: Adminservice, private cdr: ChangeDetectorRef) {
    this.cargar();
  }

  cargar() {
    this.adminservice.reportes(this.ventasDesde, this.ventasHasta, this.pedidosDesde, this.pedidosHasta).subscribe(respuesta => {
      this.reportes = respuesta.reportes;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  filtrarVentas() {
    this.cargar();
  }

  limpiarVentas() {
    this.ventasDesde = '';
    this.ventasHasta = '';
    this.cargar();
  }

  filtrarPedidos() {
    this.cargar();
  }

  limpiarPedidos() {
    this.pedidosDesde = '';
    this.pedidosHasta = '';
    this.cargar();
  }
}
