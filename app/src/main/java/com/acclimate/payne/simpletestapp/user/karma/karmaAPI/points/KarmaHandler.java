package com.acclimate.payne.simpletestapp.user.karma.karmaAPI.points;

import com.acclimate.payne.simpletestapp.user.karma.UserNotFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class KarmaHandler<T extends PointsProvider> implements InvocationHandler {

    private T karmaInterface;

    public KarmaHandler(T karmaInterface){
        this.karmaInterface = karmaInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Annotation[] annotations = method.getAnnotations();
        KarmaRequired karmaAnnotation = method.getAnnotation(KarmaRequired.class);
        // If methods has no annotation or if it has no KarmaRequired annotation or if it is the right method
        if (annotations.length == 0 || karmaAnnotation == null) {
            return method.invoke(karmaInterface, args);
        }

        int points;
        try {
            points = karmaInterface.getPoints();
        } catch (UserNotFoundException unfe){
            karmaInterface.userNotFound();
            return proxy;
        }

        if (points >= karmaAnnotation.value()){
            return method.invoke(karmaInterface, args);
        } else {  // TODO : what if arguments are different ??? (for on success method and on fail method ?)

            // find method with KarmaFail annotation with attributes 'method' that has value method.getName()
            for (Class interf : karmaInterface.getClass().getInterfaces())
                for (Method m : interf.getMethods()){
                    if (m.getAnnotation(KarmaFail.class) != null
                            && method.getName().equals(m.getAnnotation(KarmaFail.class).method())){
                        return m.invoke(karmaInterface, args);
                    }
                }
        }

        return proxy;
    }

}
