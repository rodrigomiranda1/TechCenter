import { TestBed } from '@angular/core/testing';

import { Catalogoadminservice } from './catalogoadminservice';

describe('Catalogoadminservice', () => {
  let service: Catalogoadminservice;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Catalogoadminservice);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
