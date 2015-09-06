package geolab.graphitefinder.model;

/**
 * Created by geolabedu on 05.09.15.
 */
public class Coords {
    private double longitude;
    private double latitude;
    private String title;
    private String imgURL;


    public Coords(double longitude, double latitude, String title, String imgURL) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.title = title;
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
