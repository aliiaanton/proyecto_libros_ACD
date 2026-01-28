import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlindDateComponent } from './blind-date.component';

describe('BlindDateComponent', () => {
  let component: BlindDateComponent;
  let fixture: ComponentFixture<BlindDateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlindDateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlindDateComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
