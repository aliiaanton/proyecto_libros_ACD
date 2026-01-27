import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration-confirmation',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './registration-confirmation.component.html',
  styleUrl: './registration-confirmation.component.css'
})
export class RegistrationConfirmationComponent {
  
  email: string = '';

  constructor(private router: Router) {
    // Obtenemos el email del state de la navegación
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.email = navigation.extras.state['email'] || '';
    }
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
