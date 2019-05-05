//package com.example.payne.simpletestapp.networkTest.old;
//
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.example.payne.simpletestapp.alerts.UserAlert;
//import com.example.payne.simpletestapp.alerts.BasicAlert;
//import com.example.payne.simpletestapp.networkTest.TestObjectsInstances;
//import com.example.payne.simpletestapp.server.requests.AlertRequestType;
//import com.example.payne.simpletestapp.server.requests.RequestCategory;
//import com.example.payne.simpletestapp.server.requests.exceptions.ErrorMessage;
//import com.example.payne.simpletestapp.server.deprecated.getRequests.GetRequestAlert;
//import com.example.payne.simpletestapp.server.deprecated.getRequests.GetRequestUserAlerts;
//import com.example.payne.simpletestapp.server.deprecated.patchRequest.PatchAlertRequest;
//import com.example.payne.simpletestapp.server.deprecated.postRequests.PostRequestAlert;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.osmdroid.util.BoundingBox;
//import org.springframework.http.HttpAuthentication;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Random;
//
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.*;
//
//@RunWith(AndroidJUnit4.class)
//public class AlertRequestTests {
//
//    // todo :
//    // GET :
//    // "api/alerts/{alertType}" - check
//    // "api/alerts/{alertType}/{alertId}"
//    // POST
//    // "api/alerts/user"
//    // PATCH
//    // "api/alerts/user{alertId}”
//    // "api/alerts/user/geometry/{alertId}”
//    // DELETE
//    // "api/alerts/user{alertId}”
//
//    static private TestObjectsInstances objectsInstances;
//
//    @BeforeClass
//    public static void getInstances(){
//        objectsInstances = TestObjectsInstances.getInstance();
//    }
//
//    /* ***************
//        GET REQUESTS
//    **************** */
//
//    /**
//     * Get  :"api/alerts/{alertType}"
//     *
//     * Test get requests for all three alert types :
//     * {@link AlertRequestType#HISTORICAL}
//     * {@link AlertRequestType#LIVE}
//     * {@link AlertRequestType#USER}
//     */
//    @Test
//    public void getRequestAlertTypeTest() {
//
//        BasicAlert[] alertesHisto = new BasicAlert[]{};
//        BasicAlert[] alertes = new BasicAlert[]{};
//        UserAlert[] alertesUser = new UserAlert[]{};
//
//        GetRequestAlert alertesRequest =
//                new GetRequestAlert(AlertRequestType.HISTORICAL, alertesHisto);
//        alertesHisto = alertesRequest.send().getResponse();
//        Log.i(TAG_SERVEUR_GET, "Alertes histo length = " + alertesHisto.length);
//
//        HashSet<BasicAlert> testSet = new HashSet<>(Arrays.asList(alertes));
//
//        Assert.assertEquals("HashSet and ArrayList of same size", alertes.length, testSet.size());
//
//        alertesRequest.setAlertRequestType(AlertRequestType.LIVE);
//        alertes = alertesRequest.send().getResponse();
//        Log.i(TAG_SERVEUR_GET, "Alertes actuelle length = " + alertes.length);
//
//        alertesRequest.setAlertRequestType(AlertRequestType.USER);
//
//        GetRequestUserAlerts alertesUserRequest =
//                new GetRequestUserAlerts(alertesUser);
//        alertesUser = alertesUserRequest.send().getResponse();
//        Log.i(TAG_SERVEUR_GET, "Alertes user length = " + alertesUser.length);
//
//        Log.i(TAG_SERVEUR_GET, "Alerte user response = " + alertesUser[0].toString());
//
//    }
//
//    /**
//     * Get  :"api/alerts/{alertType}"
//     */
//    @Test
//    public void getRequestAlertWithBoxTest(){
//
//        BasicAlert[] alertes = new BasicAlert[]{};
//        BoundingBox bBox = new BoundingBox(45.5, -74.5, 44.5, -75.5);
//        GetRequestAlert alertesRequest =
//                new GetRequestAlert(AlertRequestType.HISTORICAL, bBox, alertes);
//
//        Log.i(TAG_SERVEUR_GET,"Alertes box url = " + alertesRequest.getUrl());
//
//        alertes = alertesRequest.send().getResponse();
//
//        Log.i(TAG_SERVEUR_GET,"Alertes box length = " + alertes.length + "");
//
//
//    }
//
//
//
//
//    /**
//     * Get : "api/alerts/{alertType}/{alertId}"
//     *
//     * Test get request for a specific
//     */
//    @Test
//    public void getRequestAlertId(){
//
//    }
//
//
//
//
//    /* ***************
//       POST REQUESTS
//    *************** */
//
//
//    public void postRequestAlert(){
//
//        UserAlert body = TestObjectsInstances.getInstance().userAlert;
//
//        PostRequestAlert request = new PostRequestAlert(body);
//        Log.i(TAG_SERVEUR_POST, "alerte request url = " + request.getUrl());
//        Log.i(TAG_SERVEUR_POST, "alerte body = " + body.toString());
//
//        UserAlert response = request.send().getResponse();
//
//        Log.i(TAG_SERVEUR_POST, "alert response = " + response.toString());
//
//
//
//    }
//
//
//
//
//    /* ****************
//       PATCH REQUESTS
//     *************** */
//
//    @Test
//    public void patchRequestAlert() throws InterruptedException, IOException {
//
//        Random rand = new Random();
//
//        Map<String, Object> changes = new HashMap<>();
//        changes.put("type", "testChange" + rand.nextInt());
//        changes.put("lastName", "lastNameChange" + rand.nextInt());
//
//        String userId = "tempIdForTestinBecauseServerDoesntseemToAutoGenereteOnPost983";
//
//        Thread.sleep(2000);
//        PatchAlertRequest patch = new PatchAlertRequest(userId, changes);
//
//        try {
//
//            Log.i(TAG_SERVEUR_PATCH, "Patch alert path url = " + patch.getUrl());
//
//            UserAlert patched = patch.send().getResponse();
//            Log.i(TAG_SERVEUR_PATCH, "patched" + patched.toString());
//
//            String response = new StringTestRequestBuilder<>()
//                    .category(RequestCategory.USER)
//                    .method(HttpMethod.PATCH)
//                    .body(changes)
//                    .customUrlParam(userId)
//                    .auth(new HttpAuthentication() {
//                        @Override
//                        public String getHeaderValue() {
//                            return "Authorization test";
//                        }
//                    })
//                    .build()
//                    .send().getBody();
//
//            Log.i(TAG_SERVEUR_PATCH, "String test request =" + response);
//
//        } catch (HttpClientErrorException clientErrorException){
//
//            Log.e(TAG_SERVEUR_PATCH, "HttpError = " + clientErrorException.getMessage());
//
//        } catch (HttpMessageNotReadableException serial){
//
//            // it is possible to get the ResponseEntity here
//
//            String response = new StringTestRequestBuilder<>()
//                    .category(RequestCategory.USER)
//                    .method(HttpMethod.PATCH)
//                    .body(changes)
//                    .customUrlParam(userId)
//                    // .auth(basicAuth)
//                    .build()
//                    .send().getBody();
//
//            ObjectMapper errorMap = new ObjectMapper();
//            ErrorMessage message = errorMap.readValue(response, ErrorMessage[].class)[0];
//
//            Log.e(TAG_SERVEUR_PATCH, "exception = " + serial.getLocalizedMessage());
//            Log.w(TAG_SERVEUR_PATCH, "response error = " + message.getMessage());
//            Log.w(TAG_SERVEUR_PATCH, "status code = " );
//
//        }
//
//
//    }
//
//
//}
