import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { switchMap } from 'rxjs/operators';
import { GeolocalizacionService } from './geolocalizacion.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {


  private apiUrl = ' http://localhost:8080/users'; 
  constructor(private http: HttpClient, private geoService: GeolocalizacionService) {} 
  
    register(email: string, pwd1: string, pwd2: string, bar: string, clientId: string, clientSecret: string,
            codigoPostal: string, precioCancion: number, signatureDataUrl: string, Ubireal: boolean) {

      const obtenerCoords$ = Ubireal 
        ? this.geoService.getCoordenadas() 
        : this.geoService.getCoordenadasporCodigoPostal(codigoPostal);

      return obtenerCoords$.pipe(
        switchMap(coords => {
          const info = { 
            bar, 
            email, 
            pwd1, 
            pwd2, 
            clientId, 
            clientSecret,
            latitud: coords.latitude,
            longitud: coords.longitude,
            precioCancion,
            firma: signatureDataUrl
          };

          return this.http.post<any>(`${this.apiUrl}/register`, info, { responseType: 'text' as 'json' });
        })
      );
    }

  login(email: string, pwd: string){

    let info = { 
    email : email, 
    pwd : pwd
    }

    return this.http.post<any>(this.apiUrl+"/login", info, {withCredentials: true, responseType: 'text' as 'json'}); 

  }


  baresCercademi(latitud: number, longitud: number){

    const url = `${this.apiUrl}/bares?latitud=${latitud}&longitud=${longitud}`;

    return this.http.get<any>(url, { responseType: 'text' as 'json' }); 

  }

    buscarDatos(){

    const clientId= sessionStorage.getItem("clientId")
    return this.http.get<any>(`${this.apiUrl}/obtenerDatos/${clientId}`);

  }

  logout(): Observable<any> {
  return this.http.post(`${this.apiUrl}/logout`, {}, { 
    withCredentials: true 
  });
}

  cambiarPassword(){
    const clientId= sessionStorage.getItem("clientId");
    
    this.http.post(`${this.apiUrl}/cambiarPassword?clientId=${clientId}`, {}, { 
  withCredentials: true 
}).subscribe();
  }


  recuperarDatos(email: string, token: string){

    return this.http.get<any>(`${this.apiUrl}/recuperarDatos?email=${email}&id=${token}`);

  }

  actualizarDatos(clave: string, nombreBar: string, email: string, pwd: string) {
        let info = { 
          nombreBar: nombreBar,
          email : email, 
          pwd : pwd
        }

    return this.http.post<any>(`${this.apiUrl}/actualizarDatos?email=${clave}`, info, {withCredentials: true, responseType: 'text' as 'json'}); 

  }

}
