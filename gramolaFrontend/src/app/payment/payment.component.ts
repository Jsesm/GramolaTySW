import { Component, OnInit } from '@angular/core'; 
import { PaymentService } from '../payment.service'; 
import { Router } from '@angular/router'; 
import { CommonModule } from '@angular/common'; // Importar CommonModule para directivas como ngIf

// Declaramos Stripe para que TypeScript lo reconozca globalmente
declare let Stripe: any 

@Component({ 
  selector: 'app-payment', 
  standalone: true, 
  // Añadimos CommonModule
  imports: [CommonModule], 
  templateUrl: './payment.component.html', 
  styleUrl: './payment.component.css' 
}) 

export class PaymentComponent implements OnInit { 
  stripe?: any;
  mensajeMensual?: string= "Disfruta de las comodidades de la gramola pagando 10€ al mes";
  mensajeAnual?: string= "¡¡SOLO HOY!! Paga 100€ por un año y el més te saldrá a solo 8,33€";
  transactionDetails: any;
  token?: string; 
  // Nuevo estado para controlar la visibilidad y el spinner
  isLoading: boolean = false; 
  
  // Estado para rastrear si Stripe se inicializó correctamente
  stripeInitialized: boolean = false;
  precio: any;

  constructor(private paymentService: PaymentService, private router : Router) { } 

  ngOnInit(): void { 
    const params = this.router.parseUrl(this.router.url).queryParams; 
    this.token = params['token']; 
    console.log("Token:", this.token);
    
    // 1. Obtener la clave pública (La llamada que estaba fallando antes)
    this.paymentService.getPublickey().subscribe({
      next: (publickey: string) => {
        console.log("Stripe Public Key recibida.");
        // Inicialización de Stripe (Ahora que sabemos que la clave llegó)
        this.stripe = new Stripe(publickey);
        this.stripeInitialized = true;
      },
      error: (err) => {
        console.error("Error al obtener la clave pública:", err);
        alert("Error al cargar la configuración de pagos. Por favor, recargue la página.");
        this.stripeInitialized = false;
      }
    });
  } 

  prepay(tipo: string) { 
    if (!this.stripeInitialized) {
      alert("El sistema de pagos aún no está inicializado. Inténtelo de nuevo.");
      return;
    }
    
    this.setLoading(true); // Mostrar spinner durante la llamada a prepay

    this.paymentService.prepay(tipo).subscribe({ 
      next: (response: any) => { 
        this.transactionDetails = JSON.parse(response.body);
        this.precio= this.transactionDetails.precio/100;
        this.showForm(); 
        this.setLoading(false); // Ocultar spinner después de obtener client_secret
      }, 
      error: (response: any) => { 
        alert("Error al iniciar la transacción: " + response.message || response); 
        this.setLoading(false); // Ocultar spinner en caso de error
      }, 
    }) 
  } 

  showForm() { 
    // Ocultar el botón inicial
    const initialButton = document.querySelector('button:not(#submit)') as HTMLElement;
    if (initialButton) {
      initialButton.classList.add('hidden');
    }

    const botonesIniciales = document.getElementById("prepay");

    if (botonesIniciales) {
      botonesIniciales.classList.add('hidden');
    }

    // Mostrar el formulario con transición suave
    let form = document.getElementById("payment-form") as HTMLElement;
    if (form) {
      form.classList.remove("hidden");
    }
    
    // Inicialización de Stripe Elements
    let elements = this.stripe.elements(); 
    let style = { 
      base: { 
        color: "#121212", fontFamily: 'Montserrat, sans-serif', 
        fontSmoothing: "antialiased", fontSize: "16px", 
        "::placeholder": { 
          color: "#121212" 
        } 
      }, 
      invalid: { 
        fontFamily: 'Arial, sans-serif', color: "#E91429", 
        iconColor: "#E91429" 
      } 
    } 
    let card = elements.create("card", { style: style,
      hidePostalCode: true
    });
    card.mount("#card-element");
    
    // Manejo de eventos de la tarjeta
    card.on("change", function (event: any) { 
      const submitButton = document.querySelector("#submit") as HTMLButtonElement;
      submitButton.disabled = event.empty || event.error; 
      document.querySelector("#card-error")!.textContent = 
        event.error ? event.error.message : ""; 
    }); 
    
    // Listener del formulario
    let self = this;
    form.addEventListener("submit", function (event) { 
      event.preventDefault(); 
      self.payWithCard(card); 
    }); 
  } 

  payWithCard(card: any) { 
    let self = this;
    this.setLoading(true); // Mostrar spinner mientras se confirma el pago
    document.querySelector("#card-error")!.textContent = ""; // Limpiar errores

    this.stripe.confirmCardPayment(this.transactionDetails.data.client_secret, { 
      payment_method: { 
        card: card 
      } 
    }).then(function (response: any) { 
      self.setLoading(false); // Ocultar spinner
      
      if (response.error) { 
        // Mostrar error de confirmación
        document.querySelector("#card-error")!.textContent = response.error.message;
        alert(response.error.message);
      } else { 
        if (response.paymentIntent.status === 'succeeded') { 
          // 2. Confirmación exitosa en el backend
          self.paymentService.confirm(response, self.transactionDetails.id, self.token!).subscribe({ 
            next: (response: any) => { 
              // Mostrar mensaje de éxito y redireccionar
              self.mensajeMensual="PAGO REALIZADO CON ÉXITO... Se te redirigirá a la pantalla de inicio de sesión."
              self.mensajeAnual="PAGO REALIZADO CON ÉXITO... Se te redirigirá a la pantalla de inicio de sesión."
              self.setLoading(true, true);
              const resultMessage = document.querySelector(".result-message") as HTMLElement;
              resultMessage.classList.remove("hidden");
              document.getElementById("payment-form")!.classList.add("hidden"); // Ocultar formulario
              setTimeout(() => {
                 self.router.navigate(["/login"]) 
              }, 3000); // Redirigir después de 3 segundos para que el usuario vea el mensaje
            }, 
            error: (error: any) => { 
              console.error("Error al confirmar el pago en el backend", error); 
              alert("Pago exitoso en Stripe, pero falló la confirmación final en el servidor.");
            } 
          }) 
        }
      } 
    }); 
  }


  
setLoading(isLoading: boolean, isSuccess: boolean = false) {
  this.isLoading = isLoading;
  
  const submitButton = document.querySelector("#submit") as HTMLButtonElement;
  const spinner = document.querySelector("#spinner") as HTMLElement;
  const buttonText = document.querySelector("#button-text") as HTMLElement;
  const successMessage = document.querySelector("#success-message") as HTMLElement;
  const facturaText = document.querySelector("#factura-text") as HTMLElement;
  const cardElement = document.querySelector("#card-element") as HTMLElement;

  if (isSuccess) {
    // ESTADO: PAGO EXITOSO
    //spinner.classList.add("hidden");
    submitButton.classList.add("hidden");
    facturaText.classList.add("hidden");
    //cardElement.classList.add("hidden");
    
    successMessage.classList.remove("hidden"); // Mostramos el éxito
    return;
  }

  // ESTADO: CARGANDO O ERROR
  if (submitButton && spinner && buttonText) {
    if (isLoading) {
      submitButton.disabled = true;
      spinner.classList.remove("hidden");
      buttonText.classList.add("hidden");
    } else {
      spinner.classList.add("hidden");
      buttonText.classList.remove("hidden");
      
      const cardError = document.querySelector("#card-error")?.textContent;
      submitButton.disabled = !!cardError;
    }
  }
}
}