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
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Milan
 */
@Path("/locations")
public class LocationsResourceContainer {

    @Context
    private UriInfo context;
    
    private final String connectionString;

    /**
     * Creates a new instance of LocationsResourceContainer
     */
    public LocationsResourceContainer() {
        connectionString = DBConfig.BASE_CONNECTION_STRING + 
                AppListener.getDatabaseDirectory() + 
                DBConfig.DATABASE_NAME;
    }

    /**
     * Retrieves representation of an instance of hr.foi.mtlab.rest.services.LocationsResourceContainer
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json; charset=UTF-8")
    public String getJson() {
        List<Adresa> adrese = dajSveAdrese();
        JSONArray jsonArray = new JSONArray();
        
        int i=0;
        for(Adresa a: adrese) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", a.getIdadresa());
                jsonObject.put("location", a.getAdresa());
                jsonObject.put("lat", a.getGeoloc().getLatitude());
                jsonObject.put("lon", a.getGeoloc().getLongitude());
                jsonArray.put(i++, jsonObject);
            } catch (JSONException ex) {
                Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        return jsonArray.toString();
    }

    /**
     * POST method for creating an instance of LocationResource
     * @param location
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Produces("application/json; charset=UTF-8")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String postJson(@FormParam("location") String location) {
        try {
            JSONObject resultObject = new JSONObject();
            GMKlijent gmClient = new GMKlijent();
            Lokacija geoLocation = gmClient.getGeoLocation(location);
            Adresa adresa = new Adresa();
            adresa.setAdresa(location);
            adresa.setGeoloc(geoLocation);
            int id = saveLocation(adresa);
            if (id > 0) {
                resultObject.put("id", id);
                resultObject.put("location", location);
                resultObject.put("lat", geoLocation.getLatitude());
                resultObject.put("lon", geoLocation.getLongitude());
                return resultObject.toString();
            } else return "{}";
        } catch (Exception ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return "{}";
        }
    }

    /**
     * Sub-resource locator method for {id}
     * @param id
     * @return 
     */
    @Path("{id}")
    public LocationResource getLocationResource(@PathParam("id") String id) {
        return LocationResource.getInstance(id);
    }
    
     private List<Adresa> dajSveAdrese() {
        List<Adresa> adrese = new ArrayList<>();
        String query = "select * from adrese";

        try {
            Class.forName(DBConfig.DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return adrese;
        }

        try (Connection connection = DriverManager.getConnection(connectionString, DBConfig.USER, DBConfig.PASS);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String adresa = resultSet.getString(2); //new String(resultSet.getBytes(2), "UTF-8");
                String lat = resultSet.getString(3);
                String lng = resultSet.getString(4);
                adrese.add(new Adresa(id, adresa, new Lokacija(lat, lng)));
            }

        } catch (SQLException ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return adrese;
        }
        return adrese;
    }
     
    private int saveLocation(Adresa adresa) {
        String insertCommand = "insert into adrese(adresa, latitude, longitude) "
                + "values (?, ?, ?)";
        try {
            Class.forName(DBConfig.DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        
        
        try (Connection connection = DriverManager.getConnection(connectionString, DBConfig.USER, DBConfig.PASS);
                PreparedStatement statement = connection.prepareStatement(insertCommand)) 
        {
            statement.setString(1, adresa.getAdresa());
            statement.setString(2, adresa.getGeoloc().getLatitude());
            statement.setString(3, adresa.getGeoloc().getLongitude());
            statement.executeUpdate();
            return statement.getGeneratedKeys().getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(LocationsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
     
     
}
