package com.zedy.elmasria.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mostafa_anter on 12/2/16.
 */
public class NewsItem implements Parcelable {
    private String  title;
    private String timeStamp;
    private String content;
    private String imageUrl;

    public NewsItem(String title, String timeStamp, String content, String imageUrl) {
        this.title = title;
        this.timeStamp = timeStamp;
        this.content = content;
        this.imageUrl = imageUrl;
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

    protected NewsItem(Parcel in) {
        title = in.readString();
        timeStamp = in.readString();
        content = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(timeStamp);
        dest.writeString(content);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
