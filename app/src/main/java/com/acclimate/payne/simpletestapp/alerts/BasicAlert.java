package com.acclimate.payne.simpletestapp.alerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BasicAlert implements Serializable {

    // protected int         autoId;              // Serveur
    protected String      id;                     // Serveur
    protected String      nom;                    // UserRequest
    protected String      source;                 // UserRequest
    protected String      territoire;             // UserRequest
    protected String      dateDeMiseAJour;        // App
    protected String      description;            // UserRequest
    protected String      type;                   // UserRequest
    protected String      certitude;              // App : auto
    protected String      severite;               //
    protected String      urgence;                //
    protected String      sousCategorie;          // UserRequest
    protected String      count;                  // App : 0
    protected double      lng;
    protected double      lat;
    protected Geometry    geometry;               // App

    @JsonIgnore
    protected AlertTypes  enumType;

    public BasicAlert() { }

    @Override @JsonIgnore
    public String toString(){

        try {

            return (new ObjectMapper()).writeValueAsString(this);

        } catch (JsonProcessingException jpe){

//            Log.e("Error to String","Error while printing BasicAlert infos", jpe);
            String result = "";

            result += "nom : " + this.nom + "\n";
            result += "position : lat = " + this.getLat() + " - longitude = " + this.getLng();
            result += "type : " + this.type;

            return result;

        }

    }

    @JsonIgnore
    public void initType(){

        if (type != null && enumType == null){

            switch (type) {

                case "Eau":
                    enumType = AlertTypes.EAU;
                    break;
                case "Feu":
                    enumType = AlertTypes.FEU;
                    break;
                case "Terrain":
                    enumType = AlertTypes.TERRAIN;
                    break;
                case "Météo": case "Meteo":
                default:
                    enumType = AlertTypes.METEO;
                    break;

            }

        }

    }


    @Override
    public int hashCode() {
        if ( id != null && !id.equals("")) {
            return id.hashCode();
        } else {

            return (nom + source + territoire).hashCode();

        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BasicAlert other = (BasicAlert) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;

    }

}
