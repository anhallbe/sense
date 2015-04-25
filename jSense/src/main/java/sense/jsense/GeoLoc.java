/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sense.jsense;

/**
 *
 * @author andreas
 */
public class GeoLoc {
    private double lat;
    private double lon;
    
    public GeoLoc(String loc) {
        String[] latlon = loc.split(",");
        this.lat = Double.parseDouble(latlon[0]);
        this.lon = Double.parseDouble(latlon[1]);
    }
    
    public GeoLoc(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
    
    public static double distance(GeoLoc from, GeoLoc to) {
        return from.distanceTo(to);
    }
    
    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        GeoLoc loc1 = new GeoLoc(lat1, lon1);
        GeoLoc loc2 = new GeoLoc(lat2, lon2);
        return GeoLoc.distance(loc1, loc2);
    }
    
    public static double distance(String latLong1, String latLong2) {
        GeoLoc loc1 = new GeoLoc(latLong1);
        GeoLoc loc2 = new GeoLoc(latLong2);
        return GeoLoc.distance(loc1, loc2);
    }
    
    public double distanceTo(GeoLoc other) {
        double lat1 = lat;
        double lat2 = other.getLat();
        
        double lon1 = lon;
        double lon2 = other.getLon();
        
        /**
         Calculate distance using the Haversine method.
         * http://stackoverflow.com/a/16794680/2279621
         **/
        
        final int R = 6371; // Radius of the earth

        Double latDistance = deg2rad(lat2 - lat1);
        Double lonDistance = deg2rad(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0.0;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        return Math.sqrt(distance);
    }
    
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return lat + "," + lon;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GeoLoc) {
            if(((GeoLoc)obj).getLat() == this.getLat() && ((GeoLoc)obj).getLon() == this.getLon())
                return true;
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        return hash;
    }
}
