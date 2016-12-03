package com.zedy.elmasria.models;

/**
 * Created by mostafa_anter on 12/2/16.
 */

public class NewsItem {
    private String  title,
            timeStamp,
            content,
            imageUrl;

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
}
