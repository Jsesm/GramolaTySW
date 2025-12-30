import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GeolocalizacionService {



  coordenadas?: GeolocationPosition

  constructor(){
      if (navigator.geolocation){
            navigator.geolocation.getCurrentPosition(
              (position)=>{
                  this.coordenadas=position
              }   
            );
            
          }
  }


    getCoordenadas(): Observable<{ latitude: number; longitude: number }> {
  return new Observable(observer => {
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        observer.next({
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude
        });
        observer.complete();
      },
      (err) => observer.error(err),
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
      }
    );
  });
}

    getCoordenadasporCodigoPostal(codigoPostal: string): Observable<{ latitude: number; longitude: number }> {
      const url = `https://nominatim.openstreetmap.org/search?postalcode=${encodeURIComponent(codigoPostal)}&countrycodes=es&format=json&addressdetails=1&limit=1`;
      return new Observable(observer => {
        fetch(url, {
          headers: {
            'Accept': 'application/json'
            // Note: setting a custom User-Agent is not allowed from browsers; if you hit rate limits,
            // consider using a backend proxy that adds proper headers.
          }
        })
          .then(response => {
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            return response.json();
          })
          .then((results: any[]) => {
            if (results && results.length > 0) {
              const data = results[0];
              const latitude = Number(data.lat);
              const longitude = Number(data.lon);
              observer.next({ latitude, longitude });
              observer.complete();
            } else {
              observer.error(new Error('No se encontraron resultados para el código postal'));
            }
          })
          .catch(err => observer.error(err));
      });
    }
    
}

