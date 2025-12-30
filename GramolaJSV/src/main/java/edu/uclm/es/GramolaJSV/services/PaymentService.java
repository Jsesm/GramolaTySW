package edu.uclm.es.GramolaJSV.services;

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

import edu.uclm.es.GramolaJSV.dao.StripeTransactionDao;
import edu.uclm.es.GramolaJSV.dao.TokenDao;
import edu.uclm.es.GramolaJSV.model.StripeTransaction;
import edu.uclm.es.GramolaJSV.model.Token;

@Service
public class PaymentService {
    static {
        Stripe.apiKey = "sk_test_51SIV18I9bvpKxx36ImWVBmloO10cvdLq32rlx0TCSegg5K5kwOSauISE7PCuJJ1UsHPtMNxVyPVAdec9FgFJH4u300O4A5IXki";
    } // Clave Secreta
    @Autowired
    private StripeTransactionDao dao;
    @Autowired
    private UserService usuarioservice;
    @Autowired
    private TokenDao tokenDao;

    public StripeTransaction prepay() throws StripeException, JSONException {
        PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                .setCurrency("eur")
                .setAmount(1000L) // importe en centimos 1000 centimos y L porque es un long
                .build();
        PaymentIntent intent = PaymentIntent.create(createParams);
        JSONObject transactionDetails = new JSONObject(intent.toJson());
        StripeTransaction st = new StripeTransaction();
        st.setData(transactionDetails);
        this.dao.save(st);
        return st;
    }

    public void confirmar(String paymentId, String tokenid) throws StripeException {
        Optional<Token> tok = this.tokenDao.findById(tokenid);

        if (tok.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el token");
        }

        Token token = tok.get();
        token.use();
        this.tokenDao.save(token);

    }
}
