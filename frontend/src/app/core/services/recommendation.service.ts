import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BlindDateResponse, RecommendationResponse } from '../models/book.model';

@Injectable({
  providedIn: 'root'
})
export class RecommendationService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/recommendations';

  // Obtener una Cita a Ciegas aleatoria
  getBlindDate(): Observable<BlindDateResponse> {
    return this.http.get<BlindDateResponse>(`${this.apiUrl}/blind-date`);
  }

  // Obtener recomendaciones personalizadas (Requiere estar logueado, el token va en el header)
  getPersonalRecommendations(): Observable<RecommendationResponse[]> {
    return this.http.get<RecommendationResponse[]>(`${this.apiUrl}/personal/scored`);
  }
}