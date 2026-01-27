import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  
  registerData: RegisterRequest = { username: '', email: '', password: '' };
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    // Validar que las contraseñas coincidan
    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    // Validar longitud mínima de contraseña
    if (this.registerData.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres.';
      return;
    }

    this.authService.register(this.registerData).subscribe({
      next: () => {
        // Redirigir a la página de confirmación con el email
        this.router.navigate(['/registration-confirmation'], {
          state: { email: this.registerData.email }
        });
      },
      error: (err) => {
        if (err.status === 409) {
          this.errorMessage = 'El email o usuario ya están registrados.';
        } else {
          this.errorMessage = 'Error al registrarse. Por favor, inténtalo de nuevo.';
        }
        console.error(err);
      }
    });
  }
}