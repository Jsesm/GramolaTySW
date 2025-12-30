import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';



@Injectable({
  providedIn: 'root'
})

export class SpotiService {  
    
  spotiV1Url = 'https://api.spotify.com/v1'; //Poner las cosas de verdad
  
  constructor(private http: HttpClient) { }
  
  getAuthorizationToken(code: string, redirect: string) : Observable<any> { 
    let url = `http://localhost:8080/spoti/getAuthorizationToken?code=${code}&clientId=${sessionStorage.getItem("clientId")}&redirect=${redirect}`; 
    return this.http.get(url, {withCredentials: true}); 
 }

  getDevices() : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/devices`;
    return this.http.get<any>(url, { headers } );
  } 


  buscarCancion(busqueda: string) : Observable<any> { 
     const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/search?q=${encodeURIComponent(busqueda)}&type=track&limit=5`;
    return this.http.get<any>(url, { headers } );
  } 

  getCurrentPlayList() : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/queue`;
    return this.http.get<any>(url, { headers } );
  }
  
  pausar(id: string) : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/pause?device_id=${id}`;
    return this.http.put<any>(url, {}, { headers, responseType: 'text' as 'json' } );
  }

  reanudar(id: string) : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/play?device_id=${id}`;
    return this.http.put<any>(url, {}, { headers , responseType: 'text' as 'json' } );
  }

  siguiente(id: string) : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/next?device_id=${id}`;
    return this.http.post<any>(url, {}, { headers, responseType: 'text' as 'json'} );
  }

  again(id: string) : Observable<any> { 
    const headers = new HttpHeaders({  
      'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
    }); 
    let url = `${this.spotiV1Url}/me/player/seek?position_ms=0&device_id=${id}`;
    return this.http.put<any>(url, {}, { headers, responseType: 'text' as 'json'} );
  }

  aniadir(_track: string) : Observable<any> {
        const headers = new HttpHeaders({  
          'Authorization': `Bearer ${sessionStorage.getItem("spotiToken")}` 
        }); 
        const url = `${this.spotiV1Url}/me/player/queue?uri=spotify:track:${_track}`;

         return this.http.post(url, null, {headers,responseType: 'text'});
  }

}
