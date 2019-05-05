package com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points;

import com.acclimate.payne.simpletestapp.appUtils.App;
import com.acclimate.payne.simpletestapp.user.User;
import com.acclimate.payne.simpletestapp.user.karma.UserNotFoundException;

public interface PointsProvider {

    default int getPoints(){
        User user = App.getInstance().getCurrentUser();
        if (user != null){
            return App.getInstance().getCurrentUser().getPoints();
        } else {
            throw new UserNotFoundException("User is not defined");
        }
    }

    void userNotFound();

}
