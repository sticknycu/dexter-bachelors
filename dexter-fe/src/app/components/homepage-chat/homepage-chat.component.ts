import {Component, OnInit} from '@angular/core';
import {ChatMessageComponent} from "../chat-message/chat-message.component";
import {UserChatService} from "../../services/chats-service.service";
import {RouterOutlet} from "@angular/router";

@Component({
  selector: 'app-homepage-chat',
  standalone: true,
  imports: [
    ChatMessageComponent,
    RouterOutlet
  ],
  templateUrl: './homepage-chat.component.html',
  styleUrl: './homepage-chat.component.css'
})
export class HomepageChatComponent {
}
