import { Component, EventEmitter, Output } from '@angular/core';
import { Router } from '@angular/router'; // El Router sigue siendo necesario para el login exitoso
import { FormsModule } from '@angular/forms';
import { UserService } from '../../user.service';
import { GeolocalizacionService } from '../../geolocalizacion.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true, 
  imports: [FormsModule, CommonModule], // Ya no necesitamos RouterLink
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {


  // CLAVE: El evento que notifica al padre que debe ir a Register
  @Output() onGoToRegister = new EventEmitter<void>();

  nohaybares: boolean= false
  coordenadas?: GeolocationPosition
  email: string = '';
  password: string = '';
    errorMsg: string = 'Escriba sus datos y pulse "Gestionar mi bar" si quiere gestionarlo o "Dar acceso a los clientes" si quiere que utilicen este dispositivo.';

  scopes : string[] = ["user-read-private", "user-read-email", "playlist-read-private", "playlist-read-collaborative",
     "user-read-playback-state", "user-modify-playback-state", "user-read-currently-playing", "user-library-read",
      "user-library-modify", "user-read-recently-played", "user-top-read", "app-remote-control", "streaming"]; 
  
  
  spoti = {
  redirectUrl: 'http://127.0.0.1:4200/callback',
  redirectUrlUsers: 'http://127.0.0.1:4200/usercallback',
  authorizeUrl: 'https://accounts.spotify.com/authorize'
};

  faltaemail: boolean= false;
  faltapass: boolean= false;

  constructor(private router: Router, private userService : UserService, private geoService : GeolocalizacionService) {  }
  
  // Función que el botón de enlace llamará
  goToRegister() {
    this.onGoToRegister.emit();
  }

  onLoginSubmit() {
    this.login();
    
  }


    login() {
        if (!this.email || !this.password) {
          this.errorMsg = 'Por favor, introduce tu email y contraseña.';
          return; 
        }
        
        this.userService.login(this.email, this.password).subscribe( 
          response => { 
            sessionStorage.setItem("clientId", response) 
            this.getToken(this.spoti.redirectUrl); 
          }, 
          err => { 
            this.errorMsg = 'Con esas credenciales no hemos encontrado una cuenta creada que haya pagado.';
          } 
        );
    }


    private getToken(redirectUrl: string) { 
      let state = this.generateString();
      let params = "response_type=code"; 
      params += `&client_id=${sessionStorage.getItem("clientId")}`; 
      params += `&scope=${encodeURIComponent(this.scopes.join(" "))}`; 
      params += `&redirect_uri=${redirectUrl}`; 
      params += `&state=${state}`; 
      sessionStorage.setItem("oauth_state", state); 
      let url = this.spoti.authorizeUrl + "?" + params 
      window.location.href = url 
    } 
  
    generateString(length: number = 16): string {
      const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
      let result = '';
      for (let i = 0; i < length; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
      }
      return result;
    }


    soyuncliente() {
        if (!this.email || !this.password) {
          this.errorMsg = 'Por favor, introduce tu email y contraseña.';
          return; 
        }
        this.userService.login(this.email, this.password).subscribe( 
          response => { 
            sessionStorage.setItem("clientId", response);
            this.geoService.getCoordenadas().subscribe( 
              response => { 
        
                this.userService.baresCercademi(response.latitude, response.longitude).subscribe(
                  response => {
           
                  if(response){
                    this.getToken(this.spoti.redirectUrlUsers);
                  }else{
                    this.nohaybares=true;
                  }
                },
                  err => {
                    console.log(err.error.message);
                    this.nohaybares=true;
                  }
                );
              }, 
              err => { 
                this.errorMsg = err.error.message;
                console.log(err.error.message);
              } 
            );
          }, 
          err => { 
            this.errorMsg = 'Con esas credenciales no hemos encontrado una cuenta creada que haya pagado.';
          } 
        );
      }


    entendido() {
      this.nohaybares=false;
    }


    
}