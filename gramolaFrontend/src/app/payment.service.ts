import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Track } from './models/track';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  // @ts-ignore
  constructor(private client: HttpClient) { }

  prepay(tipo: string): Observable<any> {

    const clientId = sessionStorage.getItem("clientId");
    const opcion = (tipo === "Cancion") ? clientId : "registro";
    
    return this.client.get(`http://localhost:8080/payment/prepay?tipo=${tipo}&opcion=${opcion}`, {
      withCredentials: true,
      observe: "response",
      responseType: "text"
    })
  }

  confirm(response: any, transactionId: string, token: string): Observable<any> {
    response.transactionId = transactionId
    response.token = token
    return this.client.post<any>("http://localhost:8080/payment/confirm", response, {
      withCredentials: true,
      observe: "response",
      responseType: "text" as 'json'
    })
  }

  guardarCancion(response: any, transactionId: string, quieroaniadir: Track | undefined) {
    response.transactionId = transactionId
    response.nombreCancion = quieroaniadir?.name
    response.autorCancion = quieroaniadir?.artist
    response.idCancion = quieroaniadir?.id
    response.clientId= sessionStorage.getItem("clientId")
    return this.client.post<any>("http://localhost:8080/payment/guardarCancion", response, {
      withCredentials: true,
      observe: "response",
      responseType: "text" as 'json'
    })
  }

  getPublickey(){
    return this.client.get("http://localhost:8080/payment/getPublickey", {
      withCredentials: true,
      responseType: "text"
    })
  }
}