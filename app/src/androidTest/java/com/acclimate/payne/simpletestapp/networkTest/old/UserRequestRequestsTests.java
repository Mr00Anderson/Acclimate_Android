//package com.example.payne.simpletestapp.networkTest.old;
//
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import com.example.payne.simpletestapp.networkTest.TestObjectsInstances;
//import com.example.payne.simpletestapp.server.requests.HttpBearerAuthentification;
//import com.example.payne.simpletestapp.server.requests.RequestCategory;
//import com.example.payne.simpletestapp.server.deprecated.getRequests.GetRequest;
//import com.example.payne.simpletestapp.server.deprecated.getRequests.GetRequestUser;
//import com.example.payne.simpletestapp.server.deprecated.patchRequest.PatchUserRequest;
//import com.example.payne.simpletestapp.server.deprecated.postRequests.PostRequestUser;
//import com.example.payne.simpletestapp.user.User;
//
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.http.HttpBasicAuthentication;
//import org.springframework.http.HttpMethod;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.TAG_SERVEUR_GET;
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.TAG_SERVEUR_PATCH;
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.TAG_SERVEUR_POST;
//
//@RunWith(AndroidJUnit4.class)
//public class UserRequestRequestsTests {
//
//    private static TestObjectsInstances objectsInstances;
//
//    @BeforeClass
//    public static void getInstances(){
//        objectsInstances = TestObjectsInstances.getInstance();
//    }
//
//
//    /**
//     * Get request : "api/users/{uId}”
//     */
//    @Test
//    public void getRequestForUserFromId(){
//
//        String uid = "greggy4987";
//
//        GetRequestUser request = new GetRequestUser(uid);
////        Log.i(TAG_SERVEUR_GET, "get request url = " + request.getUrl());
//
//        User user = request.send().getResponse();
////        Log.i(TAG_SERVEUR_GET, "UserRequest response = " + user.toString());
//
//    }
//
//    /**
//     * get request : "api/users"
//     */
//    @Test
//    public void getAllUserRequest(){
//
//        User[] users = new User[]{};
//
//        GetRequest<User> request = new GetRequest<>(RequestCategory.USER, users);
////        Log.i(TAG_SERVEUR_POST, "get request url = " + request.getUrl());
//
//        users = request.send().getResponse();
////        Log.i(TAG_SERVEUR_POST, "AllUsers response length = " + users.length);
////        Log.i(TAG_SERVEUR_POST, "AllUsers first user = " + users[0].toString());
//
//    }
//
//
//    /**
//     * "api/users"
//     */
//    @Test
//    public void postUserTest() {
//
//        User user = objectsInstances.user;
//
//        PostRequestUser request = new PostRequestUser(user);
//
//        Log.i(TAG_SERVEUR_POST, user.toString());
//
//        Log.i(TAG_SERVEUR_POST, "user requet url = " + request.getUrl());
//
//        User response = request.send().getResponse();
//        Log.i(TAG_SERVEUR_POST, "user requet response = " + response.toString());
//        Assert.assertEquals(user.getuId(), response.getuId());
//
//    }
//
//
//    /* **************
//        PATCH USER
//     ************* */
//
//    @Test
//    public void patchUserRequest(){
//
//        Random rand = new Random();
//        User user = objectsInstances.user;
//
//        PostRequestUser request = new PostRequestUser(user);
//        request.send().getResponse();
//        Log.i(TAG_SERVEUR_PATCH, "Before user change" + user.toString());
//
//        Map<String, Object> changes = new HashMap<>();
//        changes.put("userName", "testChange" + rand.nextInt());
//        changes.put("lastName", "lestNameChange" + rand.nextInt());
//
//        PatchUserRequest patch = new PatchUserRequest(user, changes);
//        // UserRequest changed = patch.send().getResponse();
//
//        // Log.i(TAG_SERVEUR_PATCH, "After user change" + changed.toString());
//
//        HttpBasicAuthentication basicAuth = new HttpBasicAuthentication("test", "passTest");
//        HttpBearerAuthentification bearerAuth = new HttpBearerAuthentification("test");
//
//        String response = new StringTestRequestBuilder<>()
//                .category(RequestCategory.USER)
//                .method(HttpMethod.PATCH)
//                .body(changes)
//                .customUrlParam(user.getuId())
//                .auth(basicAuth)
//                .build()
//                .send().getBody();
//
//        Log.i(TAG_SERVEUR_PATCH, "String test request =" + response);
//
//    }
//
//
//    @Test
//    public void patchUserKarmaTest(){
//
//
//
//    }
//
//}
