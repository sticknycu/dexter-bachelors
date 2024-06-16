import { TestBed } from '@angular/core/testing';

import { KeycloakAuthService } from './keycloak-service.service';

describe('KeycloakServiceService', () => {
  let service: KeycloakAuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KeycloakAuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
