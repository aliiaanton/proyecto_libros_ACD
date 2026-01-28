export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  genrePreferenceIds: number[];
  tagPreferenceIds: number[];
}

export interface User {
  userId: number;
  username: string;
  email: string;
  bio?: string;
}