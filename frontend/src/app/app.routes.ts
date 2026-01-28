import { Routes } from '@angular/router';

// Importación de los componentes (Páginas)
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { BlindDateComponent } from './pages/blind-date/blind-date.component';
import { BookDetailComponent } from './pages/book-detail/book-detail.component';
import { QuizComponent } from './pages/quiz/quiz.component';
import { MyLibraryComponent } from './pages/my-library/my-library.component';
import { VerifyEmailComponent } from './pages/verify-email/verify-email.component';
import { SearchComponent } from './pages/search/search.component';

export const routes: Routes = [
  // Redirección inicial: Si entran a la raíz, van al Home
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // Rutas Públicas
  { path: 'home', component: HomeComponent, title: 'Inicio | BookMatch 🎀' },
  { path: 'login', component: LoginComponent, title: 'Ingresar | BookMatch' },
  { path: 'register', component: RegisterComponent, title: 'Registro | BookMatch' },
  { path: 'blind-date', component: BlindDateComponent, title: 'Cita a Ciegas 💌' },
  { path: 'quiz', component: QuizComponent, title: 'El Oráculo Literario ✨' },
  { path: 'my-library', component: MyLibraryComponent, title: 'Mi Biblioteca 📚' },
  { path: 'verify-email', component: VerifyEmailComponent, title: 'Verificando... ⏳' },
  { path: 'search', component: SearchComponent, title: 'Resultados 🔎' },
  
  // Ruta de Detalle (acepta un ID dinámico)
  { path: 'book/:id', component: BookDetailComponent, title: 'Detalle del Libro 📖' },

  // Rutas Pendientes (Las crearemos en los siguientes pasos, las dejo comentadas para que no den error)
  // { path: 'quiz', component: QuizComponent },
  // { path: 'my-library', component: MyLibraryComponent, canActivate: [authGuard] },
  
  // Wildcard: Cualquier ruta desconocida redirige al Home
  { path: '**', redirectTo: 'home' }
];