package edu.uclm.es.GramolaJSV.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.es.GramolaJSV.configuration.ConfigurationLoader;
import edu.uclm.es.GramolaJSV.dao.TokenDao;
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
    @Autowired
    private TokenDao tokenDao;
    @Autowired
    @Lazy
    private PaymentService paymentService;

    public void register(String bar, String email, String pwd, String clientId, String clientSecret,
            String latitud, String longitud, double precio, String firma) throws JSONException, IOException {

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
            String urlBase = ConfigurationLoader.get().getJsoCOnfiguration().getString("urlconfirmToken");

            correo.mandarCorreo(email,
                    urlBase + email + "?token=" + user.getCreationtoken().getId(),
                    0);

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

        if (this.paymentService.comprobarpago(email) && userToken.isUsed()) { // Si ha pagado y está verficado le
                                                                              // redirijo al login
            throw new ResponseStatusException(HttpStatus.SEE_OTHER, "Ya verificado y pagado");
        }

        if (userToken.isUsed()) { // Si ya está verficado pero no ha pagado le borro la cuenta
            User userfiltro = new User();
            userfiltro.setCreationtoken(userToken);
            User usuario = this.buscarUsuario(userfiltro);
            userDao.delete(usuario);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token incorrecto");
        }

        if (userToken.getCreationTime() < System.currentTimeMillis() - 60 * 1000 * 30) { // Si ha caducado le borro la
                                                                                         // cuenta
            User userfiltro = new User();
            userfiltro.setCreationtoken(userToken);
            User usuario = this.buscarUsuario(userfiltro);
            userDao.delete(usuario);
            throw new ResponseStatusException(HttpStatus.GONE, "Token caducado");

        }

        userToken.use();
        this.tokenDao.save(userToken);
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

        if (!this.paymentService.comprobarpago(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario no ha pagado");
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

    public void comprobarBares(double latitud, double longitud, String clientId) {
        double distanciaEntrePuntos;
        User usuariofiltro = new User();
        usuariofiltro.setClientId(clientId);
        User user = this.buscarUsuario(usuariofiltro);

        distanciaEntrePuntos = DistanciaCoordenadas.calcularDistanciaMetros(Double.parseDouble(user.getLatitud()),
                Double.parseDouble(user.getLongitud()), latitud, longitud);

        System.out.println("Hay un diferencia de: " + distanciaEntrePuntos);

        if (distanciaEntrePuntos > 100) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario con clientId: " + clientId);
        }

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

    public void cambiarPassword(String clientId, String email) throws JSONException, IOException {
        User usuariofiltro = new User();
        usuariofiltro.setClientId(clientId);
        User usuario = this.buscarUsuario(usuariofiltro);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario no encontrado");
        }
        Token pwdtoken = new Token();
        usuario.setPwdtoken(pwdtoken);
        this.userDao.save(usuario);
        String urlBase = ConfigurationLoader.get().getJsoCOnfiguration().getString("urlChange");
        correo.mandarCorreo(
                email,
                urlBase + "?id=" + pwdtoken.getId() + "&email=" + usuario.getEmail(),
                1);
    }

    public Map<String, String> recuperarDatos(String email, String tokenid) {
        String tokenUsuario;
        User usuariofiltro = new User();
        usuariofiltro.setEmail(email);
        User usuario = this.buscarUsuario(usuariofiltro);

        if (usuario == null || usuario.getPwdtoken() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene un token de cambio de contraseña");
        }

        Token tokusuario = usuario.getPwdtoken();

        tokenUsuario = tokusuario.getId();

        if (!tokenUsuario.equals(tokenid)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No tiene un token de cambio de contraseña");
        }

        if (tokusuario.getCreationTime() < System.currentTimeMillis() - 60 * 1000 * 30) {
            throw new ResponseStatusException(HttpStatus.GONE, "Token caducado");
        }

        tokusuario.use();
        this.tokenDao.save(tokusuario);

        Map<String, String> respuesta = new HashMap<>();

        respuesta.put("email", usuario.getEmail());
        respuesta.put("nombreBar", usuario.getNombre());

        return respuesta;
    }

    public void actualizarDatos(String email, String nombreBar, String emailNuevo, String pwd) {
        User usuariofiltro = new User();
        usuariofiltro.setEmail(email);
        User usuario = this.buscarUsuario(usuariofiltro);

        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.GONE, "NO existe el usuario");

        }

        if (usuario.getPwdtoken() == null) {
            throw new ResponseStatusException(HttpStatus.GONE, "El usuario no ha pedido cambiar sus datos");
        }

        if (!usuario.getPwdtoken().isUsed()) {
            throw new ResponseStatusException(HttpStatus.GONE, "No hemos comprobado su id anteriormente");
        }

        if (!nombreBar.isEmpty()) {
            usuario.setNombre(nombreBar);
        }

        if (!emailNuevo.isEmpty()) {
            usuario.setEmail(emailNuevo);
        }

        if (!pwd.isEmpty()) {
            usuario.setPwd(pwd);
        }

        usuario.setPwdtoken(null);
        userDao.save(usuario);

    }

}
