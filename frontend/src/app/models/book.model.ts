export interface Book {
  bookId?: number; // Opcional porque al buscar en Google a veces no tenemos nuestro ID aun
  googleBookId: string;
  title: string;
  authors: string;
  description?: string;
  coverUrl?: string;
}