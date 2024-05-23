import { Component } from '@angular/core';
import {ChatMessageComponent} from "../chat-message/chat-message.component";

@Component({
  selector: 'app-homepage-chat',
  standalone: true,
  imports: [
    ChatMessageComponent
  ],
  templateUrl: './homepage-chat.component.html',
  styleUrl: './homepage-chat.component.css'
})
export class HomepageChatComponent {

}
