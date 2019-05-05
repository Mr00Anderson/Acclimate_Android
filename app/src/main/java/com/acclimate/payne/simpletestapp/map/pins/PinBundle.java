package com.acclimate.payne.simpletestapp.map.pins;

import android.util.Pair;

import java.util.List;

public class PinBundle<T extends Pin> extends Pair<List<T>, List<T>> {

    PinBundle(List<T> toAdd, List<T> toRemove){
        super(toAdd, toRemove);
    }

    public List<T> toAdd(){
        return first;
    }

    public List<T> toRemove(){
        return second;
    }

}
