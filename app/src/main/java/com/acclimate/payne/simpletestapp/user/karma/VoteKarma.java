package com.acclimate.payne.simpletestapp.user.karma;


import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points.KarmaFail;
import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points.KarmaRequired;
import com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points.PointsProvider;

import java.util.Map;

import static com.acclimate.payne.simpletestapp.user.karma.KarmaRequirementsValues.PLUS_ONE;

public interface VoteKarma extends PointsProvider {

    @KarmaRequired(PLUS_ONE)
    void patchAlertScore(Map<String, Object> body, String userId);

    @KarmaFail(method = "patchAlertScore")
    void notEnoughKarmaToVote(Map<String, Object> body, String userId);

}
