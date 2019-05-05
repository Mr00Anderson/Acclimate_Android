/*
package com.example.payne.simpletestapp.map.infoWindow;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.payne.simpletestapp.appUtils.Async;
import com.example.payne.simpletestapp.server.requests.HttpRequest;
import com.example.payne.simpletestapp.server.requests.PatchRequest;
import com.example.payne.simpletestapp.server.requests.RequestHandler;
import com.example.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.example.payne.simpletestapp.user.User;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import static com.example.payne.simpletestapp.server.Server.KARMA;

@Getter @Setter
public class BubbleKarmaController implements BubbleKarma {

    private User user;
    private UserInfoWindow infoWindow;
    private View button;

    BubbleKarmaController(User user, UserInfoWindow infoWindow, View button){
        this.user = user; this.infoWindow = infoWindow; this.button = button;
    }

    public int getPoints() {
        return user.getPoints();
    }

    public void patchAlertScore(Map<String, Object> body, String alertId) {

        infoWindow.whileRequestPending(button);

        HttpRequest<Map> request = PatchRequest.alert(body, alertId);
        RequestHandler<Map> handler = new RequestHandler<>(request,
                this::patchRequestSucceed, this::patchRequestFail);
        handler.handle();

    }

    // karma failed
    public void notEnoughKarmaToVote(Map<String, Object> body, String userId) {
        infoWindow.notEnoughKarmaToVote(button);
    }


    private void patchRequestSucceed(Map response){
        infoWindow.onRequestReceived(response);
    }


    private void patchRequestFail(RequestErrorException exception){
        infoWindow.onFailedRequest(exception, button);
    }


}
*/
