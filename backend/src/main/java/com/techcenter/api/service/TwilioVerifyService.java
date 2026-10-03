package com.techcenter.api.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioVerifyService {

    private final boolean enabled;
    private final String accountSid;
    private final String authToken;
    private final String serviceSid;
    private final String channel;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public TwilioVerifyService(
            @Value("${techcenter.integraciones.twilio.enabled:false}") boolean enabled,
            @Value("${techcenter.integraciones.twilio.account-sid:}") String accountSid,
            @Value("${techcenter.integraciones.twilio.auth-token:}") String authToken,
            @Value("${techcenter.integraciones.twilio.verify-service-sid:}") String serviceSid,
            @Value("${techcenter.integraciones.twilio.channel:sms}") String channel) {
        this.enabled = enabled;
        this.accountSid = accountSid;
        this.authToken = authToken;
        this.serviceSid = serviceSid;
        this.channel = channel;
    }

    public boolean configurado() {
        return enabled && !accountSid.isBlank() && !authToken.isBlank() && !serviceSid.isBlank();
    }

    public String normalizarTelefonoPeru(String telefono) {
        String limpio = telefono == null ? "" : telefono.replaceAll("[^0-9+]", "");
        if (limpio.matches("^9\\d{8}$")) {
            return "+51" + limpio;
        }
        if (limpio.matches("^51(9\\d{8})$")) {
            return "+" + limpio;
        }
        return limpio;
    }

    public boolean telefonoPeruValido(String telefono) {
        return normalizarTelefonoPeru(telefono).matches("^\\+519\\d{8}$");
    }

    public String codigoDemo() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    public boolean enviarCodigo(String telefono) {
        if (!configurado()) {
            return false;
        }
        return postTwilio("/Verifications", "To=" + encode(telefono) + "&Channel=" + encode(channel));
    }

    public boolean verificarCodigo(String telefono, String codigo) {
        if (!configurado()) {
            return false;
        }
        return postTwilio("/VerificationCheck", "To=" + encode(telefono) + "&Code=" + encode(codigo));
    }

    private boolean postTwilio(String path, String body) {
        try {
            String auth = Base64.getEncoder()
                    .encodeToString((accountSid + ":" + authToken).getBytes(StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://verify.twilio.com/v2/Services/" + serviceSid + path))
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() >= 200 && response.statusCode() < 300
                    && !response.body().contains("\"status\":\"canceled\"");
        } catch (IOException | InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }
}