package movie.android.com.movieapp.models;

import java.io.Serializable;

/**
 * plain class to hold Trailer Information
 */
public class Trailer implements Serializable {
    private String id ;
    private String key ;
    private String name ;
    private String site;
    private int size ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
