import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// Asegúrate de importar tus interfaces si las separaste, o usa 'any' por ahora si es rápido
// import { CustomListResponse } from '../models/book.models';

@Injectable({ providedIn: 'root' })
export class LibraryService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/library';

  // Actualizar estado (Ya lo teníamos)
  updateStatus(userId: number, googleBookId: string, status: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/status`, { userId, googleBookId, status });
  }

  // --- NUEVO: Listas Personalizadas ---

  // Obtener mis listas
  getCustomLists(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/custom-lists`);
  }

  // Crear una lista nueva
  createCustomList(name: string, description: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/custom-list`, { 
      name, 
      description, 
      isPublic: true 
    });
  }

  // Eliminar lista
  deleteList(listId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/custom-list/${listId}`);
  }
}