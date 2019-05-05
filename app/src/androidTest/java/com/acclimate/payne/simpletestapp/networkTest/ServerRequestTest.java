package com.acclimate.payne.simpletestapp.networkTest;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ServerRequestTest {

    private static final String DELIM = ".";
    public static final String TAG_SERVEUR = "SERVER";
    public static final String TAG_POST = "POST";
    public static final String TAG_GET = "GET";
    public static final String TAG_PATCH = "PATCH";
    public static final String TAG_SERVEUR_POST = TAG_SERVEUR + DELIM + TAG_POST;
    public static final String TAG_SERVEUR_GET = TAG_SERVEUR + DELIM + TAG_GET;
    public static final String TAG_SERVEUR_PATCH = TAG_SERVEUR + DELIM + TAG_PATCH;
    public static final String TAG_SERVEUR_HANDLE = TAG_SERVEUR + DELIM + "HANDLE";
    public static final String TIMESTAMP = "TIMESTAMP";

    private static Context appContext = InstrumentationRegistry.getTargetContext();


    @Test
    public void testNewRequest() {

/*
        HttpRequest<User> request = GetRequest.user("x1232");
        String response = request.send().getResponseAsString();
        Log.i(TAG_SERVEUR_GET, response);

        User user = request.getResponse();
        Log.i(TAG_SERVEUR_GET, user.toString());


        HttpRequest<UserAlert[]> request1 = GetRequest.alert(AlertRequestType.USER);
        String response1 = request1.send().getResponseAsString();
        Log.i(TAG_SERVEUR_GET, response1);

        UserAlert[] alerts = request1.getResponse();
        Log.i(TAG_SERVEUR_GET, alerts.length + "");
        Log.i(TAG_SERVEUR_GET, alerts[0].toString());
*/

/*
        List<String> plusOne = new ArrayList<>();
        plusOne.add("me");
        plusOne.add("and");
        plusOne.add("you");

        Map<String, Object> body = new HashMap<>();
        body.put("nom", "AHHHHH YEAAAAAAAH");
        body.put("plusOneUsers", plusOne);

        HttpRequest<Map> request2 = PatchRequest.alert(body, "69433");
        Map<String, Object> response2 = request2.send().getResponse();

        Log.i(TAG_SERVEUR_PATCH, request2.getResponseAsString());

        for (String key : response2.keySet()) {
            Log.i(TAG_SERVEUR_PATCH, "key = " + key +
                    "  value = " + (response2.get(key) == null ? "null" : response2.get(key).toString()));

        }
*/


/*
        // test to update registration token of a User
        final String user_to_patch = "VrE9uPOY4EOrw4xtSztAvlBNxfg2";

        HttpRequest<User> getUserById = GetRequest.user(user_to_patch);
        User tmpUser = getUserById.send().getResponse();

        List<String> tokens = tmpUser.getRegistrationToken();
        tokens.add("testToke2");

        Map<String, Object> body2 = new HashMap<>();
        body2.put("registrationToken", tokens);

        HttpRequest<Map> request3 = PatchRequest.user(body2, user_to_patch);
        Map<String, Object> response3 = request3.send().getResponse();

        Log.i(TAG_SERVEUR_PATCH, request3.getResponseAsString());

        for (String key : response3.keySet()) {
            Log.i(TAG_SERVEUR_PATCH, "key = " + key +
                    "  value = " + (response3.get(key) == null ? "null" : response3.get(key).toString()));

*/
    }
}


/*
        MonitoredZone toPost = TestObjectsInstances.getInstance().monitoredZone;
        HttpRequest<MonitoredZone> postZone = PostRequest.monitoredZone(toPost);

        MonitoredZone response4 = postZone.send().getResponse();

        Log.i(TAG_SERVEUR_POST, "response monitoredZone = " + response4.toString());
*/
