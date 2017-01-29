package go.hackcambridge.com.go.models;

/**
 * Created by boatengyeboah on 29/01/2017.
 */

public class Hotel {
    private double longi;
    private double lati;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }
}
