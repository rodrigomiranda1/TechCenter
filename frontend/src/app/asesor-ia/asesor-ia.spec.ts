import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AsesorIa } from './asesor-ia';

describe('AsesorIa', () => {
  let component: AsesorIa;
  let fixture: ComponentFixture<AsesorIa>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AsesorIa],
    }).compileComponents();

    fixture = TestBed.createComponent(AsesorIa);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
