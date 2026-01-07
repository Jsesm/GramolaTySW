import { Component } from '@angular/core';
import { ReproductorService } from '../reproductor.service';
import { SpotiService } from '../spoti.service';
import { Track } from '../models/track';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PaymentService } from '../payment.service';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { interval, Subscription, switchMap } from 'rxjs';

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
    stripeInitialized: boolean = false;
    precio: any;
    
    private timeoutId: any;

    nombreBar?: any;
    firma?: any;
  error: string="";

    private subscription: Subscription = new Subscription;

    constructor(private spoti : SpotiService, private gramola : ReproductorService, private paymentService: PaymentService, private userService : UserService) {}

      ngOnInit(): void { 
        this.getCurrentPlayList();
        this.crearMetodoPago();
        this.buscarDatos();

        this.subscription = interval(5000)
      .pipe(
        switchMap(() => this.gramola.getCurrentPlayList())
      )
      .subscribe((state) => {
        this.actual = state.actual;
        this.tracks = state.tracks;
        this.playlistError = state.error;
      });

        
      }


      ngOnDestroy() {
        this.subscription.unsubscribe();
      }

    buscarDatos(){
      this.userService.buscarDatos().subscribe( 
      response => {
        this.nombreBar=response.nombreBar;
        this.firma= response.firma;
        
      }, 
      err => { 
        console.log(err); 
      } 
    );

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

    aniadirCancion(){

      if(!this.quieroaniadir) return;

      this.spoti.aniadir(this.quieroaniadir.id).subscribe({
          next: (result) => {
            this.isAceptar=false;
            this.busqueda=[];
            this.busquedaCancion="";
            console.log('Cancion añadida a la cola', this.busqueda);
            
            this.timeoutId = setTimeout(() => {
              this.getCurrentPlayList();
            }, 400);

          },
          error: (err) => {
            console.error('Error en la cola:', err);
          }
    });
  }

    crearMetodoPago(){
      this.paymentService.getPublickey().subscribe({
        next: (publickey: string) => {
          this.stripe = new Stripe(publickey);
          this.stripeInitialized = true;
        },
        error: (err) => {
          console.error("Error al obtener la clave pública:", err);
          alert("Error al cargar la configuración de pagos. Por favor, recargue la página.");
        }
      });
    }

    confirmarPeticion() {
    if (!this.stripeInitialized) return alert("Cargando sistema...");
      this.paymentService.prepay("Cancion").subscribe({
        next: (res: any) => {
          this.transactionDetails = JSON.parse(res.body);
          this.precio= this.transactionDetails.precio/100;
          this.showForm();
        },
        error: (err) => alert("Error de conexión")
      });
    }

  showForm() {
    // Mostrar UI (asumiendo que manejas clases .hidden)
    document.getElementById("payment-form")?.classList.remove("hidden");
    const botonesIniciales = document.getElementById("botones-confirmacion");

    if (botonesIniciales) {
      botonesIniciales.classList.add('hidden');
    }
    
    const elements = this.stripe.elements();
    const card = elements.create("card", { hidePostalCode: true });
    card.mount("#card-element");

    // Listener simplificado
    document.getElementById("payment-form")?.addEventListener("submit", (e) => {
      e.preventDefault();
      this.payWithCard(card);
    });
  }

  payWithCard(card: any) {
    const secret = this.transactionDetails.data.client_secret;
    this.stripe.confirmCardPayment(secret, { payment_method: { card } })
      .then((result: any) => {
        if (result.error) {
          this.error= result.error.message || "Ha ocurrido un error desconocido";
          console.log("Error");
        } else if (result.paymentIntent.status === 'succeeded') {
          this.confirmarEnBackend(result);
        }
      });
  }

confirmarEnBackend(stripeResponse: any) {
  this.paymentService.guardarCancion(stripeResponse, this.transactionDetails.id, this.quieroaniadir)
    .subscribe(() => {
      this.aniadirCancion(); 
    });
}

    



}
