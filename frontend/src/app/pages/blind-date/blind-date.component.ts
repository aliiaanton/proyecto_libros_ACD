import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RecommendationService } from '../../core/services/recommendation.service';
import { BookService } from '../../core/services/book.service';
import { BlindDateResponse, Book } from '../../core/models/book.model';
import { BookCardComponent } from '../../shared/book-card/book-card.component';

@Component({
  selector: 'app-blind-date',
  standalone: true,
  imports: [CommonModule, RouterModule, BookCardComponent],
  templateUrl: './blind-date.component.html',
  styleUrls: ['./blind-date.component.scss']
})
export class BlindDateComponent implements OnInit {
  private recommendationService = inject(RecommendationService);
  private bookService = inject(BookService);

  currentDate: BlindDateResponse | null = null;
  revealedBook: Book | null = null;
  
  isLoading = true;
  isRevealing = false;
  isMatch = false; // Controla si ya se ha revelado

  ngOnInit() {
    this.loadNewDate();
  }

  // 1. Obtener una nueva cita aleatoria
  loadNewDate() {
    this.isLoading = true;
    this.isMatch = false;
    this.revealedBook = null;

    this.recommendationService.getBlindDate().subscribe({
      next: (data) => {
        this.currentDate = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error cargando cita:', err);
        this.isLoading = false;
      }
    });
  }

  // 2. El usuario hace clic en "¡Es un Match!"
  revealIdentity() {
    if (!this.currentDate) return;

    this.isRevealing = true;
    
    // Buscamos los detalles del libro usando su ID de Google
    // Usamos el endpoint de búsqueda que ya tienes configurado
    this.bookService.searchBooks(this.currentDate.googleBookId).subscribe({
      next: (books) => {
        if (books && books.length > 0) {
          this.revealedBook = books[0]; // Tomamos el primer resultado (es búsqueda exacta por ID)
          this.isMatch = true;
        }
        this.isRevealing = false;
      },
      error: () => {
        this.isRevealing = false;
        alert('No pudimos encontrar el libro... ¡Qué misterio!');
      }
    });
  }
}