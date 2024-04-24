package Malipo.MPesa;


import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.dtcsapp.DashBoard;

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
        context.addParameter("input_Amount", DashBoard.KIASI_MALIPO+"");
        context.addParameter("input_Country", "TZN");
        context.addParameter("input_Currency", "TZS");
        context.addParameter("input_CustomerMSISDN", "255"+DashBoard.NAMBA_MALIPO+"");
        context.addParameter("input_ServiceProviderCode", "000000");
        context.addParameter("input_ThirdPartyConversationID", "asiiv02e5958774f7ba228d83d0d689761");
        context.addParameter("input_TransactionReference", "T1234C");
        context.addParameter("input_PurchasedItemsDesc", "Shoes");

        context.addHeader("Origin", "*");

        //APIResponse response = null;
        try {
            // Execute the second request
            APIResponse response = request.execute();
            if (response != null) {
                Log.d("TAG", response.getStatusCode() + " - " + response.getReason());
                Log.d("TAG", response.getResult());

                for (Map.Entry<String, String> entry : response.getBody().entrySet()) {
                    Log.d("TAG", entry.getKey() + ":" + response.getBody().get(entry.getKey()));
                }
            } else {
                Log.e("TAG", "API call failed to get result. Please check.");
                return null;
            }

            return response.getResult();
        } catch (Exception e) {
            // Handle exceptions
            Log.d("MainActivity", "IMEGOMA KUITA MPESA SYSTEM");
            logger.error("Error executing second request (API CAliing): " + e.getMessage());
            return null;
        }


    }

    protected void onPostExecute(String result) {

        if (result==null) {
            Log.d("MainActivity", "TAYARI REQUEST YA USSD IMETUMWA TAFATHALI SUBIRI NA FATA MAELEKEZO KWENYE SIMU YAKO");
            QueryTransact queryTransact = new QueryTransact();
            queryTransact.execute();
        } else {
            // Handle error...
            // acess json
            JSONObject jsonResponse = null;
            if (result != null) {
                try {
                    jsonResponse = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                }
            }
            String sessionId = jsonResponse.optString("output_ResponseDesc");
            Toast.makeText(DashBoard.myContext, sessionId+"tapesa", Toast.LENGTH_SHORT).show();
            DashBoard.progressDialog2.dismiss();
        }
    }

}
