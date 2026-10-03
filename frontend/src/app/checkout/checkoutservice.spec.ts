import { TestBed } from '@angular/core/testing';

import { Checkoutservice } from './checkoutservice';

describe('Checkoutservice', () => {
  let service: Checkoutservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Checkoutservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
