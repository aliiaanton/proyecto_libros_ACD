import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginData: LoginRequest = { email: '', password: '' };
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.authService.login(this.loginData).subscribe({
      next: () => {
        this.router.navigate(['/']); // Al Home si todo va bien
      },
      error: (err) => {
        // Manejo específico de errores
        if (err.status === 403 && err.error?.message?.includes('verificad')) {
          this.errorMessage = 'Debes verificar tu email antes de iniciar sesión. Revisa tu correo.';
        } else if (err.status === 401 || err.status === 403) {
          this.errorMessage = 'Credenciales incorrectas';
        } else {
          this.errorMessage = 'Error al iniciar sesión. Por favor, inténtalo de nuevo.';
        }
        console.error(err);
      }
    });
  }
}