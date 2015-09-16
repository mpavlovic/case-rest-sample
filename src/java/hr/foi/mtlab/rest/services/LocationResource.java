/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.foi.mtlab.rest.services;

import hr.foi.mtlab.data.Adresa;
import hr.foi.mtlab.data.DBConfig;
import hr.foi.mtlab.data.Lokacija;
import hr.foi.mtlab.listeners.AppListener;
import hr.foi.mtlab.rest.clients.GMKlijent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Milan
 */
public class LocationResource {

    private String id;
    private String connectionString;

    /**
     * Creates a new instance of LocationResource
     */
    private LocationResource(String id) {
        this.id = id;
        this.connectionString = DBConfig.BASE_CONNECTION_STRING + 
                AppListener.getDatabaseDirectory() + 
                DBConfig.DATABASE_NAME;
    }

    /**
     * Get instance of the LocationResource
     */
    public static LocationResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of LocationResource class.
        return new LocationResource(id);
    }

    /**
     * Retrieves representation of an instance of hr.foi.mtlab.rest.services.LocationResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of LocationResource
     * @param location
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Produces("application/json; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String putJson(@FormParam("location") String location) {
        try {
            JSONObject resultObject = new JSONObject();
            GMKlijent gmClient = new GMKlijent();
            Lokacija geoLocation = gmClient.getGeoLocation(location);
            Adresa adresa = new Adresa();
            adresa.setAdresa(location);
            adresa.setGeoloc(geoLocation);
            if (updateLocation(adresa)) {
                resultObject.put("id", id);
                resultObject.put("location", location);
                resultObject.put("lat", geoLocation.getLatitude());
                resultObject.put("lon", geoLocation.getLongitude());
                return resultObject.toString();
            }
            else return "{}";
        } catch (Exception ex) {
            Logger.getLogger(LocationResource.class.getName()).log(Level.SEVERE, null, ex);
            return "{}";
        }
    }

    /**
     * DELETE method for resource LocationResource
     */
    @DELETE
    public void delete() {
    }
    
    private boolean updateLocation(Adresa adresa) {
        String updateCommand = "update adrese set adresa = ?, latitude = ?, longitude = ? where id = ?";
        
        try {
            Class.forName(DBConfig.DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationResource.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        try (Connection connection = DriverManager.getConnection(connectionString, DBConfig.USER, DBConfig.PASS);
                PreparedStatement statement = connection.prepareStatement(updateCommand))
        {
            statement.setString(1, adresa.getAdresa());
            statement.setString(2, adresa.getGeoloc().getLatitude());
            statement.setString(3, adresa.getGeoloc().getLongitude());
            statement.setInt(4, Integer.parseInt(id));
            return statement.executeUpdate() == 1;
            
        } catch (SQLException ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
