import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Empleadoservice, PanelEmpleado } from '../empleadoservice';

@Component({
  selector: 'app-panel-empleado',
  imports: [CommonModule, RouterLink],
  templateUrl: './panel.html',
  styleUrl: './panel.css',
})
export class Panel {

  panel: PanelEmpleado = {
    fechaActual: '', ventasHoy: 0, movimientosHoy: 0, stockBajo: [],
    totalVentas: 0, totalPedidos: 0, totalProductos: 0, totalMovimientos: 0, ultimasVentas: []
  };
  cargando = true;

  constructor(private empleadoservice: Empleadoservice, private cdr: ChangeDetectorRef) {
    this.empleadoservice.panel().subscribe(respuesta => {
      this.panel = respuesta.panel;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }
}
