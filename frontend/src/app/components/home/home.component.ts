import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BookService } from '../../services/book.service';
import { Book } from '../../models/book.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  query: string = '';
  books: Book[] = [];
  errorMessage: string = '';

  constructor(private bookService: BookService) {}

  search() {
    if (!this.query.trim()) return;
    this.bookService.searchBooks(this.query).subscribe({
      next: (data) => this.books = data,
      error: (err) => this.errorMessage = 'Error conectando al servidor'
    });
  }
}