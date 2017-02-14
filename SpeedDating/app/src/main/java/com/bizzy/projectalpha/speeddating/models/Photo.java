package com.bizzy.projectalpha.speeddating.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by usama on 8/27/15.
 */
//@ParseClassName("Photo")
/*public class Photo extends ParseObject {
    public ParseUser getPhotographer() {
        return getParseUser("photographer");
    }

    public void setPhotographer(ParseUser photographer) {
        put("photographer", photographer);
    }

    public ParseFile getPhoto() {
        return getParseFile("photo");
    }

    public void setPhoto(ParseFile photo) {
        put("photo", photo);
    }
}*/



public class Photo {
    String album, imageUrl, id,author;

    public String getAlbum() {
        return album;
    }
    public String getAuthor() {
        return author;
    }

    public User getCurrentAuthor(){
        return (User)ParseUser.getCurrentUser();
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "album='" + album + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}