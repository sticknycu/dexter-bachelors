import {CommonModule, NgForOf, NgIf} from '@angular/common';
import {Component, input, Input, OnDestroy, OnInit} from "@angular/core";
import {filter, pipe, Subscription, switchMap} from "rxjs";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";
import {InputMessage, Message, UserChatService} from "../../services/chats-service.service";
import {KeycloakService} from "keycloak-angular";

@Component({
  selector: 'app-chat-message',
  standalone: true,
  imports: [
    NgIf,
    NgForOf
  ],
  templateUrl: './chat-message.component.html',
  styleUrls: ['./chat-message.component.css']
})
export class ChatMessageComponent implements OnInit, OnDestroy {
  messages: Array<Message> = [];
  @Input("chatId") chatId: string | undefined;
  private routeSub: Subscription | undefined;

  constructor(private route: ActivatedRoute,
              private router: Router,
              private userChatService: UserChatService,
              private keycloak: KeycloakService) { }

  ngOnInit(): void {
    this.routeSub = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      switchMap(() => {
        this.chatId = this.route.snapshot.paramMap.get('chatId') || '1';
        return this.userChatService.getChatMessages(this.chatId);
      })
    ).subscribe(msg => {
      this.messages = msg
        .sort((o1, o2) => o1.time.valueOf() - o2.time.valueOf())
        .reverse();
      this.scrollToBottom();
      console.log('Messages:', msg);
    });
  }

  scrollToBottom(): void {
    try {
      const container = document.querySelector('.chat-container');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  attachedFile: File | null = null;
  attachedFileURL: string | null = null;
  error: string | null = null;

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    if (event.dataTransfer?.files) {
      this.handleFiles(event.dataTransfer.files);
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.handleFiles(input.files);
    }
  }

  handleFiles(files: FileList) {
    if (files.length > 1) {
      this.error = 'Only one file can be attached at a time.';
    } else {
      this.error = null;
      this.attachedFile = files[0];
      this.attachedFileURL = URL.createObjectURL(this.attachedFile);
    }
  }

  visualizeFile() {
    if (this.attachedFileURL) {
      window.open(this.attachedFileURL, '_blank');
    }
  }

  removeFile() {
    this.attachedFile = null;
    this.attachedFileURL = null;
  }

  sendMessage(): void {
    if (this.attachedFile) {
      this.convertFileToBase64(this.attachedFile).then(base64String => {
        const localChatId = this.chatId ? this.chatId : "";
        const inputMessage = new class implements InputMessage {
          chatRoomId: string = localChatId;
          content: string = base64String;
        }

        const username = this.keycloak.getUsername();

        const inputMessageAsMessage = new class implements Message {
          time: Date = new Date(Date.now());
          usernameFrom: string = username;
          chatRoomId: string = localChatId;
          originalImage: string = base64String;
        }
        this.messages = this.messages.concat(inputMessageAsMessage);
        this.userChatService.sendMessage(inputMessage).subscribe(
          response => {
            console.log('Send Message Response:', response.originalImage);
            this.messages = this.messages.concat(response)
              .sort((o1, o2) => o1.time.valueOf() - o2.time.valueOf())
              // .reverse();
            this.scrollToBottom();
          }
        );
      }).catch(error => {
        console.error('Error converting file to Base64:', error);
      });
    } else {
      console.error('No file attached.');
    }
  }

  private convertFileToBase64(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        const base64String = (reader.result as string).split(',')[1];
        resolve(base64String);
      };
      reader.onerror = error => reject(error);
      reader.readAsDataURL(file);
    });
  }
}
