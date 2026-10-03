import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Toastservice, ToastMessage } from '../toastservice';

@Component({
  selector: 'app-toast',
  imports: [CommonModule],
  templateUrl: './toast.html',
  styleUrl: './toast.css',
})
export class Toast {

  toasts: ToastMessage[] = [];

  constructor(private toastservice: Toastservice, private cdr: ChangeDetectorRef) {
    this.toastservice.toasts$.subscribe(toasts => {
      this.toasts = toasts;
      setTimeout(() => this.cdr.detectChanges());
    });
  }

  cerrar(id: number) {
    this.toastservice.quitar(id);
  }
}
