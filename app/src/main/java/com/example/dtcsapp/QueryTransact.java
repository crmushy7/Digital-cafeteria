package com.example.dtcsapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import mu.prevoir.sdkhttp.APIContext;
import mu.prevoir.sdkhttp.APIMethodType;
import mu.prevoir.sdkhttp.APIRequest;
import mu.prevoir.sdkhttp.APIResponse;

public class QueryTransact extends AsyncTask<Void, Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(QueryTransact.class);


    @Override
    protected String doInBackground(Void... voids) {
        // Create Context with API to request a Session ID
        APIContext context = APIContext.builder()
                // Api key
                .apiKey(PataSession.APIKey)
                // Public key
                .publicKey(PataSession.publicKey)
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

        // Add/update headers
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

        // Display results
        if (response != null) {
            System.out.println("\n\n");
            Log.d("MainActivity", "*** REQUEST YA KUOMBA MUAMALA INAANZIA HAPA ***");
            System.out.println(response.getStatusCode() + " - " + response.getReason());
            System.out.println(response.getResult());

            for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                System.out.println(entry.getKey() + ":" + response.getBody().get(entry.getKey()));
            }
        } else {
            System.err.println("SessionKey call failed to get result. Please check.");
            return null;
        }

        // The above call issued a sessionID which can be used as the API key in calls that needs the sessionID
        context = APIContext.builder()
                .apiKey(response.getBody().get("output_SessionID"))
                .publicKey(PataSession.publicKey)
                .ssl(true)
                .apiMethodType(APIMethodType.GET)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/sandbox/ipg/v2/vodacomTZN/queryTransactionStatus/")
                .build();

        request = new APIRequest(context);

        context.addParameter("input_QueryReference", "000000000000000000001");
        context.addParameter("input_ServiceProviderCode", "000000");
        context.addParameter("input_ThirdPartyConversationID", "asiiv02e5958774f7ba228d83d0d689761");
        context.addParameter("input_Country", "TZN");

        context.addHeader("Origin", "*");

        // SessionID can take up to 30 seconds to become 'live' in the system and will be invalid until it is
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        response = null;
        try {
            response = request.execute();
        } catch (Exception e) {
            System.out.println("Call failed: " + e.getMessage());
        }

        if (response != null) {
            System.out.println(response.getStatusCode() + " - " + response.getReason());
            System.out.println(response.getResult());

            for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                System.out.println(entry.getKey() + ":" + response.getBody().get(entry.getKey()));
            }


            // acess json
            JSONObject jsonResponse = null;
            if (response != null && response.getResult() != null) {
                try {
                    jsonResponse = new JSONObject(response.getResult());
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            }
            String sessionId = jsonResponse.optString("output_ResponseDesc");
            Log.d("MainActivity", "SESSION ID YA OPENAPI: " + sessionId);
            DashBoard.progressDialog2.dismiss();


        } else {
            System.err.println("API call failed to get result. Please check.");
            DashBoard.progressDialog2.dismiss();
            return null;
        }

        return response.getResult();
    }
}
