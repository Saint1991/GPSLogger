package geologger.saints.com.geologger.foursquare;

import org.androidannotations.annotations.EBean;

/**
 * Created by Mizuno on 2015/01/26.
 */
@EBean
public class FourSquareLocation {

    private String address;
    private double lat;
    private double lng;
    private int distance;
    private String cc;
    private String city;
    private String state;
    private String country;
    private String[] formattedAddress;

    public FourSquareLocation() {

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
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

    public String[] getFormattedAddress() {
        return formattedAddress;
    }

    public void setFomattedAddress(String[] fomattedAddress) {
        this.formattedAddress = fomattedAddress;
    }

}
