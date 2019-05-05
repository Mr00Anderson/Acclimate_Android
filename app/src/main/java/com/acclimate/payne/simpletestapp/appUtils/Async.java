package com.acclimate.payne.simpletestapp.appUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * For information purpose only.
 *
 * Tells the programmer that this method behaves asynchronously.
 */
@Target(ElementType.METHOD)
public @interface Async {
}
