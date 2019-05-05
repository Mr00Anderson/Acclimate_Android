package com.acclimate.payne.simpletestapp.server.requests;

import com.acclimate.payne.simpletestapp.alerts.BasicAlert;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.appUtils.AppTag;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.ErrorMessage;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.FailureCause;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.RequestErrorException;
import com.acclimate.payne.simpletestapp.server.requests.exceptions.SmallBodyError;
import com.acclimate.payne.simpletestapp.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import static com.acclimate.payne.simpletestapp.appUtils.AppTag.AUTH_HEADERS;
import static com.acclimate.payne.simpletestapp.server.Server.BASE_SERVER_ADDRESS;

/**
 * Abstract representation of a General HttpRequest to the
 * <a href=https://acclimate-api.herokuapp.com target="_blank">Acclimate RESTFUL API</a>.
 *
 * To create an instance of a Httprequest, one must use a public factory methods declared
 * in any of the abstract classes representing the request method
 * ({@link GetRequest}, {@link PostRequest}, {@link PatchRequest}, {@link DeleteRequest}).
 * The method called will return an instance of a HttpRequest configured correctly and ready to send
 * the request to the server using the {@link HttpRequest#send()} method.
 *
 * Basic usage example :
 *
 * {@code
 *      HttpRequest<User> requestForUserById = GetRequest.user("the_requested_userId");
 *      User fetchedUser = requestForUserById.send().getResponse();}
 *
 * Although the previous example would work, the HttpRequest should be wrapped around and used in
 * conjunction with a {@link RequestHandler}
 * for using different callback depending on the success (or failure) of the request.
 * The previous example does not take asynchronous operation in consideration. Note that asynchronous
 * concern are not taken care of by none of the request produced by the factory methods,
 * neither by any instance of this class. These concerns are instead dealt with using the
 * {@link RequestHandler} class.
 * <p></p>
 * The response from the server is directly abstracted into an corresponding compatible Java Object.
 * The App/Server infrastructure currently supports four response type :
 * {@link User}, {@link com.acclimate.payne.simpletestapp.monitoredZones.MonitoredZone}, {@link BasicAlert} and {@link UserAlert}.
 * Although it is possible to retreive the response as a String using
 * {@link HttpRequest#getResponseAsString()}, JACKSON Object Mapping features are preferred.
 *
 *
 * @param <T> The type of the answer from the server (a String will be converted to that type using
 *           Jackson JSON Object Marshalling).
 *
 * @author Applin
 */
public abstract class HttpRequest<T> implements IHttpSenderReceiver<T, T> {

    /**
     * The base server api url to be queried. It is already shaped to be ready to use with
     * {@link String#format(String, Object...)} to pass in url parameters.
     */
    public static final String SERVER_API = BASE_SERVER_ADDRESS + "%s/";
    private static final ObjectMapper mapper = new ObjectMapper(); // create once, reuse


    @Getter
    protected RestTemplate restTemplate = new RestTemplate();
    protected HttpHeaders headers = new HttpHeaders();
    @Getter
    protected HttpEntity<?> requestEntity;
    protected RequestCategory category;
    protected HttpMethod httpMethod;

    protected String jwtToken;
    protected boolean requiresAuth;
    protected ResponseEntity<String> responseEntity;
    protected String strResponse;

    private SmallBodyError errorResponse;
    @Setter
    private RequestErrorException requestException;

    protected T body;
    protected List<String> uids;

    @Getter  private boolean was_sent = false;


    /**
     * The actual url that will be used to send the request to the server.
     */
    @Getter @Setter
    protected String url;

    protected abstract String setUrl();

    /**
     * Main method used to gain access to the response sent back to the Server. The response will be
     * converted (marshalled ?) to the specific typed parameter T using Jackson JSON/POJO conversion
     * framework.
     *
     * @return an instance of the class T, which is the response from the server.
     */
    @SuppressWarnings("all")
    public abstract T getResponse();

    /**
     * Base constructor for all SpringRequest.
     *
     * @param category the category of request for the SpringServer. Will change th url.
     * @param httpMethod
     */
    HttpRequest(RequestCategory category, HttpMethod httpMethod) {
        this.category = category;
        this.httpMethod = httpMethod;
        HeadersConfiguration.setDafultConfig(headers);
    }

