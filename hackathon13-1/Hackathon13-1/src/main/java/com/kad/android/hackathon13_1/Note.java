package com.kad.android.hackathon13_1;

/**
 * Created by kAd on 24/08/13.
 */
public class Note {
    public int id;
    public String title, content, image, modified, created;

    public Note(int id, String title, String content, String image, String modified, String created) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
        this.modified = modified;
        this.created = created;
    }
}
