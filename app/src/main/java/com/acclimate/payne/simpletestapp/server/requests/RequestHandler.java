package com.acclimate.payne.simpletestapp.server.requests;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.acclimate.payne.simpletestapp.server.Server;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.FailureCause;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.SmallBodyError;
import com.acclimate.payne.simpletestapp.user.UserController;

import java.util.Calendar;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


/**
 * Class used in conjunction with a {@link HttpRequest} to handle asynchronous
 * operations involving network connection.
 *
 *
 * @param <T>
 */
public class RequestHandler<T> extends AsyncTask<HttpRequest<T>, Void, T> {

    @Getter
    private HttpRequest<T> request;
    private HttpResponseCallback<T> callback;
    private HttpErrorCallback httpErrorCallback;
    private RequestErrorException exception;

    @Getter
    private boolean mCompleted;

    @Setter
    private String hardJWT;

    /**
     *
     * @param request
     * @param callback
     * @param httpErrorCallback
     */
    public RequestHandler(@NonNull HttpRequest<T> request, @NonNull HttpResponseCallback<T> callback,
                          @Nullable HttpErrorCallback httpErrorCallback){
        this.callback = callback;
        this.httpErrorCallback = httpErrorCallback;
        this.request = request;
    }


    /**
     *
     */
    @SuppressWarnings("unchecked")
    public void handle(Server.AuthorizationHeaders requiresAuth){

        switch (requiresAuth) {

            case REQUIRED:
                UserController.getUserJWT(task -> {
                    if (task.isSuccessful()) {
//                        Log.i(TEST_AUTH, "token is found");
//                        Log.i(TEST_AUTH, "token  = " + task.getResult().getToken());
                        request.addRequiredToken(task.getResult().getToken());
                        execute(request);
                    } else {
//                        Log.e(TEST_AUTH, "token is not found");
                        this.exception = new RequestErrorException(
                            FailureCause.AUTH, task.getException(), 403,
                            new SmallBodyError()
                                .timestamp((int) Calendar.getInstance()
                                .getTimeInMillis()).path(request.url)
                                .message(FailureCause.AUTH.getDescription()));
                        mCompleted = false;
                    }
                });
            break;

            case FORCED:
                if (hardJWT != null){
                    request.addRequiredToken(hardJWT);
                    execute(request);
                }
                break;

            case NONE: default:
                execute(request);
            break;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    final protected T doInBackground(HttpRequest<T>... requests) {

        HttpRequest<T> request = requests[0];

        try {

            T response = request.send().getResponse();
            mCompleted = true;
            return response;

        } catch (RequestErrorException requestException){

            mCompleted = false;
            this.exception = requestException;
            return null;

        } catch (Exception e) {
//            Log.e("FATAL ERROR", e.getMessage());
            return null;
        }

    }



    @Override
    protected void onPostExecute(T response) {
        if (mCompleted) {
            callback.run(response);
        } else {
            if (httpErrorCallback != null) {
                httpErrorCallback.run(exception);
            }
        }
    }


    /**
     *
     */
    @FunctionalInterface
    public interface HttpErrorCallback {
        void run(RequestErrorException exception);
    }

    /**
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface HttpResponseCallback<T> {
        void run(T response);
    }


}
