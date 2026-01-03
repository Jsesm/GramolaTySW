package edu.uclm.es.GramolaJSV.http;

import java.io.IOException;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.es.GramolaJSV.configuration.ConfigurationLoader;
import edu.uclm.es.GramolaJSV.model.StripeTransaction;
import edu.uclm.es.GramolaJSV.services.PaymentService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("payment")
@CrossOrigin(origins = { "http://localhost:4200", "http://127.0.0.1:4200" }, allowCredentials = "true")
public class PaymentsController {
    @Autowired
    private PaymentService service;

    @GetMapping("/prepay")
    public StripeTransaction prepay(@RequestParam String tipo, @RequestParam String opcion, HttpSession session) {
        try {
            StripeTransaction transactionDetails = this.service.prepay(tipo, opcion);

            session.setAttribute("transactionDetails", transactionDetails);
            return transactionDetails;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody Map<String, Object> payload) {

        try {
            JSONObject json = new JSONObject(payload);
            String transactionId = json.getString("transactionId");
            String token = json.getString("token");
            String status = json.getJSONObject("paymentIntent").getString("status");

            if (!"succeeded".equals(status)) {
                throw new Exception("El pago no se ha completado correctamente. Estado: " + status);
            }

            this.service.confirmar(transactionId, token);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/plain")
                    .body("Pago confirmado y guardado");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @PostMapping("/guardarCancion")
    public ResponseEntity<String> guardarCancion(@RequestBody Map<String, Object> payload) {

        try {
            JSONObject json = new JSONObject(payload);
            String transactionId = json.getString("transactionId");
            String nombreCancion = json.getString("nombreCancion");
            String autorCancion = json.getString("autorCancion");
            String idCancion = json.getString("idCancion");
            String clientId = json.getString("clientId");
            String status = json.getJSONObject("paymentIntent").getString("status");

            if (!"succeeded".equals(status)) {
                throw new Exception("El pago no se ha completado correctamente. Estado: " + status);
            }

            this.service.guardarCancion(nombreCancion, autorCancion, idCancion, transactionId, clientId);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Content-Type", "text/plain")
                    .body("Pago confirmado y guardado");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    @GetMapping("/getPublickey")
    public String getPublickey() throws JSONException, IOException {
        return ConfigurationLoader.get().getJsoCOnfiguration().getJSONObject("stripe").getString("publicKey");
    }

}
