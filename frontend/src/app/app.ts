import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from './navbar/navbar';
import { Toast } from './shared/toast/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, Toast],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('TechCenter');
}
