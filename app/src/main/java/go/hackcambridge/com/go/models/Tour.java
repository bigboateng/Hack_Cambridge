package go.hackcambridge.com.go.models;

/**
 * Created by boatengyeboah on 28/01/2017.
 */

public class Tour {
    private double longi;
    private double lati;
    private String audioUrl;
    private long createdAt;
    private int rating;
    private int numOfRating;
    private String author;
    private String audioUrlSpa;
    private String audioUrlEng;
    private String audioUrlFre;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAudioUrlSpa() {
        return audioUrlSpa;
    }

    public void setAudioUrlSpa(String audioUrlSpa) {
        this.audioUrlSpa = audioUrlSpa;
    }

    public String getAudioUrlEng() {
        return audioUrlEng;
    }

    public void setAudioUrlEng(String audioUrlEng) {
        this.audioUrlEng = audioUrlEng;
    }

    public String getAudioUrlFre() {
        return audioUrlFre;
    }

    public void setAudioUrlFre(String audioUrlFre) {
        this.audioUrlFre = audioUrlFre;
    }

    public Tour(double longi, double lati, long createdAt) {
        this.longi = longi;
        this.lati = lati;
        this.createdAt = createdAt;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(Double lati) {
        this.lati = lati;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumOfRating() {
        return numOfRating;
    }

    public void setNumOfRating(int numOfRating) {
        this.numOfRating = numOfRating;
    }
}
