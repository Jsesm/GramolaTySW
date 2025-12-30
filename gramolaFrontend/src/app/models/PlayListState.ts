import { Track } from './track';

export interface PlayListState {
  actual: Track | null;
  tracks: Track[];
  error: string | null;
}