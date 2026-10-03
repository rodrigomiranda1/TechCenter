import { TestBed } from '@angular/core/testing';

import { Personasservice } from './personasservice';

describe('Personasservice', () => {
  let service: Personasservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Personasservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
