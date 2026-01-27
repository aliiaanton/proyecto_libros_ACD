import { Component, OnInit } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common'; // Importante para el *ngIf
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  isLoggedIn: boolean = false;
  username: string | null = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Nos suscribimos para saber en tiempo real si cambia el estado
    this.authService.isLoggedIn$.subscribe(status => {
      this.isLoggedIn = status;
      this.username = localStorage.getItem('username');
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}