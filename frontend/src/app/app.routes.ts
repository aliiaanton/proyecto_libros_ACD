import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { Library } from './components/library/library.component';
import { BlindDate } from './components/blind-date/blind-date.component';
import { RegistrationConfirmationComponent } from './components/registration-confirmation/registration-confirmation.component';
import { EmailVerificationComponent } from './components/email-verification/email-verification.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },           // Página principal (Home)
  { path: 'login', component: LoginComponent },     // Página Login
  { path: 'register', component: RegisterComponent }, // Página Registro
  { path: 'registration-confirmation', component: RegistrationConfirmationComponent }, // Confirmación registro
  { path: 'verify-email', component: EmailVerificationComponent }, // Verificación email
  { path: 'library', component: Library }, // Mi Biblioteca
  { path: 'blind-date', component: BlindDate }, // Cita a ciegas
  { path: '**', redirectTo: '' }                    // Si pone una ruta rara, volver al Home
];