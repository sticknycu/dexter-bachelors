import {Component, OnDestroy, OnInit} from '@angular/core';
import {NgForOf, NgOptimizedImage} from "@angular/common";
import {HomepageChatComponent} from "../homepage-chat/homepage-chat.component";
import {InputMessage, UserChatService} from "../../services/chats-service.service";
import {NavigationEnd, Router, RouterLink} from "@angular/router";
import {filter, map, Subscription} from "rxjs";

@Component({
  selector: 'app-side-bar',
  standalone: true,
  imports: [
    NgOptimizedImage,
    HomepageChatComponent,
    NgForOf,
    RouterLink
  ],
  templateUrl: './side-bar.component.html',
  styleUrl: './side-bar.component.css'
})
export class SideBarComponent implements OnInit, OnDestroy {

  chatId: string = "";
  __URL: string = "";
  chats: Set<string> = new Set();
  private routerSub: Subscription | null = null;

  constructor(private userChatService: UserChatService,
              private router: Router) { }

  ngOnInit(): void {
    this.userChatService.getUserChats().subscribe(
      (chats: Set<string>) => {
        // Ensure chats is set to a Set
        this.chats = new Set(chats);
        console.log("Chats: ", this.chats);
      },
      (error) => {
        console.error('Error fetching user chats:', error);
      }
    );
  }

  ngOnDestroy(): void {
    if (this.routerSub) {
      this.routerSub.unsubscribe();
    }
  }

  createChat() {
    this.userChatService.createChat().subscribe(
      response => {
        console.log('Chat created with ID:', response.chatId);
        // Ensure the chats are added properly
        this.chats.add(response.chatId);
      },
      error => {
        console.error('Error creating chat:', error);
      }
    );
  }

  setChatUrl(chatId: string) {
    this.chatId = chatId;
    if (chatId == "") this.createChat();
  }
}

