//package com.example.payne.simpletestapp.networkTest.old;
//
//import android.util.Log;
//
//import com.example.payne.simpletestapp.monitoredZones.MonitoredZone;
//import com.example.payne.simpletestapp.networkTest.TestObjectsInstances;
//import com.example.payne.simpletestapp.server.deprecated.postRequests.PostRequestZone;
//
//import org.junit.Test;
//
//import static com.example.payne.simpletestapp.networkTest.ServerRequestTest.TAG_SERVEUR_POST;
//
//public class MonitoredZoneTests {
//
////    GET :
////    "api/users"
////    "api/users/{uId}”
////
////    POST
////    "api/users"
////
////    PATCH
////    "api/users/{uId}”
////    " api/users/karma/{uId}"
////
////    DELETE
////    "api/users/{uId}”
//
//
//    @Test
//    public void postZone(){
//
//
//        MonitoredZone zone = TestObjectsInstances.getInstance().zone;
//        PostRequestZone post = new PostRequestZone(zone);
//        MonitoredZone response = post.send().getResponse();
//
//        Log.i(TAG_SERVEUR_POST, "response = " + response.toString());
//
//    }
//
//}
