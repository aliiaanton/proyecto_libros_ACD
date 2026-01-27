import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms';   
import { BookService } from './services/book.service'; // Fíjate que ahora importamos .service
import { Book } from './models/book.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './app.component.html', 
  styleUrl: './app.component.css'     
})
export class AppComponent {
  title = 'BookMatch';
  query: string = '';
  books: Book[] = [];
  errorMessage: string = '';

  constructor(private bookService: BookService) {}

  search() {
    if (!this.query.trim()) return;

    this.bookService.searchBooks(this.query).subscribe({
      next: (data) => {
        this.books = data;
        this.errorMessage = '';
      },
      error: (err) => {
        console.error('Error conectando con el backend', err);
        this.errorMessage = 'No se pudo conectar con el servidor Spring Boot.';
      }
    });
  }
}