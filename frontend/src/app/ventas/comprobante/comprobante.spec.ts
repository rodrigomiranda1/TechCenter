import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Comprobante } from './comprobante';

describe('Comprobante', () => {
  let component: Comprobante;
  let fixture: ComponentFixture<Comprobante>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Comprobante],
    }).compileComponents();

    fixture = TestBed.createComponent(Comprobante);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
