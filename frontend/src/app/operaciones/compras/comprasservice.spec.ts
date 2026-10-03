import { TestBed } from '@angular/core/testing';

import { Comprasservice } from './comprasservice';

describe('Comprasservice', () => {
  let service: Comprasservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Comprasservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
