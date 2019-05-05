package com.acclimate.payne.simpletestapp.server.requests;

import org.springframework.http.HttpAuthentication;

//TODO : test this shit
/**
 * Represents HTTP Bearer Authentication. Intended for use
 * with {@link org.springframework.http.client.ClientHttpRequest}
 * and {@link org.springframework.web.client.RestTemplate}.
 * @see <a href="http://www.ietf.org/rfc/rfc2617.txt">RFC2617</a>
 * @author Olivier L. Applin
 */
public class HttpBearerAuthentification extends HttpAuthentication {

    private String token;

    public HttpBearerAuthentification(String token){
        this.token = token;
    }

    /**
     * @return the value for the 'Authorization' HTTP header.
     */
    public String getHeaderValue() {
        return "Authorization " + token;
    }

    @Override
    public String toString() {
        return String.format("Authorization: %s", getHeaderValue());
    }

}
