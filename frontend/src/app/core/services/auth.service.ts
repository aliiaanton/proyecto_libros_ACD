import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, AuthResponse, RegisterRequest } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  
  // URL base de tu backend Spring Boot
  private apiUrl = 'http://localhost:8080/api/auth';

  // Signal reactivo: ¿Hay un usuario logueado?
  // Verifica si existe un token al iniciar la app
  currentUser = signal<string | null>(localStorage.getItem('username'));

  // --- LOGIN ---
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
      tap(response => {
        // Guardamos token y usuario en el navegador
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);
        
        // Actualizamos la señal para que toda la app sepa que entramos
        this.currentUser.set(response.username);
      })
    );
  }

  // --- REGISTRO ---
  register(data: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  // --- LOGOUT ---
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  // --- UTILIDADES ---
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  verifyEmail(token: string): Observable<any> {
  return this.http.get(`${this.apiUrl}/verify?token=${token}`, { 
    responseType: 'text' 
  });
  }
}