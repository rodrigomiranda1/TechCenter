import { TestBed } from '@angular/core/testing';

import { Iaservice } from './iaservice';

describe('Iaservice', () => {
  let service: Iaservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Iaservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
