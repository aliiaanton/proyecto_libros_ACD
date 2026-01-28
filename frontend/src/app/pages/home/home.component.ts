import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { BookService } from '../../core/services/book.service';
import { AuthService } from '../../core/services/auth.service';
import { BookCardComponent } from '../../shared/book-card/book-card.component';
import { HomeResponse } from '../../core/models/book.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, BookCardComponent],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  private bookService = inject(BookService);
  private authService = inject(AuthService); // Para saber si saludar al usuario
  private router = inject(Router);

  homeData: HomeResponse | null = null;
  searchQuery: string = '';
  currentUser = this.authService.currentUser;
  isLoading = true;

  ngOnInit() {
    this.loadHomeData();
  }

  loadHomeData() {
    this.bookService.getHome().subscribe({
      next: (data) => {
        this.homeData = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error cargando home:', err);
        this.isLoading = false;
      }
    });
  }

  onSearch() {
    if (this.searchQuery.trim()) {
      // Navegamos a una página de resultados (la crearemos más adelante)
      // Por ahora, puedes ver el parámetro en la URL
      this.router.navigate(['/search'], { queryParams: { q: this.searchQuery } });
    }
  }
}