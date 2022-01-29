package com.example.bookchicken;

import android.net.Uri;
import android.widget.RatingBar;

public class ReadingRecord_Data {
    private String bookimage;
    private String booktitle;
    private String author;
    private float ratingBar;
    private String bookrecord;

    public ReadingRecord_Data(String bookimage, String booktitle, String author, float ratingBar, String bookrecord){
        this.bookimage = bookimage;
        this.booktitle = booktitle;
        this.author = author;
        this.ratingBar = ratingBar;
        this.bookrecord = bookrecord;
    }

    public String getBookimage(){return bookimage;}

    public String getBooktitle(){return booktitle;}

    public String getAuthor(){return author;}

    public float getRatingBar(){return ratingBar;}

    public String getBookrecord(){return bookrecord;}

}
