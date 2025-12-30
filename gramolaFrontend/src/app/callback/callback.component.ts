import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SpotiService } from '../spoti.service';
import { Router } from '@angular/router'; 
import { Observable } from 'rxjs';

@Component({
  selector: 'app-callback',
  imports: [],
  templateUrl: './callback.component.html',
  styleUrl: './callback.component.css'
})
export class CallBackComponent {

  apiUrl: "http://localhost:8080/spoti" |  undefined;
  http: any;
  
  constructor(private route: ActivatedRoute, private router: Router, public spoti : SpotiService) {} 
  ngOnInit(): void { 
    const qp = this.route.snapshot.queryParamMap; 
    const code  = qp.get('code'); 
    const state = qp.get('state'); 
    const error = qp.get('error'); 
    if (error) { 
      this.router.navigateByUrl('/'); 
      return; 
    } 
    if (!code || !state) { 
      alert("No hay código o estado") 
      return 
    } 

    history.replaceState({}, '', '/callback'); 
    this.spoti.getAuthorizationToken(code, "callback").subscribe({ 
      next: (data: { access_token: any; }) => { 
        sessionStorage.setItem("spotiToken", data.access_token) 
        this.router.navigateByUrl('/music'); 
      }, 
      error: (err: any) => { 
        console.error('Error fetching access token:', err); 
      } 
    }); 
  }
  
  

  getAuthorizationToken(code : string) : Observable<any> { 
    let url = `${this.apiUrl}/getAuthorizationToken?code=${code}&clientId=${sessionStorage.getItem("clientId")}`; 
    return this.http.get(url); 
 } 
} 