import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Adminservice, AuditoriaItem } from '../adminservice';

@Component({
  selector: 'app-auditoria',
  imports: [CommonModule],
  templateUrl: './auditoria.html',
  styleUrl: './auditoria.css',
})
export class Auditoria {

  auditorias: AuditoriaItem[] = [];
  cargando = true;

  constructor(private adminservice: Adminservice, private cdr: ChangeDetectorRef) {
    this.adminservice.auditoria().subscribe(respuesta => {
      this.auditorias = respuesta.auditorias;
      this.cargando = false;
      this.cdr.detectChanges();
    });
  }
}
