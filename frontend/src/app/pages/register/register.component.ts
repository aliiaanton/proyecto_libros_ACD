import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { BookService } from '../../core/services/book.service';
import { Genre, Tag } from '../../core/models/book.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private bookService = inject(BookService);
  private router = inject(Router);

  // Datos para los selectores
  availableGenres: Genre[] = [];
  availableTags: Tag[] = [];
  
  // Arrays para guardar lo que el usuario selecciona
  selectedGenreIds: number[] = [];
  selectedTagIds: number[] = [];

  errorMessage: string = '';
  successMessage: string = '';

  registerForm = this.fb.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  ngOnInit() {
    // Cargar opciones del backend
    this.bookService.getGenres().subscribe(data => this.availableGenres = data);
    this.bookService.getTags().subscribe(data => this.availableTags = data);
  }

  // Métodos para manejar la selección de chips
  toggleGenre(id: number) {
    if (this.selectedGenreIds.includes(id)) {
      this.selectedGenreIds = this.selectedGenreIds.filter(g => g !== id);
    } else {
      this.selectedGenreIds.push(id);
    }
  }

  toggleTag(id: number) {
    if (this.selectedTagIds.includes(id)) {
      this.selectedTagIds = this.selectedTagIds.filter(t => t !== id);
    } else {
      this.selectedTagIds.push(id);
    }
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const request = {
        username: this.registerForm.value.username!,
        email: this.registerForm.value.email!,
        password: this.registerForm.value.password!,
        genrePreferenceIds: this.selectedGenreIds,
        tagPreferenceIds: this.selectedTagIds
      };

      this.authService.register(request).subscribe({
        next: () => {
          this.successMessage = '¡Cuenta creada! Revisa tu email para verificarla 💌';
          // Opcional: Redirigir tras unos segundos
          setTimeout(() => this.router.navigate(['/login']), 3000);
        },
        error: (err) => {
          this.errorMessage = 'Hubo un problema al registrarte. ¿Quizás el usuario ya existe?';
        }
      });
    }
  }
}