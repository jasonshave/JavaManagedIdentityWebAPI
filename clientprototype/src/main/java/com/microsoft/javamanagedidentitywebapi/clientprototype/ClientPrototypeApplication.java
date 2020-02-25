package com.microsoft.javamanagedidentitywebapi.clientprototype;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ClientPrototypeApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ClientPrototypeApplication.class, args);
    }

    @Value("${clientId}")
    String clientId;

    @Value("${clientSecret}")
    String clientSecret;

    @Value("${authority}")
    String authority;

    @Value("${resource-api-url}")
    String resourceApiUrl;

    @Value("${default-scope}")
    String defaultScope;

    @Override
    public void run(String... args) throws Exception {
        try {
            IAuthenticationResult result = getAccessTokenByClientCredentialGrant();

            System.out.println("Bearer token: " + result.accessToken());
            String fromWebServer = getDataFromWebServer(result.accessToken());

            System.out.println("##########################################");
            System.out.println("The server time is: " + fromWebServer);
            System.out.println("##########################################");

        } catch (Exception ex) {
            System.out.println("Oops! We have an exception of type - " + ex.getClass());
            System.out.println("Exception message - " + ex.getMessage());
            throw ex;
        }
    }

    private IAuthenticationResult getAccessTokenByClientCredentialGrant() throws Exception {

        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                clientId,
                ClientCredentialFactory.createFromSecret(clientSecret))
                .authority(authority)
                .build();

        // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(defaultScope))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }

    private String getDataFromWebServer(String accessToken) throws IOException {
        URL url = new URL(resourceApiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept","application/json");

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }

/*        ExecutorService service = Executors.newFixedThreadPool(1);

        AuthenticationContext context = new AuthenticationContext(authority, true, service);

        Future<AuthenticationResult> future = context.acquireToken(applicationIdUri, new ClientCredential(clientId, clientSecret), null);
        AuthenticationResult result = future.get();

        System.out.println("Bearer " + result.getAccessToken());

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + result.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(resourceApiUrl, HttpMethod.GET, entity, String.class);

        System.out.println("##########################################");
        System.out.println("The server's time is: " + responseEntity.getBody());
        System.out.println("##########################################");*/
    }

