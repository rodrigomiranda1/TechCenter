import { TestBed } from '@angular/core/testing';

import { Empleadoservice } from './empleadoservice';

describe('Empleadoservice', () => {
  let service: Empleadoservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Empleadoservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
