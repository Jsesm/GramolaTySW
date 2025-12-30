import { Injectable } from '@angular/core';
import { SpotiService } from './spoti.service';
import { Track } from './models/track';
import { catchError, map, Observable, of } from 'rxjs';
import { PlayListState } from './models/PlayListState';
import { BusquedaState } from './models/BusquedaState';


@Injectable({
  providedIn: 'root'
})
export class ReproductorService {

  

  constructor(private spoti : SpotiService) { }

  

    getCurrentPlayList() : Observable<PlayListState> {
      return this.spoti.getCurrentPlayList().pipe(
        map(result => {
          const tracks: Track[] = [];
          let sonando: Track | null = null;
          // 2. Procesamos la canción que está sonando ahora mismo
          if (result.currently_playing) {
            const actual = this.mapToTrack(result.currently_playing);
            actual.is_active = true;
            tracks.push(actual);
            sonando = actual;
          }
          // 3. Procesamos el resto de la cola (Queue)
          if (result.queue && result.queue.length > 0) {
            result.queue.forEach((item: any) => {
              tracks.push(this.mapToTrack(item));
            });
          }
          const state: PlayListState = {
            actual: sonando,
            tracks,
            error: null
          };
          return state;
        }),
        catchError(err => of({
          actual: null,
          tracks: [],
          error: (err && err.message) ? err.message : String(err)
        }))
      );
    }



    buscarCancion(busquedaCancion: string) : Observable<BusquedaState>{
      return this.spoti.buscarCancion(busquedaCancion).pipe(
        map(result => {
          const items: any[] = result?.tracks?.items ?? [];
          const busqueda: Track[] = [];
          items.forEach((item: any) => {
                busqueda.push(this.mapToTrack(item));
          });

          const state: BusquedaState = {
            busqueda,
            error: null
          };
          return state;
        }),
        catchError(err => of({
          busqueda: [],
          error: (err && err.message) ? err.message : String(err)
        }))
      );
    }


    private mapToTrack(item: any): Track {
        return {
          id: item.id,
          name: item.name,
          artist: item.artists.map((a: any) => a.name).join(', '),
          album: item.album.name,
          cover: item.album.images[0]?.url,
          duration_ms: item.duration_ms,
          uri: item.uri,
          is_active: false
        };
      }
    

}
