import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { LibraryService } from '../../core/services/library.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-my-library',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './my-library.component.html',
  styleUrls: ['./my-library.component.scss']
})
export class MyLibraryComponent implements OnInit {
  private libraryService = inject(LibraryService);
  public authService = inject(AuthService);

  activeTab: 'LISTS' | 'SHELF' = 'LISTS'; // Pestaña activa
  customLists: any[] = [];
  isLoading = true;

  // Modal Crear Lista
  showCreateModal = false;
  newListName = '';
  newListDesc = '';

  ngOnInit() {
    this.loadLists();
  }

  loadLists() {
    this.isLoading = true;
    this.libraryService.getCustomLists().subscribe({
      next: (data) => {
        this.customLists = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  createList() {
    if (!this.newListName.trim()) return;

    this.libraryService.createCustomList(this.newListName, this.newListDesc).subscribe({
      next: () => {
        this.loadLists(); // Recargar
        this.closeModal();
      },
      error: () => alert('Error al crear la lista')
    });
  }

  deleteList(id: number) {
    if(confirm('¿Seguro que quieres borrar esta lista? 🥀')) {
      this.libraryService.deleteList(id).subscribe(() => this.loadLists());
    }
  }

  closeModal() {
    this.showCreateModal = false;
    this.newListName = '';
    this.newListDesc = '';
  }
}