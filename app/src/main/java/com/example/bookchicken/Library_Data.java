package com.example.bookchicken;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Library_Data implements Serializable {

    private String libraryname; // 도서관명
    private String codevalue; // 구명
    private String fdrm_close_date; // 정기 휴관일
    private String tel_no; // 전화번호

    public String getLibraryname(){return libraryname;}

    public String getcodevalue(){return codevalue;}

    public String getfdrm_close_date(){return fdrm_close_date;}

    public String gettel_no(){return tel_no;}

    public void setLibraryname(String libraryname) {this.libraryname = libraryname;}

    public void setcodevalue(String codevalue) {this.codevalue = codevalue;}

    public void setfdrm_close_date(String fdrm_close_date) {this.fdrm_close_date = fdrm_close_date;}

    public void settel_no(String tel_no) {this.tel_no = tel_no;}

    public String toString(){
        return "Library_Data {libraryname= " + libraryname + ", " +
                "codevalue= " + codevalue + ", " +
                "fdrm_close_date= " + fdrm_close_date + ", " +
                "tel_no= " + tel_no + "}";
    }

}
