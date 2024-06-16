import { Routes } from '@angular/router';
import {HomepageComponent} from "./components/homepage/homepage.component";
import {AuthGuard} from "./guard/auth.guard";
import {ChatMessageComponent} from "./components/chat-message/chat-message.component";

export const routes: Routes = [
  { path: 'chat/:chatId', component: ChatMessageComponent, canActivate: [AuthGuard] },
  { path: '', component: HomepageComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: ' ' },
];

