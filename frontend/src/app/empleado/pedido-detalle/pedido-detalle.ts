import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Empleadoservice, PedidoConfirmacion } from '../empleadoservice';

@Component({
  selector: 'app-pedido-detalle-empleado',
  imports: [CommonModule, RouterLink],
  templateUrl: './pedido-detalle.html',
  styleUrl: './pedido-detalle.css',
})
export class PedidoDetalle {

  pedido: PedidoConfirmacion | null = null;
  cargando = true;
  backendUrl = 'http://localhost:8080';

  constructor(private route: ActivatedRoute, private empleadoservice: Empleadoservice, private cdr: ChangeDetectorRef) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.empleadoservice.pedidoDetalle(id).subscribe(respuesta => {
      this.pedido = respuesta.pedido;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }
}
