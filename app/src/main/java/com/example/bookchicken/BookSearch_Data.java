package com.example.bookchicken;

import java.io.Serializable;

public class BookSearch_Data implements Serializable{

    private String bookimage; //이미지
    private String booktitle;//책 제목
    private String author; //작가
    private String publisher; // 출판사
    private String bookprice; // 가격
    private String booklink; // 링크

    public String getBookImage(){ return bookimage; }

    public String getBooktitle(){
        return booktitle;
    }

    public String getAuthor(){
        return author;
    }

    public String getPublisher(){ return publisher; }

    public String getBookprice(){return bookprice;}

    public String getBooklink() {return booklink;}

    public void setBookImage(String bookimage){
        this.bookimage=bookimage;
    }

    public void setBooktitle(String booktitle){
        this.booktitle = booktitle;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setPublisher(String publisher){
        this.publisher = publisher;
    }

    public void setBookprice(String bookprice) {this.bookprice = bookprice;}

    public void setBooklink(String booklink) {this.booklink = booklink;}

    @Override
    public String toString(){
        return "BookSearch_Data {image= " + bookimage + ", " +
                "title= " + booktitle + ", " +
                "author= " + author + ", " +
                "publisher= " + publisher + ", " +
                "bookprice= " + bookprice +
                "}";
    }
}
