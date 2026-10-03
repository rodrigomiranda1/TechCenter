import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Authservice } from '../auth/authservice';
import { Observable } from 'rxjs';
import { Usuario } from '../auth/model/Usuario';

@Component({
  selector: 'app-inicio',
  imports: [CommonModule],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {
  usuario$: Observable<Usuario | null>;

  constructor(private authservice: Authservice) {
    this.usuario$ = this.authservice.currentUser$;
  }
}
