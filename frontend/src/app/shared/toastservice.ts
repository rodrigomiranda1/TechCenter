import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface ToastMessage {
  id: number;
  tipo: 'exito' | 'error';
  texto: string;
}

@Injectable({
  providedIn: 'root'
})
export class Toastservice {

  private contador = 0;
  private toastsSubject = new BehaviorSubject<ToastMessage[]>([]);
  toasts$ = this.toastsSubject.asObservable();

  exito(texto: string) {
    this.mostrar('exito', texto);
  }

  error(texto: string) {
    this.mostrar('error', texto);
  }

  quitar(id: number) {
    this.toastsSubject.next(this.toastsSubject.value.filter(t => t.id !== id));
  }

  private mostrar(tipo: 'exito' | 'error', texto: string) {
    const toast: ToastMessage = { id: ++this.contador, tipo, texto };
    this.toastsSubject.next([...this.toastsSubject.value, toast]);
    setTimeout(() => this.quitar(toast.id), 3500);
  }
}
