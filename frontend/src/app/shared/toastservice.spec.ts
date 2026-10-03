import { TestBed } from '@angular/core/testing';

import { Toastservice } from './toastservice';

describe('Toastservice', () => {
  let service: Toastservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Toastservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
