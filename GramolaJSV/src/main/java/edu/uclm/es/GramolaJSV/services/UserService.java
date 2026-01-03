package edu.uclm.es.GramolaJSV.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.es.GramolaJSV.dao.UserDao;
import edu.uclm.es.GramolaJSV.model.Token;
import edu.uclm.es.GramolaJSV.model.User;
import edu.uclm.es.GramolaJSV.utils.DistanciaCoordenadas;
import edu.uclm.es.GramolaJSV.utils.StringEncryptor;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MailService correo;

    public String register(String bar, String email, String pwd, String clientId, String clientSecret,
            String latitud, String longitud, double precio, String firma) {

        Optional<User> optUser = this.userDao.findById(email);

        if (optUser.isEmpty()) {
            User user = new User();
            user.setNombre(bar);
            user.setEmail(email);
            user.setPwd(pwd);
            user.setCreationtoken(new Token());
            user.setClientId(clientId);
            user.setClientSecret(clientSecret);
            user.setLongitud(longitud);
            user.setLatitud(latitud);
            user.setPrecioCancion(precio);
            user.setFirma(firma);
            this.userDao.save(user);

            correo.mandarCorreo(email,
                    "http://127.0.0.1:8080/users/confirmToken/" + email + "?token=" + user.getCreationtoken().getId(),
                    0);
            return "OK 200";
            // return "http://localhost:8080/users/confirmToken/"+ email +"?token="+
            // user.getCreationToken().getId();
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }
    }

    public void confirmToken(String email, String token) {
        Optional<User> user = this.userDao.findById(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario");
        }

        Token userToken = user.get().getCreationtoken();

        if (!userToken.getId().equals(token)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token incorrecto");
        }

        if (userToken.isUsed()) {

            throw new ResponseStatusException(HttpStatus.GONE, "Token ya verificado");
        }

        if (userToken.getCreationTime() < System.currentTimeMillis() - 60 * 1000 * 30) {
            User userfiltro = new User();
            userfiltro.setCreationtoken(userToken);
            User usuario = this.buscarUsuario(userfiltro);

            userDao.delete(usuario);

            throw new ResponseStatusException(HttpStatus.GONE, "Token caducado");
        }

        if (userToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token ya utilizado");
        }

        userToken.use();
    }

    public String login(String email, String pwd) {

        Optional<User> usuario = this.userDao.findById(email);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario");
        }

        User user = usuario.get();

        if (!user.getCreationtoken().isUsed()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no ha sido confirmado");
        }

        if (!user.getPwd().equals(StringEncryptor.encrypt(pwd))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El usuario no esta registrado");
        }

        return user.getClientId();
    }

    public User getUserByClientId(String clientId) {
        for (User user : this.userDao.findAll()) {
            if (user.getClientId().equals(clientId)) {
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario con clientId: " + clientId);
    }

    public String comprobarBares(double latitud, double longitud) {
        double distanciaEntrePuntos;
        double menorDistancia = 2000; // Ponemos una distancia mayor a la mínima exigida
        String clientId = "";
        for (User user : this.userDao.findAll()) {
            distanciaEntrePuntos = DistanciaCoordenadas.calcularDistanciaMetros(Double.parseDouble(user.getLatitud()),
                    Double.parseDouble(user.getLongitud()), latitud, longitud);

            System.out.println(distanciaEntrePuntos);

            if (distanciaEntrePuntos <= 100 && distanciaEntrePuntos < menorDistancia) {
                clientId = user.getClientId();
            }
        }
        return clientId;
    }

    public User buscarUsuario(User usuarioFiltro) {
        ExampleMatcher matcher = ExampleMatcher.matchingAny()
                .withIgnoreNullValues()
                .withIgnoreCase();

        Example<User> example = Example.of(usuarioFiltro, matcher);

        return this.userDao.findOne(example).orElse(null);
    }

    public Map<String, String> obtenerDatos(String clientId) {
        User usuariofiltro = new User();
        usuariofiltro.setClientId(clientId);
        User usuario = this.buscarUsuario(usuariofiltro);

        Map<String, String> respuesta = new HashMap<>();

        respuesta.put("nombreBar", usuario.getNombre());
        respuesta.put("firma", usuario.getFirma());

        return respuesta;
    }

    public ResponseEntity<Void> logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Destruye la sesión en el servidor
        }
        return ResponseEntity.ok().build();
    }

    public void cambiarPassword(String clientId) {
        User usuariofiltro = new User();
        usuariofiltro.setClientId(clientId);
        User usuario = this.buscarUsuario(usuariofiltro);

        correo.mandarCorreo(usuario.getEmail(),
                "http://127.0.0.1:4200/change?email=" + usuario.getEmail(), 1);
    }

    public Map<String, String> recuperarDatos(String email) {
        User usuariofiltro = new User();
        usuariofiltro.setEmail(email);
        User usuario = this.buscarUsuario(usuariofiltro);

        Map<String, String> respuesta = new HashMap<>();

        respuesta.put("email", usuario.getEmail());
        respuesta.put("nombreBar", usuario.getNombre());

        return respuesta;
    }

    public void actualizarDatos(String email, String nombreBar, String emailNuevo, String pwd) {
        User usuariofiltro = new User();
        usuariofiltro.setEmail(email);
        User usuario = this.buscarUsuario(usuariofiltro);

        if (!nombreBar.isEmpty()) {
            usuario.setNombre(nombreBar);
        }

        if (!emailNuevo.isEmpty()) {
            usuario.setEmail(emailNuevo);
        }

        if (!pwd.isEmpty()) {
            usuario.setPwd(pwd);
        }

        userDao.save(usuario);

    }

}
