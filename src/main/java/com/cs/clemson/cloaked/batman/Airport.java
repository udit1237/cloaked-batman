
package com.cs.clemson.cloaked.batman;

import java.util.HashMap;

/**
 *
 * @author emmanueljohn
 */
public class Airport {
    private String iata;
    private String airport;
    private String city;
    private String state;
    private String country;
    private double latitude;
    private double longitude;
    private HashMap<String, Integer> flights = new HashMap<String, Integer>();
    
    public Airport(){
        
    }
    public Airport(String iata, String airport, double latitude, double longitude) {
        this.iata = iata;
        this.airport = airport;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public HashMap<String, Integer> getFlights() {
        return flights;
    }
    
    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getAirport() {
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public void addFlight(String iata){
        if(flights.containsKey(iata)){
            flights.put(iata, flights.get(iata)+1);
        }else{
            flights.put(iata, 1);
        }
    }
   
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + (this.iata != null ? this.iata.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Airport other = (Airport) obj;
        if ((this.iata == null) ? (other.iata != null) : !this.iata.equals(other.iata)) {
            return false;
        }
        return true;
    }
    
}
