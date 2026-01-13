package edu.uclm.es.GramolaJSV.http;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.es.GramolaJSV.model.SpotiToken;
import edu.uclm.es.GramolaJSV.services.SpotiService;

@RestController
@RequestMapping("spoti")
@CrossOrigin(origins = { "http://localhost:4200", "http://127.0.0.1:4200" }, allowCredentials = "true")
public class SpotiController {

    @Autowired
    private SpotiService service;

    @GetMapping("/getAuthorizationToken")
    public SpotiToken getAuthorizationToken(@RequestParam String code, @RequestParam String clientId,
            @RequestParam String redirect) throws JSONException, IOException {
        SpotiToken token = this.service.getAuthorizationToken(code, clientId, redirect);
        return token;

    }
}