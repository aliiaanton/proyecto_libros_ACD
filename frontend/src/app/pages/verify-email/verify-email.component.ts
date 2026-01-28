import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.scss']
})

export class VerifyEmailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private authService = inject(AuthService);
  
  // Asegúrate de que status empieza en LOADING
  status: 'LOADING' | 'SUCCESS' | 'ERROR' = 'LOADING';
  message: string = '';

  ngOnInit() {
    const token = this.route.snapshot.queryParams['token'];
    console.log('Token recibido:', token); // DEBUG

    if (token) {
      this.verifyToken(token);
    } else {
      this.status = 'ERROR';
      this.message = 'No se encontró el token de verificación.';
    }
  }

  verifyToken(token: string) {
    this.authService.verifyEmail(token).subscribe({
      next: (responseText) => {
        console.log('Respuesta del Backend:', responseText); // DEBUG
        
        // Forzamos el cambio de estado
        this.status = 'SUCCESS'; 
        this.message = responseText; // "Email verificado correctamente..."
      },
      error: (err) => {
        console.error('Error verificando:', err); // DEBUG
        
        this.status = 'ERROR';
        // A veces el error contiene el mensaje de texto si falla el parseo
        // Intentamos sacar el mensaje más útil posible
        this.message = err.error || err.statusText || 'Ha ocurrido un error al verificar.';
      }
    });
  }
}