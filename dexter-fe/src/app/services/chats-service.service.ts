import {Injectable, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Time} from "@angular/common";

@Injectable({
  providedIn: 'root'
})
export class UserChatService {

  private apiUrl = 'http://localhost:3333/reactive';

  constructor(private http: HttpClient) {
  }

  getUserChats(): Observable<Set<string>> {
    return this.http.get<Set<string>>(`${this.apiUrl}/user-chats`);
  }

  createChat(): Observable<ChatCreatedResponse> {
    return this.http.post<ChatCreatedResponse>(`${this.apiUrl}/chat`, {});
  }

  sendMessage(message: InputMessage) {
    return this.http.post<Message>(`${this.apiUrl}/send-message`, message);
  }

  getChatMessages(chatId: string) {
    console.log(`Call with ${chatId}`)
    return this.http.get<Array<Message>>(`${this.apiUrl}/chat/${chatId}/messages`);
  }
}

// data class Message(val usernameFrom: String, val originalImage: ByteArray?, val cannyImage: ByteArray?, val generatedImage: ByteArray?, val chatRoomId: UUID, val time: Instant)

export interface Message {
  usernameFrom: string;
  originalImage?: string;
  cannyImage?: string;
  generatedImage?: string;
  chatRoomId: string;
  time: Date;
}

export interface InputMessage {
  content: string; // byte array
  chatRoomId: string; // uuid
}

export interface ChatCreatedResponse {
  chatId: string;
}
