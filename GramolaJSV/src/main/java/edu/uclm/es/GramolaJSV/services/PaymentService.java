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
import edu.uclm.es.GramolaJSV.dao.StripeTransactionDao;
import edu.uclm.es.GramolaJSV.dao.TokenDao;
import edu.uclm.es.GramolaJSV.model.Cancion;
import edu.uclm.es.GramolaJSV.model.StripeTransaction;
import edu.uclm.es.GramolaJSV.model.Token;
import edu.uclm.es.GramolaJSV.model.User;

@Service
public class PaymentService {
    static {
        Stripe.apiKey = "sk_test_51SIV18I9bvpKxx36ImWVBmloO10cvdLq32rlx0TCSegg5K5kwOSauISE7PCuJJ1UsHPtMNxVyPVAdec9FgFJH4u300O4A5IXki";
    } // Clave Secreta
    @Autowired
    private StripeTransactionDao stdao;
    @Autowired
    private UserService usuarioservice;
    @Autowired
    private TokenDao tokenDao;
    @Autowired
    private CancionDao cancionDao;

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
        if (tipo.equals("Cuenta")) {
            precio = ConfigurationLoader.get().getJsoCOnfiguration().getJSONObject("stripe").getLong("suscriptionMes");
        } else if (tipo.equals("Anual")) {
            precio = ConfigurationLoader.get().getJsoCOnfiguration().getJSONObject("stripe")
                    .getLong("suscriptionAnual");
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
        song.setBar(usuarioReal.getNombre());
        this.cancionDao.save(song);
    }

}
