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

   getCoordenadasPorDireccion(direccion: string): Observable<{ latitude: number; longitude: number }> {
  // Usamos el parámetro 'q' para direcciones completas
  const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(direccion)}&countrycodes=es&format=json&limit=1`;
  
  return new Observable(observer => {
    fetch(url, {
      headers: {
        'Accept': 'application/json'
        // IMPORTANTE: Nominatim requiere un User-Agent identificativo. 
        // Si lo usas mucho, añade el nombre de tu app aquí si puedes.
      }
    })
      .then(response => {
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        return response.json();
      })
      .then((results: any[]) => {
        if (results && results.length > 0) {
          const data = results[0];
          observer.next({ 
            latitude: Number(data.lat), 
            longitude: Number(data.lon) 
          });
          observer.complete();
        } else {
          observer.error(new Error('No se encontraron coordenadas para esa dirección'));
        }
      })
      .catch(err => observer.error(err));
  });
}
}

