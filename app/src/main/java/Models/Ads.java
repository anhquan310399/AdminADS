package Models;

import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Ads implements Serializable {
    private String _id;
    private String title;
    private String content;
    private String body;
    private String urlImage;
    private String expires;

    public Ads(String _id, String title, String content, String body, String urlImage, String expires) {
        this._id = _id;
        this.title = title;
        this.content = content;
        this.body = body;
        this.urlImage = urlImage;
        this.expires = expires;
    }

    public Ads() {
        this._id = "";
        this.title = "";
        this.content = "";
        this.body = "";
        this.urlImage = "";
        this.expires = "";
    }

    @Override
    public String toString() {
        return new GsonBuilder().create().toJson(this, Ads.class);
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
