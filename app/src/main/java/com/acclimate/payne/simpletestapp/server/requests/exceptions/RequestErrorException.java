package com.acclimate.payne.simpletestapp.server.requests.exceptions;


import com.acclimate.payne.simpletestapp.server.requests.HttpRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @NoArgsConstructor
public class RequestErrorException extends RuntimeException {

    private FailureCause failureCause;
    private Exception rootException;
    private int code;
    private SmallBodyError errorMessage;
    @Setter private HttpRequest request;

    public RequestErrorException(FailureCause failureCause,
                                 Exception conversionException,
                                 int code,
                                 SmallBodyError message){

        super(message.getMessage());

        this.failureCause = failureCause;
        this.rootException = conversionException;
        this.code = code;
        this.errorMessage = message;

    }


    public String prettyPrinter(){

        return String.format(
                "\nFailure cause : %s\n" +
                "Exception : %s\n" +
                "Error code : %s\n" +
                "Server response : %s",
                failureCause.getDescription(),
                rootException.getLocalizedMessage(),
                code,
                errorMessage.toString()
                );

    }

}
