import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-email-verification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './email-verification.component.html',
  styleUrl: './email-verification.component.css'
})
export class EmailVerificationComponent implements OnInit {
  
  verificationStatus: 'pending' | 'success' | 'error' = 'pending';
  message: string = 'Verificando tu email...';

  constructor(
    private route: ActivatedRoute, 
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit() {
    // Obtener el token de verificación de la URL
    const token = this.route.snapshot.queryParamMap.get('token');
    
    if (token) {
      this.verifyEmail(token);
    } else {
      this.verificationStatus = 'error';
      this.message = 'Token de verificación no encontrado.';
    }
  }

  verifyEmail(token: string) {
    this.authService.verifyEmail(token).subscribe({
      next: () => {
        this.verificationStatus = 'success';
        this.message = '¡Tu email ha sido verificado correctamente! Ya puedes iniciar sesión.';
      },
      error: (err) => {
        this.verificationStatus = 'error';
        this.message = 'Error al verificar el email. El token puede haber expirado o ser inválido.';
        console.error(err);
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
