import { Routes } from '@angular/router';
import { AuthContainerComponent } from './auth/auth-container/auth-container.component'; 
import { MandarcorreoComponent } from './mandarcorreo/mandarcorreo.component'; 
import { PaymentComponent } from './payment/payment.component';
import { CallBackComponent } from './callback/callback.component';
import { MusicComponent } from './music/music.component';
import { UserCallBackComponent } from './usercallback/usercallback.component';
import { MusicUserComponent } from './music-user/music-user.component';

export const routes: Routes = [
    
    { path: '', redirectTo: 'login', pathMatch: 'full' }, 
    { path: 'login', component: AuthContainerComponent },
    { path: 'register', component: AuthContainerComponent },
    { path: 'correo', component: MandarcorreoComponent },
    { path: 'payment', component: PaymentComponent },
    { path: 'callback', component: CallBackComponent },
    { path: 'music', component: MusicComponent },
    { path: 'musicUser', component: MusicUserComponent },
    { path: 'usercallback', component:  UserCallBackComponent },
    
    { path: '**', redirectTo: 'login' } 
];