import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  // @ts-ignore
  constructor(private client: HttpClient) { }

  prepay(): Observable<any> {
    return this.client.get("http://localhost:8080/payment/prepay", {
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

  getPublickey(){
    return this.client.get("http://localhost:8080/payment/getPublickey", {
      withCredentials: true,
      responseType: "text"
    })
  }
}