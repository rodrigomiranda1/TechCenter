import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Carritoservice } from './carritoservice';
import { CarritoResumen, urlImagenCarrito } from './model/CarritoItem';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Checkoutservice } from '../checkout/checkoutservice';

@Component({
  selector: 'app-carrito',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './carrito.html',
  styleUrl: './carrito.css',
})
export class Carrito {

  protected readonly urlImagenCarrito = urlImagenCarrito;

  resumen: CarritoResumen = { items: [], total: 0 };
  cargando: boolean = true;

  metodoPago: string = 'TARJETA_DEMO';
  tipoComprobante: string = 'BOLETA';
  direccionEntrega: string = '';
  tipoEntrega: 'RECOJO' | 'DELIVERY' = 'RECOJO';

  constructor(private carritoservice: Carritoservice, private cdr: ChangeDetectorRef, private router: Router,
    private checkoutservice: Checkoutservice) {
    this.cargar();
  }

  cargar() {
    this.cargando = true;
    this.carritoservice.cargar().subscribe(resumen => {
      this.resumen = resumen;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }

  sumar(idProducto: number) {
    this.carritoservice.actualizarCantidad(idProducto, 'sumar').subscribe(respuesta => {
      this.resumen = respuesta.carrito;
      this.cdr.detectChanges();
    });
  }

  restar(idProducto: number) {
    this.carritoservice.actualizarCantidad(idProducto, 'restar').subscribe(respuesta => {
      this.resumen = respuesta.carrito;
      this.cdr.detectChanges();
    });
  }

  quitar(idProducto: number) {
    this.carritoservice.quitar(idProducto).subscribe(respuesta => {
      this.resumen = respuesta.carrito;
      this.cdr.detectChanges();
    });
  }

  vaciar() {
    this.carritoservice.vaciar().subscribe(respuesta => {
      this.resumen = respuesta.carrito;
      this.cdr.detectChanges();
    });
  }

  irAPago() {
    this.checkoutservice.guardarDatos({
      direccionEntrega: this.direccionEntrega,
      tipoComprobante: this.tipoComprobante,
      metodoPago: this.metodoPago
    });
    this.router.navigate(['/checkout/pago']);
  }

  seleccionarEntrega(tipo: 'RECOJO' | 'DELIVERY') {
  this.tipoEntrega = tipo;
  if (tipo === 'RECOJO') {
    this.direccionEntrega = '';
   }
  }

  seleccionarComprobante(tipo: string) {
    this.tipoComprobante = tipo;
  }

}
