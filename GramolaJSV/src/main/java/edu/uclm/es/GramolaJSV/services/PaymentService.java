package edu.uclm.es.GramolaJSV.services;

import java.io.IOException;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import edu.uclm.es.GramolaJSV.configuration.ConfigurationLoader;
import edu.uclm.es.GramolaJSV.dao.CancionDao;
import edu.uclm.es.GramolaJSV.dao.PagoGramolaDao;
import edu.uclm.es.GramolaJSV.dao.StripeTransactionDao;
import edu.uclm.es.GramolaJSV.dao.TokenDao;
import edu.uclm.es.GramolaJSV.model.Cancion;
import edu.uclm.es.GramolaJSV.model.PagoGramola;
import edu.uclm.es.GramolaJSV.model.StripeTransaction;
import edu.uclm.es.GramolaJSV.model.Token;
import edu.uclm.es.GramolaJSV.model.User;

@Service
public class PaymentService {
    static {
        try {
            JSONObject stripeConfig = ConfigurationLoader.get().getJsoCOnfiguration().getJSONObject("stripe");
            Stripe.apiKey = stripeConfig.getString("secretKey");
        } catch (IOException | JSONException e) {
            System.err.println("ERROR CRÍTICO: No se pudo cargar la configuración de Stripe");
            e.printStackTrace();
        }
    }
    @Autowired
    private StripeTransactionDao stdao;
    @Autowired
    private UserService usuarioservice;
    @Autowired
    private TokenDao tokenDao;
    @Autowired
    private CancionDao cancionDao;
    @Autowired
    private PagoGramolaDao pagogramolaDao;

    public StripeTransaction prepay(String tipo, String opcion) throws StripeException, JSONException, IOException {

        long precio = this.buscarPrecio(tipo, opcion);
        PaymentIntentCreateParams createParams = this.crearFactura(precio);

        PaymentIntent intent = PaymentIntent.create(createParams);
        JSONObject transactionDetails = new JSONObject(intent.toJson());
        StripeTransaction st = new StripeTransaction();
        st.setData(transactionDetails);
        st.setPrecio(precio);
        this.stdao.save(st);
        return st;
    }

    private long buscarPrecio(String tipo, String opcion) throws JSONException, IOException {
        long precio = 0L;
        // Como no se puede hardcodear hay que escribir una fila en la base de datos con
        // id 1, PrecioMensual 1000, PrecioAnual 10000
        if (tipo.equals("Cuenta") || tipo.equals("Anual")) {
            PagoGramola pg = pagogramolaDao.findById(1)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Configuración de precios no inicializada en DB"));

            return tipo.equals("Cuenta") ? pg.getPrecioMensual() : pg.getPrecioAnual();
        } else {
            User usuariofiltro = new User();
            usuariofiltro.setClientId(opcion);
            User usuario = this.usuarioservice.buscarUsuario(usuariofiltro);
            precio = usuario.getPrecioCancion();
        }

        return precio;
    }

    private PaymentIntentCreateParams crearFactura(long precio) {
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("eur")
                .setAmount(precio)
                .build();

        return createParams;
    }

    public void confirmar(String paymentId, String tokenid) throws StripeException {
        Optional<Token> tok = this.tokenDao.findById(tokenid);
        if (tok.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el token");
        }

        Token token = tok.get();
        /*
         * token.use();
         * this.tokenDao.save(token);
         */

        User usuariofiltro = new User();
        usuariofiltro.setCreationtoken(token);
        this.completarTransaccion(paymentId, usuariofiltro);

    }

    public User completarTransaccion(String paymentId, User usuariofiltro) {
        User usuarioReal = usuarioservice.buscarUsuario(usuariofiltro);
        Optional<StripeTransaction> st = this.stdao.findById(paymentId);
        if (st.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la transaccion");
        }
        StripeTransaction transaccion = st.get();
        transaccion.setEmail(usuarioReal.getEmail());
        stdao.save(transaccion);

        return usuarioReal;
    }

    public void guardarCancion(String nombreCancion, String autorCancion, String idCancion, String paymentId,
            String clientId) {

        User usuariofiltro = new User();
        usuariofiltro.setClientId(clientId);

        User usuarioReal = this.completarTransaccion(paymentId, usuariofiltro);

        Cancion song = new Cancion();
        song.setIdCancion(idCancion);
        song.setNombreCancion(nombreCancion);
        song.setAutorCancion(autorCancion);
        song.setBar(usuarioReal.getEmail());
        this.cancionDao.save(song);
    }

    public boolean comprobarpago(String email) {

        for (StripeTransaction st : this.stdao.findAll()) {
            if (email != null && email.equals(st.getEmail())) {
                return true;
            }
        }
        return false;
    }

}
