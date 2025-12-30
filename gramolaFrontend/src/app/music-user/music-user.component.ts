import { Component } from '@angular/core';
import { ReproductorService } from '../reproductor.service';
import { SpotiService } from '../spoti.service';
import { Track } from '../models/track';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../payment.service';
import { Router } from '@angular/router';

// Declaramos Stripe para que TypeScript lo reconozca globalmente
declare let Stripe: any 


@Component({
  selector: 'app-music-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './music-user.component.html',
  styleUrl: './music-user.component.css'
})
export class MusicUserComponent {
    busquedaCancion: string="";
    busqueda: Track[] =[];
    songError? : string | null;
    isAceptar: boolean=false;
    quieroaniadir?: Track; //Cancion que quieres añadir
    tracks : Track[] = [];
    actual? : Track | null;
    playlistError? : string | null;

    //Método de pago
    stripe?: any;
    transactionDetails: any;
    token?: string; 
    // Nuevo estado para controlar la visibilidad y el spinner
    isLoading: boolean = false; 
    // Estado para rastrear si Stripe se inicializó correctamente
    stripeInitialized: boolean = false;

    private timeoutId: any;

    constructor(private spoti : SpotiService, private gramola : ReproductorService, private paymentService: PaymentService) {}

      ngOnInit(): void { 
        this.getCurrentPlayList()
      } 

    buscarCancion(){
      this.gramola.buscarCancion(this.busquedaCancion).subscribe((state) => {
        this.busqueda = state.busqueda;
        this.songError = state.error;
      });
    }

    escribiendoCancion(valor: string) {
      if (this.timeoutId) {
        clearTimeout(this.timeoutId);
      }
      if (valor && valor.length > 5) {
        this.timeoutId = setTimeout(() => {
          this.buscarCancion();
        }, 300);
      }
    }

    pedirCancion(cancion: Track) {
      this.quieroaniadir=cancion;
      this.isAceptar=true;
    }
    getCurrentPlayList() {
      this.gramola.getCurrentPlayList().subscribe((state) => {
        this.actual = state.actual;
        this.tracks = state.tracks;
        this.playlistError = state.error;
      });
    }

    cancelar() {
      this.isAceptar=false;
    }

    confirmarPeticion(){
      /*
      this.paymentService.getPublickey().subscribe({
        next: (publickey: string) => {
          this.stripe = new Stripe(publickey);

          
          
        },
        error: (err) => {
          console.error("Error al obtener la clave pública:", err);
          alert("Error al cargar la configuración de pagos. Por favor, recargue la página.");
        
        }
      });*/
    }



}
