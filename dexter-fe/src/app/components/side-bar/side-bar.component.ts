import { Component } from '@angular/core';
import {NgForOf, NgOptimizedImage} from "@angular/common";
import {HomepageChatComponent} from "../homepage-chat/homepage-chat.component";

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [
    NgOptimizedImage,
    HomepageChatComponent,
    NgForOf
  ],
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css'
})
export class SideBarComponent {

  chats = [
    new class implements Chat {
      title: string = "New Chat"
    },
    new class implements Chat {
      title: string = "Testing"
    },
    new class implements Chat {
      title: string = "new dexter here"
    }
  ]
}

export interface Chat {
  title: string
}
