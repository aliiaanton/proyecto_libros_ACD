import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { BookService } from '../../core/services/book.service';
import { Book } from '../../core/models/book.model'; 
import { BookCardComponent } from '../../shared/book-card/book-card.component';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, RouterModule, BookCardComponent],
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private bookService = inject(BookService);
  
  query: string = '';
  books: Book[] = [];
  isLoading = false;
  hasSearched = false;

  ngOnInit() {
    // Suscribirse a los cambios en la URL (queryParams)
    this.route.queryParams.subscribe(params => {
      this.query = params['q'] || '';
      
      if (this.query) {
        this.performSearch(this.query);
      }
    });
  }

  performSearch(query: string) {
    this.isLoading = true;
    this.hasSearched = true;
    this.books = []; // Limpiar resultados anteriores

    this.bookService.searchBooks(query).subscribe({
      next: (data) => {
        this.books = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }
}