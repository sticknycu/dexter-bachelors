import { Component } from '@angular/core';
import {KeycloakAuthGuard, KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-top-menu',
  standalone: true,
  imports: [],
  templateUrl: './top-menu.component.html',
  styleUrl: './top-menu.component.css'
})
export class TopMenuComponent {

  constructor(private keycloakService: KeycloakService) {}

  logout() {
    this.keycloakService.logout();
  }
}
