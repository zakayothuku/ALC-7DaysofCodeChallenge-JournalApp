package alc.sevendayschallenge.journal.models;

import com.google.firebase.database.PropertyName;

import java.util.Date;

public class Entry {

    private String key;
    private String title;
    private String content;
    @PropertyName("added_at")
    private Date addedAt;
    @PropertyName("updated_at")
    private Date updatedAt;

    public Entry() {
    }

    public Entry(String key, String title, String content, Date addedAt, Date updatedAt) {
        this.key = key;
        this.title = title;
        this.content = content;
        this.addedAt = addedAt;
        this.updatedAt = updatedAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
