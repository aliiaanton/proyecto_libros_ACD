import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';
  
  // Esto es para que la Navbar sepa en tiempo real si estamos logueados
  private isLoggedInSubject = new BehaviorSubject<boolean>(this.hasToken());
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private http: HttpClient) { }

  // --- REGISTRO ---
  register(request: RegisterRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, request);
  }

  // --- VERIFICACIÓN DE EMAIL ---
  verifyEmail(token: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/verify?token=${token}`);
  }

  // --- LOGIN ---
  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        // Guardamos el token en el navegador
        localStorage.setItem('token', response.token);
        localStorage.setItem('username', response.username);
        // Avisamos a la app que ya estamos dentro
        this.isLoggedInSubject.next(true);
      })
    );
  }

  // --- LOGOUT ---
  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    this.isLoggedInSubject.next(false);
  }

  // --- UTILIDADES ---
  private hasToken(): boolean {
    return !!localStorage.getItem('token'); // Devuelve true si hay token
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }
}