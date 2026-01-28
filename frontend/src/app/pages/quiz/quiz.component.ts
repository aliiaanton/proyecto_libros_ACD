import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuizService } from '../../core/services/quiz.service';
import { BookCardComponent } from '../../shared/book-card/book-card.component';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-quiz',
  standalone: true,
  imports: [CommonModule, BookCardComponent, RouterModule],
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.scss']
})
export class QuizComponent implements OnInit {
  private quizService = inject(QuizService);

  // Estados de la interfaz
  step: 'INTRO' | 'QUESTION' | 'LOADING' | 'RESULT' = 'INTRO';
  
  questions: any[] = [];
  currentQuestionIndex = 0;
  userAnswers: any[] = []; // Guardamos { questionId, selectedOptionId }
  
  result: any = null; // Aquí guardaremos el libro recomendado y la explicación

  ngOnInit() {
    // Cargamos las preguntas nada más entrar, pero no las mostramos hasta dar a "Empezar"
    this.quizService.getQuestions().subscribe(data => {
      this.questions = data;
    });
  }

  startQuiz() {
    this.step = 'QUESTION';
    this.currentQuestionIndex = 0;
    this.userAnswers = [];
  }

  selectOption(optionIndex: number) {
    // Guardamos la respuesta
    const currentQ = this.questions[this.currentQuestionIndex];
    
    this.userAnswers.push({
      questionId: currentQ.id,
      selectedOptionId: optionIndex
    });

    // Avanzamos o terminamos
    if (this.currentQuestionIndex < this.questions.length - 1) {
      // Pequeño delay para que se sienta fluido
      setTimeout(() => {
        this.currentQuestionIndex++;
      }, 300);
    } else {
      this.finishQuiz();
    }
  }

  finishQuiz() {
    this.step = 'LOADING';

    this.quizService.submitQuiz(this.userAnswers).subscribe({
      next: (data) => {
        // Simulamos un poco de tiempo de "pensamiento mágico" (opcional, por estética)
        setTimeout(() => {
          this.result = data;
          this.step = 'RESULT';
        }, 1500);
      },
      error: (err) => {
        console.error(err);
        alert('Hubo un error calculando tu lectura. Inténtalo de nuevo.');
        this.step = 'INTRO';
      }
    });
  }

  reset() {
    this.step = 'INTRO';
    this.result = null;
  }
  
  // Getter para calcular el progreso (porcentaje)
  get progress() {
    if (this.questions.length === 0) return 0;
    return ((this.currentQuestionIndex) / this.questions.length) * 100;
  }
}