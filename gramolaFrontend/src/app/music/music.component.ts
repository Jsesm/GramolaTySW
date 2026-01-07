import { Component, OnInit } from '@angular/core';
import { SpotiService } from '../spoti.service';
import { NgIf, NgFor } from '@angular/common';
import { PlayList } from '../models/playlist';
import { Track } from '../models/track';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReproductorService } from '../reproductor.service';
import { Router } from '@angular/router';
import { UserService } from '../user.service';
import { interval, Subscription, switchMap } from 'rxjs';

@Component({
  selector: 'app-music',
  standalone: true,
  imports: [CommonModule, FormsModule ],
  templateUrl: './music.component.html',
  styleUrl: './music.component.css'
})

export class MusicComponent implements OnInit {

  tracks : Track[] = [];
  actual? : Track | null;
  playlistError? : string | null;
  songError? : string | null

  busquedaCancion: string="";
  busqueda: Track[] =[];

  devices: any[] = []; 
  currentDevice: any; 
  
  isPaused: boolean=false;
  isAceptar: boolean=false;
  isCambioPass: boolean=false;
  isCerrarSesion: boolean=false;

  quieroaniadir?: Track; //Cancion que quieres añadir

  deviceError? : string //La ? significa que puede tener valor o no
   
  currentPlaylistError? : string 

  private timeoutId: any;
   private subscription: Subscription = new Subscription;

  constructor(private spoti : SpotiService, private gramola : ReproductorService, private router: Router, private userService: UserService) {}
   
  ngOnInit(): void { 
    this.getDevices()
    this.getCurrentPlayList()
    this.subscription = interval(5000)
      .pipe(
        switchMap(() => this.gramola.getCurrentPlayList())
      )
      .subscribe((state) => {
        this.actual = state.actual;
        this.tracks = state.tracks;
        this.playlistError = state.error;
        this.isPaused=!state.actual?.is_active;
      });
  }
  
  
  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  cerrarMensajePass(){
    this.isCambioPass=false;
  }

  pedircerrarSesion() {
    this.isCerrarSesion=true;
  }

  cambiarPassword() {
    this.userService.cambiarPassword()
    this.isCambioPass=true;
  }

  cancelarCerrarSesion(){
    this.isCerrarSesion=false;
  }

  cerrarSesion() {
    this.userService.logout().subscribe({
      next: () => {
        sessionStorage.clear();
        this.router.navigate(['/login'], { replaceUrl: true });
      },
      error: (err) => {
        console.error("Error al invalidar sesión en servidor", err);        
      }
    });
  }
    getCurrentPlayList() {
      this.gramola.getCurrentPlayList().subscribe((state) => {
        this.actual = state.actual;
        this.tracks = state.tracks;
        this.playlistError = state.error;
      });
    }

    desdeElprincipio() {
    this.spoti.again(this.currentDevice.id).subscribe({
        next: ()=>{
          console.log("Correcto");
        },
        error:(err)=>{
          console.error('Error en la cola:', err);

        }
      })
    }

    pausar() {
      if(this.isPaused){
        this.spoti.reanudar(this.currentDevice.id).subscribe({
          next: ()=>{
            console.log("Reanudando canción")
          },
          error:(err)=>{
            console.error('Error en la cola:', err);

          }
        })
      }else{
        this.spoti.pausar(this.currentDevice.id).subscribe({
          next: ()=>{
            console.log("Canción en pausa")
          },
          error:(err)=>{
            console.error('Error en la cola:', err);

          }
        })
      }
      this.isPaused=!this.isPaused;
    }

    pasarSiguiente() {
      this.spoti.siguiente(this.currentDevice.id).subscribe({
        next: () => {
          this.isPaused = false;
          setTimeout(() => {
            this.getCurrentPlayList();
          }, 300);
        },
        error: (err) => {
          console.error('Error al pasar canción:', err);
        }
      });
    }

    getDevices() { 
      this.resetErrors() 
      this.spoti.getDevices().subscribe({ 
        next: (result) => { 
          this.devices = result.devices; 
          this.currentDevice = this.devices.find(d => d.is_active); 
          if (!this.currentDevice) 
            this.deviceError = "No hay ningún dispositivo conectado" 
        }, 
        error: (err) => { 
          this.deviceError = err.message; 
        } 
      }); 
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

    cancelar() {
      this.isAceptar=false;
    }

    confirmarPeticion(){

      if(!this.quieroaniadir) return;

      this.spoti.aniadir(this.quieroaniadir.id).subscribe({
          next: (result) => {

            this.isAceptar=false;
            
            this.busqueda=[];

            this.timeoutId = setTimeout(() => {
          this.getCurrentPlayList();
        }, 300);

            this.busquedaCancion="";
            console.log('Cancion añadida a la cola', this.busqueda);
          },
          error: (err) => {
            console.error('Error en la cola:', err);
          }
    });

    

  }


  resetErrors() {
  this.deviceError="";
  this.playlistError="";
  this.currentPlaylistError="";
  this.songError="";
  }


    



}
