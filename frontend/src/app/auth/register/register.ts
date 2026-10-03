import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { Authservice } from '../authservice';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {

  nombres: string = '';
  apellidos: string = '';
  username: string = '';
  email: string = '';
  password: string = '';
  mensajeError: string = '';
  cargando: boolean = false;

  constructor(private authservice: Authservice, private router: Router) { }

  onSubmit() {
  this.mensajeError = '';
  this.cargando = true;

  this.authservice.register(this.username, this.email, this.password, this.nombres, this.apellidos).subscribe({
    next: () => {
      this.cargando = false;
      this.router.navigate(['/login']);
    },
    error: (err) => {
      this.cargando = false;
      this.mensajeError = err.error?.mensaje || 'Error al registrar la cuenta';
    }
  });
}
}
