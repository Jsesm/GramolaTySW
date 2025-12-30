import { Track } from './track';

export interface PlayList {
  id: string;               // ID de la playlist
  name: string;             // Nombre de la lista
  description?: string;     // Descripción (opcional)
  image?: string;           // Portada de la playlist
  owner?: string;           // Quién la creó
  tracks: Track[];          // El array de canciones que vas a mostrar
  total_tracks: number;     // Total de canciones en la lista
}