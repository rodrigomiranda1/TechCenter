import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Confirmacion } from './confirmacion';

describe('Confirmacion', () => {
  let component: Confirmacion;
  let fixture: ComponentFixture<Confirmacion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Confirmacion],
    }).compileComponents();

    fixture = TestBed.createComponent(Confirmacion);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
