import { AfterViewInit, Component, ElementRef, EventEmitter, Output, ViewChild } from '@angular/core';
import { UserService } from '../../user.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true, 
  imports: [FormsModule, CommonModule], 
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})

  
export class RegisterComponent implements AfterViewInit {
  @Output() onGoToLogin = new EventEmitter<void>();

  nombreBar: string = '';
  email: string = '';
  pwd1: string = '';
  pwd2: string = '';
  clientId: string = '';
  clientSecret: string = '';
  codigoPostal: string = '';
  precioCancion: number=1;
  Ubireal: boolean= false;


  signatureDataUrl: string | null= null;
  private ctx!: CanvasRenderingContext2D;
  private isDrawing: boolean = false;
  private vacio: string = '';

  mensaje: string="";
  registroOK: boolean=false;
  
  canvas!: ElementRef<HTMLCanvasElement>;
  isSignaturePadVisible: boolean = false;

  constructor(private service : UserService, private router: Router) {

   }
  ngAfterViewInit(): void {
   //Poner algo
  }

   registrarse() {
      if (this.comprobarDatosIndividuales(this.nombreBar, "Su bar debe tener algún nombre")) return;
      if (this.mirarcorreo()) return;
      if (this.comprobarDatosIndividuales(this.precioCancion, "Las canciones deben tener un precio")) return;
      if (this.comprobarDatosIndividuales(this.clientId, "Debe poner un clientId")) return;
      if (this.comprobarDatosIndividuales(this.clientSecret, "Debe poner un clientSecret")) return;
      if (this.comprobarDatosIndividuales(this.signatureDataUrl, "Debe firmar para verificar que es el dueño")) return;
      if(this.mirarcodigoPostal()) return;
        
      if(this.mirarpasswords(this.pwd1, this.pwd2, "Las contraseñas no coinciden", "Debe poner una contraseña")) return;

    
      this.registroOK=true;
      this.service.register(this.email!, this.pwd1!, this.pwd2!, this.nombreBar!, this.clientId!, this.clientSecret!, this.codigoPostal!,
          this.precioCancion!, this.signatureDataUrl!, this.Ubireal).subscribe( 
            
        ok => { 
          this.mensaje="";
          console.log('Registro exitoso', ok); 
          this.router.navigate(['/correo']);
        }, 
        error => { 
          console.error('Error en el registro', error);
          this.mensaje="Ese correo ya tiene una cuenta"; 
          this.registroOK=false;
        } 
      ); 
    }

    comprobarDatosIndividuales(dato: any, mensaje: string): boolean {

      if (!dato || dato.toString().trim() === '') {
        this.mensaje = mensaje;
        return true;
      }
      return false;
    }

    mirarpasswords(dato1: string, dato2: string, mensajenoCoinciden: string, mensajenoHayPwd: string) {
      if (!dato1 || !dato2) {
        this.mensaje = mensajenoHayPwd;
        return true;
      }
      if (dato1 !== dato2) {
        this.mensaje = mensajenoCoinciden;
        return true;
      }

      if (dato1.length<8){
        this.mensaje = "La contraseña debe tener al menos 8 caracteres";
        return true;
      }

      return false;
    }

    mirarcorreo(): boolean {
      
      const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;

      if (!this.email) {
        this.mensaje = "Debe escribir un correo";
        return true;
      }

      if (!emailPattern.test(this.email)) {
        this.mensaje = "Esperamos un correo con este formato (ejemplo@dominio.com)";
        return true;
      }


      return false;
    }

    mirarcodigoPostal(): boolean {

      const cpPattern = /^\d{5}$/;

      if (!this.codigoPostal && !this.Ubireal) {
        this.mensaje = "Escriba el código postal o acepte coger su ubicación actual";
        return true;
      }

      if (!cpPattern.test(this.codigoPostal.toString())) {
        this.mensaje = "El código postal debe tener exactamente 5 números";
        return true;
      }

      return false;
    }

       
  @ViewChild('canvas') private set canvasContent(content: ElementRef<HTMLCanvasElement>) {
    if (content) {

        this.canvas = content;
        if (this.isSignaturePadVisible) {
             this.initializeCanvas();
        }
    }
}
    

    ngOnInit(): void {
        // Inicializa tus variables si es necesario
    }

    // Método para alternar la visibilidad
    toggleSignaturePad() {
        this.isSignaturePadVisible = !this.isSignaturePadVisible;
        if (this.isSignaturePadVisible) {
            setTimeout(() => {
                this.initializeCanvas();
            }, 0);
        }
    }

    initializeCanvas() {
        const canvasEl: HTMLCanvasElement = this.canvas.nativeElement;
        this.ctx = canvasEl.getContext('2d')!;

        this.ctx.clearRect(0, 0, canvasEl.width, canvasEl.height); 

        if (!this.vacio) { 
            this.vacio = canvasEl.toDataURL('image/png');
        }

        this.ctx.lineWidth = 2;
        this.ctx.lineCap = 'round';
        this.ctx.strokeStyle = '#000';

        if (this.signatureDataUrl) {
            const img = new Image();
            img.onload = () => {
                this.ctx.drawImage(img, 0, 0);
            };
            img.src = this.signatureDataUrl;
        }
    }

    startDrawing(event: MouseEvent) {
        this.isDrawing = true;
        this.ctx.beginPath();
        this.ctx.moveTo(event.offsetX, event.offsetY);
    }

    draw(event: MouseEvent) {
        if (!this.isDrawing) return;
        this.ctx.lineTo(event.offsetX, event.offsetY);
        this.ctx.stroke();
    }

    stopDrawing() {
        this.isDrawing = false;
        this.ctx.closePath();
    }

    clearCanvas() {
        const canvasEl: HTMLCanvasElement = this.canvas.nativeElement;
        this.ctx.clearRect(0, 0, canvasEl.width, canvasEl.height);
        this.signatureDataUrl = null; // También borra la firma guardada
    }

    saveSignature() {
        const canvasEl: HTMLCanvasElement = this.canvas.nativeElement;
        this.signatureDataUrl = canvasEl.toDataURL('image/png'); 

        if (this.vacio === this.signatureDataUrl) {
            this.signatureDataUrl = null;
        }else{

          console.log('Firma guardada:', this.signatureDataUrl.substring(0, 50) + '...');
        }
        
      this.isSignaturePadVisible = false;  
    }
    

    goToLogin() {
      this.onGoToLogin.emit();
    }

}