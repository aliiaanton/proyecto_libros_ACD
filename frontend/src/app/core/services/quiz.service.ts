import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/quiz';

  // Obtener las preguntas
  getQuestions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/questions`);
  }

  // Enviar respuestas y recibir recomendación
  submitQuiz(answers: any[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/answer`, { answers });
  }
}