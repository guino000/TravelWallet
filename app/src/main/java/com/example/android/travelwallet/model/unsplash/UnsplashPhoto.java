package com.example.android.travelwallet.model.unsplash;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnsplashPhoto {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("urls")
    @Expose
    private Urls urls;
    @SerializedName("links")
    @Expose
    private PhotoLinks links;
    @SerializedName("user")
    @Expose
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public PhotoLinks getLinks() {
        return links;
    }

    public void setLinks(PhotoLinks links) {
        this.links = links;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}