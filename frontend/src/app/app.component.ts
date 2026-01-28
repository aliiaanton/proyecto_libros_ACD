import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/navbar/navbar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent], // Importamos el Navbar aquí
  template: `
    <app-navbar></app-navbar> <!-- Lo colocamos aquí -->
    <router-outlet></router-outlet> <!-- Aquí cambiarán las páginas -->
  `
})
export class AppComponent {}