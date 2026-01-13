import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { UserService } from '../user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-change',
  imports: [FormsModule, CommonModule],
  templateUrl: './change.component.html',
  styleUrl: './change.component.css'
})
export class ChangeComponent {


  email: string=""
  nombreBar: string=""
  pwd: string="Si no pones contraseña se quedará la actual"
  pwd1: string=""
  pwd2: string=""
  clave: string="";
  error: string ="";
  isCambioPass: any;
  token: any;


  onSubmit() {

    if (this.mirarcorreo()) return;
    if (this.mirarpasswords()) return;

    console.log("Hola");

      this.userService.actualizarDatos(this.clave, this.nombreBar, this.email, this.pwd1).subscribe({
        next: (response) => {
          this.isCambioPass=true;
        },
        error: (err) => {
          this.router.navigate(['/errorpwd'], { replaceUrl: true });
        }
      });
  }

  constructor(private userService: UserService, private router : Router) { } 

  ngOnInit(): void { 
    const params = this.router.parseUrl(this.router.url).queryParams; 
    const emailParam = params['email'];
    const tokenParam = params['id']; 
    this.clave= emailParam;
    this.token= tokenParam;

    if (emailParam) {
      this.userService.recuperarDatos(emailParam, tokenParam).subscribe({
        next: (response) => {
          this.email = response.email;
          this.nombreBar = response.nombreBar;
        },
        error: (err) => {
          this.router.navigate(['/errorpwd'], { replaceUrl: true });
        }
      });
    }
}


    cerrarMensajePass() {
      this.router.navigate(['/login'], { replaceUrl: true });
    }


    mirarpasswords() {
      console.log("HOla")

      if (this.pwd1 !== this.pwd2) {
        console.log("Las contraseñas no coincides")
        this.error = "Las contraseñas no coinciden";
        return true;
      }

      if (this.pwd1.length<8 && this.pwd1 !== ""){
        this.error = "La contraseña debe tener al menos 8 caracteres";
        return true;
      }

      return false;
    }

    mirarcorreo(): boolean {
      
      const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;


      if (!emailPattern.test(this.email) && this.email !== "") {
        this.error = "Esperamos un correo con este formato (ejemplo@dominio.com)";
        return true;
      }


      return false;
    }
}