    /**
     * Open communication with server and send the request using the {@link HttpEntity} provided by
     * the <a href= http://projects.spring.io/spring-android/>Spring for Android</a>
     * framework. The {@link HttpEntity} contains headers information and (if specified and required)
     * the body (payload) of the request.<p></p>
     *
     * In case of an error, some work is done to understand the source of the problem, and that
     * information is wrapped within the {@link RequestErrorException} that is thrown by the method.
     *
     * @return the request instance,
     *      used primarily to chain calls to {@link HttpRequest#getResponse(Class)}
     *      or {@link HttpRequest#getResponseAsString()}
     */
    public HttpRequest<T> send() throws RequestErrorException {

        url = setUrl();

        if (requiresAuth) {
            headers.set(AppTag.AUTH_HEADERS, jwtToken);
        }

        if (body == null) {
//            Log.i(TEST_AUTH, "reset entity headers = " + headers.toString());
            if (uids == null) {
                requestEntity = new HttpEntity<>(headers);
            } else {
//                Log.i(SERVER, "adding uids!");
                requestEntity = new HttpEntity<>(uids, headers);

            }
        } else {
            requestEntity = new HttpEntity<>(body, headers);

        }

//        Log.i(TEST_AUTH, "url : "+ url + " --- " + "headers = " + headers.toString());

        try {

//            Log.e(SERVER, "has body ? = " + requestEntity.hasBody());
//            Log.e(SERVER, "requestEntity = " + requestEntity.toString());
//            Log.e(SERVER, "url = " + url);
            responseEntity = restTemplate.exchange(
                    url,
                    httpMethod,
                    requestEntity,
                    String.class);

            strResponse = responseEntity.getBody();


        } catch (HttpServerErrorException | HttpClientErrorException e) {

            int status_code = e.getStatusCode().value();
//            Log.e(SERVER, "SERVER ERROR = ", e);

            try {

                errorResponse = mapper.readValue(strResponse, SmallBodyError[].class)[0];

                requestException=  new RequestErrorException(
                        FailureCause.SERVER_EXCEPTION,
                        e, status_code, errorResponse);
                requestException.setRequest(this);
                throw requestException;

            } catch (IOException | NullPointerException ex) {

                // Could not marshal the response to error response
                // this means something very bad and unexpected happend

                try {

                    // try big body message
                    ErrorMessage errormsg = mapper.readValue(strResponse, ErrorMessage[].class)[0];

                    SmallBodyError msg = new SmallBodyError();
                    msg.setMessage(errormsg.getMessage());

                    requestException = new RequestErrorException(
                        FailureCause.SERVER_EXCEPTION,
                        e, msg.getStatus(), msg);
                    requestException.setRequest(this);
                    throw requestException;

                } catch (Exception exe)  {

//                    Log.e(SERVER, "FATAL ERROR on HttpException", exe);
                    // Log.e(SERVER, "string response = "+ strResponse);
                    requestException = new RequestErrorException(
                            FailureCause.UNKNOWN,
                            ex, e.getStatusCode().value(),
                            new SmallBodyError()
                            .timestamp((int)Calendar.getInstance().getTimeInMillis())
                            .path(url)
                            .status(e.getStatusCode().value())
                            .message(FailureCause.UNKNOWN.getDescription()));
                    requestException.setRequest(this);
                    throw requestException;

                }


            } catch (Exception finalExe) {

//                Log.e(SERVER,"FATAL ERROR", finalExe);
                // Log.e(SERVER, "string response = "+ strResponse);
                requestException = new RequestErrorException(
                        FailureCause.UNKNOWN,
                        finalExe, e.getStatusCode().value(),
                        new SmallBodyError()
                        .timestamp((int)Calendar.getInstance().getTimeInMillis())
                        .path(url)
                        .message(FailureCause.UNKNOWN.getDescription()));
                requestException.setRequest(this);
                throw requestException;

            }

        } catch (Exception finalExe){

//            Log.e("FATAL ERROR :(", finalExe.getLocalizedMessage());

            // the world has exploded
            SmallBodyError error = new SmallBodyError()
                    .timestamp((int)Calendar.getInstance().getTimeInMillis())
                    .path(url)
                    .message(FailureCause.UNKNOWN.getDescription());

            requestException = new RequestErrorException(
                    FailureCause.UNKNOWN,
                    finalExe, -1, error);
            requestException.setRequest(this);
            throw requestException;

        }

        return this;

    }

    /**
     * Allow to retreive the response of the Request and handles error.
     *
     * @param clazz the Class to which the reponse should be converted.
     * @return the response as an instance of Class<T>.
     */
    @SuppressWarnings("unchecked")
    protected T getResponse(Class<T> clazz) {

//        Log.i(TEST_AUTH, "get response headers = " + headers.toString());

        if (strResponse == null) {

            SmallBodyError error = new SmallBodyError()
                    .timestamp((int)Calendar.getInstance().getTimeInMillis())
                    .path(url)
                    .message(FailureCause.NO_CONNECTION.getDescription());
        }

        try {

            return mapper.readValue(strResponse, clazz);

        } catch (Exception e /*JsonParseException | JsonMappingException e*/) { // IOException, JsonParseException, JsonMappingException

            // le serveur n'a pas lancé d'exception, mais on a pas pu convertir le message

            SmallBodyError smallBodyError;
            FailureCause cause;
            Exception tmp;
            try {
                smallBodyError =  mapper.readValue(strResponse, SmallBodyError.class);
                cause = FailureCause.JSON_CONVERSION;
                tmp = e;
            } catch (Exception finalExe){
                smallBodyError =  new SmallBodyError();
                cause = FailureCause.UNKNOWN;
                tmp = finalExe;

            }

            requestException = new RequestErrorException(cause, tmp, -1, smallBodyError);
            requestException.setRequest(this);
            throw requestException;

        }

    }


    /**
     * Used to fetch the response from the server as a String once the request has been completed.
     *
     * @return the response, in String format, from the server.
     */
    public String getResponseAsString(){
        if (strResponse == null){
            throw new RuntimeException("SpringRequest has not been sent.");
        }

        return strResponse;
    }

    public HttpRequest<T> setAuth(String jwt){
        headers.set(AUTH_HEADERS, jwt);
        return this;
    }


    /**
     * Check if current request has raise an error, either from server error or
     * from marshaling the response. The methods makes no distinction between the different
     * possible causes of the error
     *
     * @return true if it's considered to have encountered an error.
     */
    public boolean hasError(){
        return requestException != null;
    }


    public void addRequiredToken(String jwtToken){
        this.jwtToken = jwtToken;
        this.requiresAuth = true;
    }

}
