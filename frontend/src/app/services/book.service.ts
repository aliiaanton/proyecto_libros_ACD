import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Book } from '../models/book.model';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  
  // URL de tu Backend Spring Boot
  private apiUrl = 'http://localhost:8080/api/books';

  constructor(private http: HttpClient) { }

  // Método para buscar libros
  searchBooks(query: string): Observable<Book[]> {
    return this.http.get<Book[]>(`${this.apiUrl}/search?query=${query}`);
  }
}