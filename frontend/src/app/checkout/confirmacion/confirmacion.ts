import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Checkoutservice } from '../checkoutservice';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-confirmacion',
  imports: [CommonModule, RouterLink],
  templateUrl: './confirmacion.html',
  styleUrl: './confirmacion.css',
})
export class Confirmacion {

  pedido: any = null;
  cargando: boolean = true;
  backendUrl = 'http://localhost:8080';

  constructor(private route: ActivatedRoute, private checkoutservice: Checkoutservice, private cdr: ChangeDetectorRef,
    private toastservice: Toastservice) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.checkoutservice.obtenerConfirmacion(id).subscribe(respuesta => {
      this.pedido = respuesta.pedido;
      this.cargando = false;
      this.toastservice.exito('Pedido registrado con éxito');
      this.cdr.detectChanges();
    });
  }
}
