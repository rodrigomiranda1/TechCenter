import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Authservice } from '../auth/authservice';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Usuario } from '../auth/model/Usuario';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  usuario$: Observable<Usuario | null>;

  constructor(private authservice: Authservice, private router: Router) {
    this.usuario$ = this.authservice.currentUser$;
  }

  esEmpleado(usuario: Usuario): boolean {
    return usuario.roles?.includes('EMPLEADO') ?? false;
  }

  cerrarSesion() {
    this.authservice.logout().subscribe(() => {
      this.router.navigate(['/login']);
    });
  }
}
