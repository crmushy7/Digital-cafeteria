package com.example.dtcsapp;

import android.os.AsyncTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import mu.prevoir.sdkhttp.APIContext;
import mu.prevoir.sdkhttp.APIMethodType;
import mu.prevoir.sdkhttp.APIRequest;
import mu.prevoir.sdkhttp.APIResponse;
import com.example.dtcsapp.ItaMpesa;


public class PataSession extends AsyncTask<Void, Void, APIResponse> {

    public static final String APIKey = "PEmoo6ehtbknRbcckvvaR27oNeH9dCxI";

    public static final String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArv9yxA69XQKBo24BaF/D+fvlqmGdYjqLQ5WtNBb5tquqGvAvG3WMFETVUSow/LizQalxj2ElMVrUmzu5mGGkxK08bWEXF7a1DEvtVJs6nppIlFJc2SnrU14AOrIrB28ogm58JjAl5BOQawOXD5dfSk7MaAA82pVHoIqEu0FxA8BOKU+RGTihRU+ptw1j4bsAJYiPbSX6i71gfPvwHPYamM0bfI4CmlsUUR3KvCG24rB6FNPcRBhM3jDuv8ae2kC33w9hEq8qNB55uw51vK7hyXoAa+U7IqP1y6nBdlN25gkxEA8yrsl1678cspeXr+3ciRyqoRgj9RD/ONbJhhxFvt1cLBh+qwK2eqISfBb06eRnNeC71oBokDm3zyCnkOtMDGl7IvnMfZfEPFCfg5QgJVk1msPpRvQxmEsrX9MQRyFVzgy2CWNIb7c+jPapyrNwoUbANlN8adU1m6yOuoX7F49x+OjiG2se0EJ6nafeKUXw/+hiJZvELUYgzKUtMAZVTNZfT8jjb58j8GVtuS+6TM2AutbejaCV84ZK58E2CRJqhmjQibEUO6KPdD7oTlEkFy52Y1uOOBXgYpqMzufNPmfdqqqSM4dU70PO8ogyKGiLAIxCetMjjm6FCMEA3Kc8K0Ig7/XtFm9By6VxTJK1Mg36TlHaZKP6VzVLXMtesJECAwEAAQ==";

    private static final Logger logger = LoggerFactory.getLogger(PataSession.class);

    @Override
    protected APIResponse doInBackground(Void... voids) {
        // Create Context with API to request a Session ID
        APIContext context = APIContext.builder()
                // Api key
                .apiKey(APIKey)
                // Public key
                .publicKey(publicKey)
                // Use ssl/https
                .ssl(true)
                // Method type (can be GET/POST/PUT)
                .apiMethodType(APIMethodType.GET)
                // API address
                .address("openapi.m-pesa.com")
                // API Port
                .port(443)
                // API Path
                .path("/sandbox/ipg/v2/vodacomTZN/getSession/")
                // transaction timeout in seconds
                .timeout(180_000)
                // request timeout in seconds
                .requestTimeout(181_000)
                .build();

        context.addHeader("Origin", "*");

        // Create a request object
        APIRequest request = new APIRequest(context);

        // Do the API call and put result in a response packet
        APIResponse response = null;

        try {
            response = request.execute();
        } catch (Exception e) {
            System.out.println("Call failed: " + e.getMessage());
        }

        // Display results of session generated and check if imefanikiwa
        // can start the second  request ya mobile system
        if (response != null && response.getStatusCode() == 200) {
            System.out.println(response.getStatusCode() + " - " + response.getReason());
            System.out.println(response.getResult());

            for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                System.out.println(entry.getKey() + ":" + response.getBody().get(entry.getKey()));
            }
        } else {
            System.err.println("SessionKey call failed to get result. Please check.");
            return null;
        }
        return response;
    }

    @Override
    protected void onPostExecute(APIResponse response1) {
        // Execute the second request only if the first request was successful
        if (response1 != null && response1.getStatusCode() == 200) {
            ItaMpesa itaMpesa = new ItaMpesa();
            itaMpesa.execute(response1); // Pass the response of the first request to the second one
        } else {
            // Handle error if the first request fails
            //System.out.println("haija kufika kwenye onPostMethod ya Session generation");
            logger.debug("Session Generation failed");
        }
    }
}
