import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Empleadoservice, PedidoResumen } from '../empleadoservice';

@Component({
  selector: 'app-empleado-pedidos',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pedidos.html',
  styleUrl: './pedidos.css',
})
export class Pedidos {

  pedidos: PedidoResumen[] = [];
  filtro = '';
  cargando = true;

  constructor(private empleadoservice: Empleadoservice, private cdr: ChangeDetectorRef) {
    this.empleadoservice.pedidos().subscribe(respuesta => {
      this.pedidos = respuesta.pedidos;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  get pedidosFiltrados() {
    const t = this.filtro.trim().toLowerCase();
    if (!t) return this.pedidos;
    return this.pedidos.filter(p => p.idPedido.toString().includes(t) || p.nombreCliente.toLowerCase().includes(t));
  }
}
