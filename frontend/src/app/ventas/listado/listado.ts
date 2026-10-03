import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Ventaservice } from '../ventaservice';
import { VentaPagina } from '../model/Venta';

@Component({
  selector: 'app-listado-ventas',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './listado.html',
  styleUrl: './listado.css',
})
export class Listado {

  pagina: VentaPagina = { contenido: [], paginaActual: 0, totalPaginas: 1, totalElementos: 0, primera: true, ultima: true };
  cargando = true;

  buscar = '';
  filtroComprobante = '';
  filtroPago = '';

  constructor(private ventaservice: Ventaservice, private cdr: ChangeDetectorRef) {
    this.cargar(0);
  }

  cargar(page: number) {
    this.cargando = true;
    this.ventaservice.listado(page).subscribe(respuesta => {
      this.pagina = respuesta.pagina;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get ventasFiltradas() {
    const t = this.buscar.trim().toLowerCase();
    return this.pagina.contenido.filter(v => {
      const coincideTexto = !t || v.nombreCliente.toLowerCase().includes(t) || (v.serie + v.correlativo).toLowerCase().includes(t);
      const coincideComprobante = !this.filtroComprobante || v.serie.startsWith(this.filtroComprobante);
      const coincidePago = !this.filtroPago || v.metodoPago === this.filtroPago;
      return coincideTexto && coincideComprobante && coincidePago;
    });
  }

  paginasArray(): number[] {
    return Array.from({ length: this.pagina.totalPaginas }, (_, i) => i);
  }
}
