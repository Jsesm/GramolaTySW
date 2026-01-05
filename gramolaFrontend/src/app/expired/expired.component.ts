import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-expired',
  imports: [],
  templateUrl: './expired.component.html',
  styleUrl: './expired.component.css'
})
export class ExpiredComponent {

  constructor(private router: Router) { }

  goToRegister(): void {
    this.router.navigate(['/']);
  }

}
