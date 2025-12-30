package edu.uclm.es.GramolaJSV.http;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.es.GramolaJSV.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://127.0.0.1:4200", allowCredentials = "true")
@RestController // Indica que es un controlador
@RequestMapping("users")
public class UserController {

    @Autowired // Esto instancia el service automaticamente en vez de poner new UserService()
    // Para que funcione autowired la clase UserService tiene que tener notacion
    // Spring
    private UserService service;

    @CrossOrigin(origins = "http://127.0.0.1:4200", allowCredentials = "true") // Esto alomejor es mejor ponerlo arriba
                                                                               // debajo de @RequestMapping("users")
    @PostMapping("/login")
    public String login(HttpServletResponse response, HttpSession session, @RequestBody Map<String, String> body) {

        String email = body.get("email");
        String pwd = body.get("pwd");

        if (!email.contains("@") || !email.contains(".")) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid address");
        }

        // session.setAttribute("user", user);//MIRA LA FOTO

        // Cookie gramolaCookie = new Cookie("Gramola_cookie",
        // UUID.randomUUID().toString());
        // response.addCookie(gramolaCookie);

        return this.service.login(email, pwd); // Cuando te logueas el backend le devuelve al frontend el client id (de
                                               // Spotify)

    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> body) {

        String bar = body.get("bar");
        String email = body.get("email");
        String pwd1 = body.get("pwd1");
        String pwd2 = body.get("pwd2");
        String clientId = body.get("clientId");
        String clientSecret = body.get("clientSecret");
        String latitud = body.get("latitud");
        String longitud = body.get("longitud");
        String precioCancion = body.get("precioCancion");
        int precio = Integer.parseInt(precioCancion);
        String firma = body.get("firma");

        if (!pwd1.equals(pwd2)) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Password do not match");
        }

        if (pwd1.length() < 8) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Password too short");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Invalid address");
        }

        return this.service.register(bar, email, pwd1, clientId, clientSecret, latitud, longitud, precio, firma);

    }

    @GetMapping("/confirmToken/{email}")
    public void confirmToken(@PathVariable String email, @RequestParam String token, HttpServletResponse response)
            throws IOException {
        this.service.confirmToken(email, token);
        response.sendRedirect("http://127.0.0.1:4200/payment?token=" + token);
        // response.setRedirect(la parte del pago, le pasas el token)
    }

    @GetMapping("/bares")
    public String obtenerBarescercanos(@RequestParam double latitud, @RequestParam double longitud) {

        return this.service.comprobarBares(latitud, longitud);

    }

}
