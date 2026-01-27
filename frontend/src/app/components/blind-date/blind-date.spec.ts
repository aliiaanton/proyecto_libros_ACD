import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlindDate } from './blind-date.component';

describe('BlindDate', () => {
  let component: BlindDate;
  let fixture: ComponentFixture<BlindDate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlindDate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlindDate);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
