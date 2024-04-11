package com.example.dtcsapp;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import mu.prevoir.sdkhttp.APIContext;
import mu.prevoir.sdkhttp.APIMethodType;
import mu.prevoir.sdkhttp.APIRequest;
import mu.prevoir.sdkhttp.APIResponse;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import org.json.JSONException;


public class Deposit extends AsyncTask<Void, Void, String> {

    private static final String TAG = Deposit.class.getSimpleName();
    public  static String FINALRESP = "";
    public  static String FINALSTATUS = "";


    private static final String APIKey = "PEmoo6ehtbknRbcckvvaR27oNeH9dCxI";
    private static final String publicKey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArv9yxA69XQKBo24BaF/D+fvlqmGdYjqLQ5WtNBb5tquqGvAvG3WMFETVUSow/LizQalxj2ElMVrUmzu5mGGkxK08bWEXF7a1DEvtVJs6nppIlFJc2SnrU14AOrIrB28ogm58JjAl5BOQawOXD5dfSk7MaAA82pVHoIqEu0FxA8BOKU+RGTihRU+ptw1j4bsAJYiPbSX6i71gfPvwHPYamM0bfI4CmlsUUR3KvCG24rB6FNPcRBhM3jDuv8ae2kC33w9hEq8qNB55uw51vK7hyXoAa+U7IqP1y6nBdlN25gkxEA8yrsl1678cspeXr+3ciRyqoRgj9RD/ONbJhhxFvt1cLBh+qwK2eqISfBb06eRnNeC71oBokDm3zyCnkOtMDGl7IvnMfZfEPFCfg5QgJVk1msPpRvQxmEsrX9MQRyFVzgy2CWNIb7c+jPapyrNwoUbANlN8adU1m6yOuoX7F49x+OjiG2se0EJ6nafeKUXw/+hiJZvELUYgzKUtMAZVTNZfT8jjb58j8GVtuS+6TM2AutbejaCV84ZK58E2CRJqhmjQibEUO6KPdD7oTlEkFy52Y1uOOBXgYpqMzufNPmfdqqqSM4dU70PO8ogyKGiLAIxCetMjjm6FCMEA3Kc8K0Ig7/XtFm9By6VxTJK1Mg36TlHaZKP6VzVLXMtesJECAwEAAQ==";
    private String namba, kiasi;
    private Context mContext;

    public Deposit(Context context) {
        this.mContext = context;
    }
    public void nambaKiasi(String nambaa, String kiasii){
        namba = nambaa;
        kiasi = kiasii;
    }
    @Override
    protected String doInBackground(Void... voids) {
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
                    .build();

            context.addHeader("Origin", "*");
            // Create a request object
            APIRequest request = new APIRequest(context);

            // Do the API call and put result in a response packet
            APIResponse response = null;

            try{
                response = request.execute();
            }
            catch( Exception e){
                Log.d(TAG, "call Failed"+ e.getMessage());
            }

            // Display results
            if (response != null) {
                Log.d(TAG, response.getStatusCode() + " - " + response.getReason());
                Log.d(TAG, response.getResult());

                for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                    Log.d(TAG, entry.getKey() + ":" + response.getBody().get(entry.getKey()));
                }
            } else {
                Log.e(TAG, "SessionKey call failed to get result. Please check.");
                return null;
            }

            // The above call issued a sessionID which can be used as the API key in calls that needs the sessionID
            context = APIContext.builder()
                    .apiKey(response.getBody().get("output_SessionID"))
                    .publicKey(publicKey)
                    .ssl(true)
                    .apiMethodType(APIMethodType.POST)
                    .address("openapi.m-pesa.com")
                    .port(443)
                    .path("/sandbox/ipg/v2/vodacomTZN/c2bPayment/singleStage/")
                    .timeout(180_000)
                    .requestTimeout(181_000)
                    .build();

            request = new APIRequest(context);

            String uniqueID= UniqueIDGenerator.generateUniqueID();
            context.addParameter("input_Amount", kiasi);
            context.addParameter("input_Country", "TZN");
            context.addParameter("input_Currency", "TZS");
            context.addParameter("input_CustomerMSISDN", "255" + namba);
            context.addParameter("input_ServiceProviderCode", "000000");
            context.addParameter("input_ThirdPartyConversationID", uniqueID);
            context.addParameter("input_TransactionReference", "T1i234C");
            context.addParameter("input_PurchasedItemsDesc", "Shoiies");

            context.addHeader("Origin", "*");

            // SessionID can take up to 30 seconds to become 'live' in the system and will be invalid until it is
            try {
                //Toast.makeText(mContext, "dsds", Toast.LENGTH_LONG).show();
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            response = null;


                try{
                    response = request.execute();
                }
                catch ( Exception e){
                    Log.d(TAG, "call Failediiiii"+ response);
                    e.printStackTrace();
                }

            if (response != null) {
                Log.d(TAG, response.getStatusCode() + " - " + response.getReason());
                Log.d(TAG, response.getResult());

                for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                    Log.d(TAG, entry.getKey() + ":" + response.getBody().get(entry.getKey()));
                }
                try{
                   JSONObject jsonResponse = new JSONObject(response.getResult());

                    // Retrieve the value associated with the key "response_desc"



                    DashBoard.progressDialog2.dismiss();
                    FINALRESP = jsonResponse.getString("output_ResponseDesc");

                    Log.d(TAG,"MAELEZOOO YA KILICHOTOKEA: " + FINALRESP);
                    DashBoard.progressDialog2.show();
                }catch (JSONException e) {
                    e.printStackTrace();
                }



            } else {
                DashBoard.progressDialog2.dismiss();
                Log.e(TAG, "API call failed to get result. Please check.");
                FINALRESP = "FAILED";

                return null;
            }

            return response.getResult();
    }
}


