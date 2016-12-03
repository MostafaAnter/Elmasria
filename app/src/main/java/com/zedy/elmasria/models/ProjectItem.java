package com.zedy.elmasria.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mostafa_anter on 12/2/16.
 */
public class ProjectItem implements Parcelable {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String  title;
    private String timeStamp;
    private String content;
    private String imageUrl;


    private String area;
    private String coordinators;
    private String deliver;

    public ProjectItem(String id, String title, String timeStamp, String content, String imageUrl, String area, String coordinators, String deliver) {
        this.id = id;
        this.title = title;
        this.timeStamp = timeStamp;
        this.content = content;
        this.imageUrl = imageUrl;
        this.area = area;
        this.coordinators = coordinators;
        this.deliver = deliver;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCoordinators() {
        return coordinators;
    }

    public void setCoordinators(String coordinators) {
        this.coordinators = coordinators;
    }

    public String getDeliver() {
        return deliver;
    }

    public void setDeliver(String deliver) {
        this.deliver = deliver;
    }

    protected ProjectItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        timeStamp = in.readString();
        content = in.readString();
        imageUrl = in.readString();
        area = in.readString();
        coordinators = in.readString();
        deliver = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(timeStamp);
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(area);
        dest.writeString(coordinators);
        dest.writeString(deliver);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProjectItem> CREATOR = new Parcelable.Creator<ProjectItem>() {
        @Override
        public ProjectItem createFromParcel(Parcel in) {
            return new ProjectItem(in);
        }

        @Override
        public ProjectItem[] newArray(int size) {
            return new ProjectItem[size];
        }
    };
}