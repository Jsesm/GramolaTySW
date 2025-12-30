package edu.uclm.es.GramolaJSV.configuration;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationLoader {

    private JSONObject jsoConfiguration;
    private static ConfigurationLoader yo;

    private ConfigurationLoader() throws IOException {
        ClassLoader classloader = this.getClass().getClassLoader();
        try (InputStream fis = classloader.getResourceAsStream("config.json")) {
            byte[] b = new byte[fis.available()];
            fis.read(b);
            String s = new String(b);
            this.jsoConfiguration = new JSONObject(s);
        }

    }

    @Bean
    public static ConfigurationLoader get() throws IOException {
        if (yo == null) {
            yo = new ConfigurationLoader();
        }
        return yo;
    }

    public JSONObject getJsoCOnfiguration() {
        return jsoConfiguration;
    }
}
