import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Authservice } from '../auth/authservice';
import { ThemeService } from '../shared/theme.service';
import { Usuario } from '../auth/model/Usuario';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  usuario$: Observable<Usuario | null>;
  theme$: Observable<'light' | 'dark'>;

  constructor(
    private auth: Authservice,
    private router: Router,
    private theme: ThemeService
  ) {
    this.usuario$ = this.auth.currentUser$;
    this.theme$ = this.theme.theme$;
  }

  toggleTheme() {
    this.theme.toggle();
  }

  cerrarSesion() {
    this.auth.logout().subscribe(() => this.router.navigate(['/inicio']));
  }
}