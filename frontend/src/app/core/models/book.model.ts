export interface Book {
  bookId?: number;          // ID en tu base de datos local
  googleBookId: string;     // ID de Google Books
  title: string;
  authors: string;
  description: string;
  coverUrl: string;
  publishedDate?: string;
  pageCount?: number;
  averageRatingApi?: number;
  genres?: string[];
  tags?: string[];

  userReadingStatus?: 'WANT_TO_READ' | 'READING' | 'READ' | 'DROPPED' | null;
}

export interface Genre {
  id: number;
  name: string;
  bookCount?: number;
}

export interface Tag {
  id: number;
  name: string;
  description?: string;
  bookCount?: number;
}

// Para la página principal
export interface HomeResponse {
  featuredBooks: Book[];
  personalRecommendations?: RecommendationResponse[]; // Puede ser null si no está logueado
  mainGenres: Genre[];
  mainTags: Tag[];
}

// Para las recomendaciones con puntuación
export interface RecommendationResponse {
  book: Book;
  score: number;
  reasons: string[];
}

// Para la Cita a Ciegas
export interface BlindDateResponse {
  quoteId: number;
  quoteText: string;
  googleBookId: string; // Para revelar el libro después
  genre: string;        // Pista
}