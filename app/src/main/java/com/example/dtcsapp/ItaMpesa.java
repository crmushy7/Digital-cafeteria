package com.example.dtcsapp;

import android.os.AsyncTask;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mu.prevoir.sdkhttp.APIContext;
import mu.prevoir.sdkhttp.APIMethodType;
import mu.prevoir.sdkhttp.APIRequest;
import mu.prevoir.sdkhttp.APIResponse;

public class ItaMpesa extends AsyncTask<APIResponse, Void, String> {

    private static final Logger logger = LoggerFactory.getLogger(ItaMpesa.class);

    @Override
    protected String doInBackground(APIResponse... responses) {
        // Extract session ID from the first response
        String sessionID = responses[0].getBody().get("output_SessionID");

        // Create Context with API to make the second request
        APIContext context = APIContext.builder()
                .apiKey(sessionID)  // Use sessionID as the API key
                .publicKey(PataSession.publicKey)
                .ssl(true)
                .apiMethodType(APIMethodType.POST)
                .address("openapi.m-pesa.com")
                .port(443)
                .path("/sandbox/ipg/v2/vodacomTZN/c2bPayment/singleStage/")
                .build();
        // Create a request object
        APIRequest request = new APIRequest(context);

        // Add parameters for the second request
        context.addParameter("input_Amount", DashBoard.KIASI_MALIPO + "");
        context.addParameter("input_Country", "TZN");
        context.addParameter("input_Currency", "TZS");
        context.addParameter("input_CustomerMSISDN", DashBoard.NAMBA_MALIPO + "");
        context.addParameter("input_ServiceProviderCode", "000000");
        context.addParameter("input_ThirdPartyConversationID", "asiiv02e5958774f7ba228d83d0d689761");
        context.addParameter("input_TransactionReference", "T1234C");
        context.addParameter("input_PurchasedItemsDesc", "Shoes");

        context.addHeader("Origin", "*");


        try {
            // Execute the second request
            request.execute();
            return null;
        } catch (Exception e) {
            // Handle exceptions
            Log.d("MainActivity", "IMEGOMA KUITA MPESA SYSTEM");
            logger.error("Error executing second request (API CAliing): " + e.getMessage());
            return null;
        }


    }

    protected void onPostExecute(String result) {
        Log.d("MainActivity", "TAYARI REQUEST YA USSD IMETUMWA TAFATHALI SUBIRI NA FATA MAELEKEZO KWENYE SIMU YAKO");
        QueryTransact queryTransact = new QueryTransact();
        queryTransact.execute();

//        if (result != null) {
//            // Process the result...
//        } else {
//            // Handle error...
//        }
    }

}
