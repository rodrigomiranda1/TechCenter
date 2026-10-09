import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

type Theme = 'light' | 'dark';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly KEY = 'theme';
  private themeSubject = new BehaviorSubject<Theme>(this.initialTheme());
  theme$ = this.themeSubject.asObservable();

  constructor() {
    this.apply(this.themeSubject.value);
  }

  toggle(): void {
    const next: Theme = this.themeSubject.value === 'dark' ? 'light' : 'dark';
    this.themeSubject.next(next);
    this.apply(next);
    localStorage.setItem(this.KEY, next);
  }

  private initialTheme(): Theme {
    const saved = localStorage.getItem(this.KEY) as Theme | null;
    if (saved) return saved;
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  }

private apply(theme: Theme): void {
  const root = document.documentElement;
  root.setAttribute('data-theme', theme);   
  root.setAttribute('data-bs-theme', theme);   
}
}