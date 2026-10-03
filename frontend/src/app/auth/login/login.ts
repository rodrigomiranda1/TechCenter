import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Authservice } from '../authservice';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  usuario: string = '';
  password: string = '';
  mensajeError: string = '';
  cargando: boolean = false;

  constructor(private authservice: Authservice, private router: Router, private cdr: ChangeDetectorRef) { }

  onSubmit() {
    this.mensajeError = '';
    this.cargando = true;

    this.authservice.login(this.usuario, this.password).subscribe({
      next: (respuesta) => {
        this.cargando = false;

        if (respuesta.usuario.roles.includes('EMPLEADO') && !respuesta.usuario.roles.includes('ADMIN')) {
          this.router.navigate(['/inicio']);
        } else {
          this.router.navigate(['/inicio']);
        }
      },
      error: (err) => {
        this.cargando = false;
        this.mensajeError = err.error?.mensaje || 'Error al iniciar sesión';
        setTimeout(() => this.cdr.detectChanges());
      }
    });
  }
}
