import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Adminservice, DashboardData } from '../adminservice';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {

  datos: DashboardData = { totalProductos: 0, stockBajo: [] };
  cargando: boolean = true;
  filtro: string = '';

  constructor(private adminservice: Adminservice, private cdr: ChangeDetectorRef) {
    this.adminservice.dashboard().subscribe(datos => {
      this.datos = datos;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get stockFiltrado() {
    const termino = this.filtro.trim().toLowerCase();
    if (!termino) return this.datos.stockBajo;
    return this.datos.stockBajo.filter(p => p.nombre.toLowerCase().includes(termino));
  }
}
