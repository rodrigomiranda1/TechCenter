import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { Checkoutservice } from '../checkoutservice';
import { Toastservice } from '../../shared/toastservice';

@Component({
  selector: 'app-pago',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pago.html',
  styleUrl: './pago.css',
})
export class Pago {

  direccionEntrega: string;
  tipoComprobante: string;
  metodoPago: string;

  numeroTarjeta: string = '';
  titular: string = '';
  vence: string = '';
  cvv: string = '';
  codigoOperacion: string = '';

  procesando: boolean = false;

  estadoOverlay: 'oculto' | 'procesando' | 'aprobado' = 'oculto';

  constructor(private checkoutservice: Checkoutservice, private router: Router, private toastservice: Toastservice) {
    const datos = this.checkoutservice.obtenerDatos();
    this.direccionEntrega = datos.direccionEntrega;
    this.tipoComprobante = datos.tipoComprobante;
    this.metodoPago = datos.metodoPago;
  }

  usarTarjetaDemo(numero: string) {
    this.numeroTarjeta = numero;
    this.titular = 'CLIENTE TECHCENTER';
    this.vence = '11/30';
    this.cvv = '123';
  }

  confirmar() {
    this.procesando = true;
    this.estadoOverlay = 'procesando';

    this.checkoutservice.confirmar({
      direccionEntrega: this.direccionEntrega,
      tipoComprobante: this.tipoComprobante,
      metodoPago: this.metodoPago,
      numeroTarjeta: this.numeroTarjeta,
      codigoOperacion: this.codigoOperacion
    }).subscribe({
      next: (respuesta) => {
        this.estadoOverlay = 'aprobado';
        setTimeout(() => {
          this.router.navigate(['/checkout/confirmacion', respuesta.idPedido]);
        }, 1400);
      },
      error: (err) => {
        this.procesando = false;
        this.estadoOverlay = 'oculto';
        this.toastservice.error(err.error?.mensaje || 'Error al procesar el pago');
      }
    });
  }
}
