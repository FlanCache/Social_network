import {CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA} from '@angular/core';
import { AppComponent } from './app.component';
import {FeedComponent} from "./feed/feed.component";
import {BrowserModule} from '@angular/platform-browser';
import {AppRoutingModule} from './app-routing.module';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {CommonComponent} from './common/common.component';
import {ContainerComponent} from './container/container.component';
import {IonicModule} from "@ionic/angular";
import {HttpClientModule} from "@angular/common/http";
import { RegisterComponent } from './auth/register/register.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterSuccessComponent } from './auth/register-success/register-success.component';
import { HomeComponent } from './home/home.component';
import { ProfileComponent } from './profile/profile.component';


@NgModule({
  
  declarations: [
    AppComponent,
    FeedComponent,
    CommonComponent,
    RegisterComponent,
    LoginComponent,
    RegisterSuccessComponent,
    HomeComponent,
    ContainerComponent,
    ProfileComponent,

  ],
  imports: [
    
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    // RouterModule.forRoot([
    //   {path:'register', component: RegisterComponent},
    //   {path:'register-success', component:RegisterSuccessComponent},
    //   {path:'login', component:LoginComponent},
    //   {path: 'home' ,component:HomeComponent},
    //   {path: '' ,component:HomeComponent}
    // ]),
    IonicModule,
    HttpClientModule,
  ],
  providers: [
    // {
    //   provide: HTTP_INTERCEPTORS,
    //   useClass: AuthInterceptor,
    //   multi: true
    // }

  ],
  
  bootstrap: [AppComponent],
  schemas:[NO_ERRORS_SCHEMA,CUSTOM_ELEMENTS_SCHEMA]
})
export class AppModule {
  
}
