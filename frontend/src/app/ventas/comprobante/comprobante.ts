import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Ventaservice } from '../ventaservice';
import { VentaComprobante } from '../model/Venta';

@Component({
  selector: 'app-comprobante',
  imports: [CommonModule, RouterLink],
  templateUrl: './comprobante.html',
  styleUrl: './comprobante.css',
})
export class Comprobante {

  comprobante: VentaComprobante | null = null;
  cargando = true;
  backendUrl = 'http://localhost:8080';

  constructor(private route: ActivatedRoute, private ventaservice: Ventaservice, private cdr: ChangeDetectorRef) {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.ventaservice.comprobante(id).subscribe(respuesta => {
      this.comprobante = respuesta.comprobante;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }
}
