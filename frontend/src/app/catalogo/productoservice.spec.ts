import { TestBed } from '@angular/core/testing';

import { Productoservice } from './productoservice';

describe('Productoservice', () => {
  let service: Productoservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Productoservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
