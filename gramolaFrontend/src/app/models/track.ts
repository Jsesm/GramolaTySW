export interface Track {
  id: string;               // ID único de Spotify
  name: string;             // Nombre de la canción
  artist: string;           // Nombre(s) del artista (puedes unir varios con comas)
  album: string;            // Nombre del álbum
  cover: string;            // URL de la imagen (usualmente la de 300x300 o 640x640)
  duration_ms: number;      // Duración para la barra de progreso
  uri: string;              // URI de Spotify (necesario para reproducir)
  preview_url?: string;     // URL de 30 segundos (opcional, algunos no tienen)
  is_active?: boolean;      // Para resaltar si está sonando ahora
}