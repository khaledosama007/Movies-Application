package movie.android.com.movieapp.models;

import java.io.Serializable;

/**
 * Plain class to hold review data
 */
public class Review implements Serializable{
    private String id;
    private String author ;
    private String content ;
    private String url ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



}