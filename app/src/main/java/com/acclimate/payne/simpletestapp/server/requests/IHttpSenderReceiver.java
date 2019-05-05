package com.acclimate.payne.simpletestapp.server.requests;

public interface IHttpSenderReceiver<T, U> {

    HttpRequest<T> send();
    U getResponse();

}
