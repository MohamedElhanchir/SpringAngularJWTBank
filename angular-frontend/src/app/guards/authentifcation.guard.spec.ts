import { TestBed } from '@angular/core/testing';

import { AuthentifcationGuard } from './authentifcation.guard';

describe('AuthentifcationGuard', () => {
  let guard: AuthentifcationGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(AuthentifcationGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
