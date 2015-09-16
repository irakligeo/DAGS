package geolab.dags.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class GraphiteItemModel implements Serializable, Parcelable {
    private String title;
    private String description;
    private String imgURL;
    private String author;
    private String createDate;
    private double longitude;
    private double latitude;
    private int id;

    public GraphiteItemModel(String title, String description, String imgURL, String author, String createDate, double longitude, double latitude) {
        this.title = title;
        this.description = description;
        this.imgURL = imgURL;
        this.author = author;
        this.createDate = createDate;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public GraphiteItemModel(){}
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "GraphiteItemModel{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", author='" + author + '\'' +
                ", createDate='" + createDate + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
