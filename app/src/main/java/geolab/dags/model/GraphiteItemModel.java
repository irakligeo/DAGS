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
    private int likesCount;
    private double longitude;
    private double latitude;

    private String hashtag;

    private int markerID;

    public GraphiteItemModel(String title, String description, String imgURL, String author, String createDate, double longitude, double latitude, int likesCount, String hashtag, int markerId) {
        this.title = title;
        this.description = description;
        this.imgURL = imgURL;
        this.author = author;
        this.createDate = createDate;
        this.longitude = longitude;
        this.latitude = latitude;
        this.likesCount = likesCount;
        this.hashtag = hashtag;
        this.markerID = markerId;
    }



    public GraphiteItemModel(){}

    public GraphiteItemModel(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.imgURL = in.readString();
        this.author = in.readString();
        this.createDate = in.readString();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.likesCount = in.readInt();
        this.hashtag = in.readString();
        this.markerID = in.readInt();
    }


    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

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

    public String getHashtag() {
        return hashtag;
    }

    public int getMarkerID() {
        return this.markerID;
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
        parcel.writeString(this.title);
        parcel.writeString(this.description);
        parcel.writeString(this.imgURL);
        parcel.writeString(this.author);
        parcel.writeString(this.createDate);
        parcel.writeDouble(this.longitude);
        parcel.writeDouble(this.latitude);
        parcel.writeInt(this.likesCount);
        parcel.writeString(this.hashtag);
        parcel.writeInt(this.markerID);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GraphiteItemModel createFromParcel(Parcel in) {
            return new GraphiteItemModel(in);
        }

        public GraphiteItemModel[] newArray(int size) {
            return new GraphiteItemModel[size];
        }
    };
}
