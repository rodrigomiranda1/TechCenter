import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Personas } from './personas';

describe('Personas', () => {
  let component: Personas;
  let fixture: ComponentFixture<Personas>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Personas],
    }).compileComponents();

    fixture = TestBed.createComponent(Personas);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
