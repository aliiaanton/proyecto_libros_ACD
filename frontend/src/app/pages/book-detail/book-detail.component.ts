import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../core/services/book.service';
import { LibraryService } from '../../core/services/library.service';
import { ReviewService } from '../../core/services/review.service';
import { AuthService } from '../../core/services/auth.service';
import { Book } from '../../core/models/book.model';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './book-detail.component.html',
  styleUrls: ['./book-detail.component.scss']
})
export class BookDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private bookService = inject(BookService);
  private libraryService = inject(LibraryService);
  private reviewService = inject(ReviewService);
  public authService = inject(AuthService); // Público para usarlo en el HTML

  book: Book | null = null;
  reviews: any[] = [];
  isLoading = true;
  
  // Estado de lectura
  currentStatus: string = '';
  statusOptions = [
    { value: 'WANT_TO_READ', label: 'Quiero leerlo 🌸' },
    { value: 'READING', label: 'Leyendo ahora 📖' },
    { value: 'READ', label: 'Leído y terminado ✅' },
    { value: 'DROPPED', label: 'Abandonado 🥀' }
  ];

  // Formulario de reseña
  newReview = { rating: 5, comment: '' };
  isSubmittingReview = false;

  ngOnInit() {
    // Obtenemos el ID de la URL (puede ser ID local o de Google)
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadBook(id);
    }
  }

  loadBook(id: string) {
    // Nota: Tu backend busca por ID numérico en /api/books/{id}. 
    // Si usas GoogleID en la URL, necesitarás ajustar el backend o buscar por GoogleID.
    // Asumiremos que el backend es listo o que pasamos el ID numérico si lo tenemos.
    // Para simplificar, usaremos la búsqueda de Google si parece un ID de texto, o numérica si es número.
    
    // En este ejemplo, llamamos al endpoint de detalle.
    // IMPORTANTE: Asegúrate de que tu BookService maneje bien si es ID numérico o string.
    this.bookService.getBookById(Number(id)).subscribe({ // Si falla por ser string, usa searchBooks
      next: (data) => {
        this.book = data;
        this.currentStatus = data.userReadingStatus || ''; // Si el backend lo devuelve
        this.loadReviews(data.googleBookId);
        this.isLoading = false;
      },
      error: () => {
         // Fallback: Si no existe en DB local, búscalo en Google (para libros nuevos)
         this.bookService.searchBooks(id).subscribe(books => {
            if(books.length > 0) {
                this.book = books[0];
                this.loadReviews(this.book.googleBookId);
            }
            this.isLoading = false;
         });
      }
    });
  }

  loadReviews(googleBookId: string) {
    this.reviewService.getReviews(googleBookId).subscribe(data => {
      this.reviews = data;
    });
  }

  // Cambiar estado en la estantería
  onStatusChange(event: any) {
    if (!this.book || !this.authService.isLoggedIn()) return;
    
    const newStatus = event.target.value;
    // Necesitamos el userId. En una app real, decodificamos el token.
    // Por simplicidad, asumiremos que tenemos un método para obtener el ID o el backend lo saca del token.
    // Aquí usaremos un ID placeholder "1" o lo que tengas en localStorage si guardaste el ID.
    // Lo ideal: El backend saca el ID del token JWT. Mandamos 0 o null como ID.
    const userId = 1; // TODO: Sacar del AuthService o Token

    this.libraryService.updateStatus(userId, this.book.googleBookId, newStatus).subscribe({
      next: () => alert('¡Estantería actualizada! 📚'),
      error: () => alert('Error al guardar estado')
    });
  }

  // Enviar reseña
  submitReview() {
    if (!this.book) return;
    this.isSubmittingReview = true;
    
    const reviewData = {
        userId: 1, // TODO: Sacar del token
        googleBookId: this.book.googleBookId,
        rating: this.newReview.rating,
        comment: this.newReview.comment
    };

    this.reviewService.createReview(reviewData).subscribe({
        next: (res) => {
            this.reviews.push(res); // Añadir a la lista visualmente
            this.newReview = { rating: 5, comment: '' }; // Limpiar form
            this.isSubmittingReview = false;
        },
        error: () => {
            alert('Error enviando reseña');
            this.isSubmittingReview = false;
        }
    });
  }
}