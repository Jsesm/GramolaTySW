package edu.uclm.es.GramolaJSV.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import edu.uclm.es.GramolaJSV.configuration.ConfigurationLoader;
import edu.uclm.es.GramolaJSV.model.SpotiToken;
import edu.uclm.es.GramolaJSV.model.User;

@Service
public class SpotiService {

    // private RestClient restClient;
    private String tokenUrl = "https://accounts.spotify.com";

    @Autowired
    private UserService userService;

    public SpotiToken getAuthorizationToken(String code, String clientId, String redirect)
            throws JSONException, IOException {

        RestClient restClient = RestClient.create();
        User user = this.userService.getUserByClientId(clientId);
        String clientSecret = user.getClientSecret();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", ConfigurationLoader.get().getJsoCOnfiguration().getString("urlHome") + redirect);

        String header = this.basicAuth(clientId, clientSecret);
        String url = this.tokenUrl + "/api/token";
        SpotiToken token = restClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, header)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(SpotiToken.class);
        user.setSpotiSimpleToken(token);
        return token;
    }

    private String basicAuth(String clientId, String clientSecret) {
        String pair = clientId + ":" + clientSecret;
        return "Basic " + Base64.getEncoder().encodeToString(pair.getBytes(StandardCharsets.UTF_8));
    }
}
