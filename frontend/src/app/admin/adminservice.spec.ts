import { TestBed } from '@angular/core/testing';

import { Adminservice } from './adminservice';

describe('Adminservice', () => {
  let service: Adminservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Adminservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
