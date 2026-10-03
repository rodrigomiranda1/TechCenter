import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Perfilservice } from './perfilservice';
import { ClientePerfil } from './model/ClientePerfil';

@Component({
  selector: 'app-perfil',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {

  perfil: ClientePerfil = new ClientePerfil();
  cargando: boolean = true;
  guardando: boolean = false;
  mensajeExito: string = '';
  mensajeError: string = '';

  constructor(private perfilservice: Perfilservice, private cdr: ChangeDetectorRef) {
    this.perfilservice.obtener().subscribe({
      next: (respuesta) => {
        this.perfil = respuesta.perfil;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  guardar() {
    this.mensajeExito = '';
    this.mensajeError = '';
    this.guardando = true;

    this.perfilservice.actualizar(this.perfil).subscribe({
      next: (respuesta) => {
        this.guardando = false;
        this.mensajeExito = respuesta.mensaje;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.guardando = false;
        this.mensajeError = err.error?.mensaje || 'Error al actualizar el perfil';
        this.cdr.detectChanges();
      }
    });
  }
}
