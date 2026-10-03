import { TestBed } from '@angular/core/testing';

import { Ventaservice } from './ventaservice';

describe('Ventaservice', () => {
  let service: Ventaservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Ventaservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
