import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Iaservice } from './iaservice';
import { ConsultaIa } from './model/ConsultaIa';

interface MensajeChat {
  rol: 'bot' | 'user';
  texto: string;
}

@Component({
  selector: 'app-asesor-ia',
  imports: [CommonModule, FormsModule],
  templateUrl: './asesor-ia.html',
  styleUrl: './asesor-ia.css',
})
export class AsesorIa {

  mensajes: MensajeChat[] = [
    { rol: 'bot', texto: 'Hola. Dime qué producto necesitas y te sugeriré productos del inventario y cuidados importantes.' }
  ];

  pregunta: string = '';
  enviando: boolean = false;

  consultas: ConsultaIa[] = [];
  historialPersistente: boolean = false;

 sugerencias = [
  { texto: 'Componentes para mi PC', prompt: 'Dime los mejores procesadores, tarjetas gráficas o placas madre' },
  { texto: 'Laptops', prompt: 'Busco recomendaciones de laptops gamer con buena relación calidad-precio' },
  { texto: 'Audífonos o Parlantes', prompt: 'Mejores audífonos o parlantes para la casa' },
  { texto: 'Consolas y Controles', prompt: '¿Que consolas o controles me recomiendas?' },
];
  constructor(private iaservice: Iaservice, private cdr: ChangeDetectorRef) {
    this.cargarHistorial();
  }

  cargarHistorial() {
    this.iaservice.historial().subscribe(respuesta => {
      this.consultas = respuesta.consultas;
      this.historialPersistente = respuesta.historialPersistente;
      this.cdr.detectChanges();
    });
  }

  usarSugerencia(prompt: string) {
    this.pregunta = prompt;
  }

  enviar() {
    if (!this.pregunta.trim() || this.enviando) return;

    const preguntaEnviada = this.pregunta.trim();
    this.mensajes.push({ rol: 'user', texto: preguntaEnviada });
    this.pregunta = '';
    this.enviando = true;
    this.cdr.detectChanges();

    this.iaservice.preguntar(preguntaEnviada).subscribe({
      next: (respuesta) => {
        this.mensajes.push({ rol: 'bot', texto: respuesta.respuesta });
        this.enviando = false;
        this.cargarHistorial();
        this.cdr.detectChanges();
      },
      error: () => {
        this.mensajes.push({ rol: 'bot', texto: 'Ocurrió un error al procesar tu consulta. Intenta de nuevo.' });
        this.enviando = false;
        this.cdr.detectChanges();
      }
    });
  }
}
