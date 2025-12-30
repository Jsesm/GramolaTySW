import { Track } from './track';

export interface BusquedaState {
  busqueda: Track[];
  error: string | null;
}