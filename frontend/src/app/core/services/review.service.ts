import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/reviews';

  // Obtener reseñas de un libro
  getReviews(googleBookId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${googleBookId}`);
  }

  // Crear una reseña
  createReview(reviewData: { userId: number, googleBookId: string, rating: number, comment: string }): Observable<any> {
    return this.http.post(this.apiUrl, reviewData);
  }
}