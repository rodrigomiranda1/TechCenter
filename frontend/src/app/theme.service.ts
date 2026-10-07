import { Injectable, effect, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly KEY = 'techcenter-theme';

  readonly theme = signal<Theme>(this.getInitialTheme());

  constructor() {
    effect(() => {
      const t = this.theme();
      const root = document.documentElement;
      root.setAttribute('data-theme', t);
      root.setAttribute('data-bs-theme', t); // lo usa Bootstrap 5.3+
      try { localStorage.setItem(this.KEY, t); } catch {}
    });
  }

  toggle() {
    this.theme.update(t => (t === 'dark' ? 'light' : 'dark'));
  }

  private getInitialTheme(): Theme {
    try {
      const saved = localStorage.getItem(this.KEY);
      if (saved === 'light' || saved === 'dark') return saved;
    } catch {}
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }
}