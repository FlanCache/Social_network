import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FeedComponent} from "./feed/feed.component";
import {LoginComponent} from "./auth/login/login.component";
import {RegisterComponent} from "./auth/register/register.component";
import {RegisterSuccessComponent} from "./auth/register-success/register-success.component";
import {HomeComponent} from "./home/home.component";
import { ProfileComponent } from './profile/profile.component';
import { AuthGuard } from './auth/auth/auth.guard';

const routes: Routes = [
  { path:'' ,pathMatch : "full",redirectTo:'login'},
  {path:'feed',component:FeedComponent, canActivate: [AuthGuard] },
  {path:'login',component:LoginComponent},
  {path:'register',component:RegisterComponent},
  {path:'register-success', component:RegisterSuccessComponent},
  {path:'profile/:id', component: ProfileComponent, canActivate: [AuthGuard] },
  {path: 'home' ,component:HomeComponent},

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
