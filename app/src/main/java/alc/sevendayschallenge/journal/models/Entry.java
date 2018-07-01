package alc.sevendayschallenge.journal.models;

import java.util.Date;

public class Entry {

    private String key;
    private String title;
    private String content;
    private String imageUrl;
    private Long timestamp;

    public Entry() {
    }

    public Entry(String key, String title, String content, Long timestamp) {
        this.key = key;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
