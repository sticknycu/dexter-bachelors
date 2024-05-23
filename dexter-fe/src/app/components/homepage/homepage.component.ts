import {Component, model} from '@angular/core';
import {TopMenuComponent} from "../top-menu/top-menu.component";
import {SideBarComponent} from "../side-bar/side-bar.component";

@Component({
  selector: 'app-homepage',
  standalone: true,
  imports: [
    TopMenuComponent,
    SideBarComponent
  ],
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent {
}
