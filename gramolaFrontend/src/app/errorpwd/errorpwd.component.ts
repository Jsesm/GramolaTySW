import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-errorpwd',
  imports: [],
  templateUrl: './errorpwd.component.html',
  styleUrl: './errorpwd.component.css'
})
export class ErrorpwdComponent {

  constructor(private router: Router) { }


    goToRegister(): void {
    this.router.navigate(['/']);
  }

}
