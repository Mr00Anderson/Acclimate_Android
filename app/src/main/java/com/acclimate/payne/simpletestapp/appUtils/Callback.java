package com.acclimate.payne.simpletestapp.appUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * For information purpose only.
 *
 * Tells the programmer that this method is intended to be used as a callback
 * for Asynchronous operation
 */
@Target(ElementType.METHOD)
public @interface Callback {
    String method();
}
