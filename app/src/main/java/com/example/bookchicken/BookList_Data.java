package com.example.bookchicken;

import java.io.Serializable;
import java.io.StringReader;

public class BookList_Data implements Serializable {

    private String booktitle;
    private String author;
    private String publisher;
    private String bookpage;
    private String bookimage;

    public BookList_Data(String bookimage, String booktitle, String author, String publisher, String bookpage){
        this.bookimage = bookimage;
        this.booktitle = booktitle;
        this.author = author;
        this.publisher = publisher;
        this.bookpage = bookpage;
    }

    public String getBookimage(){return bookimage;}

    public String getBooktitle(){return booktitle;}

    public String getAuthor(){return author;}

    public String getPublisher(){return publisher;}

    public String getBookpage(){return bookpage;}

}
