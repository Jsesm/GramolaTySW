import { Component } from '@angular/core';
import { LoginComponent } from "../login/login.component";
import { RegisterComponent } from "../register/register.component";

@Component({
  selector: 'app-auth',
  templateUrl: './auth-container.component.html',
  styleUrls: ['./auth-container.component.css'],
  imports: [LoginComponent, RegisterComponent]
})
export class AuthContainerComponent {
  isFlipped: boolean = false;

  toggleCard() {
    this.isFlipped = !this.isFlipped;
  }
}