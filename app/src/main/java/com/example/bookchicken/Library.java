package com.example.bookchicken;

import com.google.gson.annotations.SerializedName;

public class Library {

    @SerializedName("LBRRY_SEQ_NO")
    private String LBRRY_SEQ_NO;
    @SerializedName("LBRRY_NAME")
    private String LBRRY_NAME;
    @SerializedName("GU_CODE")
    private String GU_CODE;
    @SerializedName("CODE_VALUE")
    private String CODE_VALUE;
    @SerializedName("ADRES")
    private String ADRES;
    @SerializedName("FDRM_CLOSE_DATE")
    private String FDRM_CLOSE_DATE;
    @SerializedName("TEL_NO")
    private String TEL_NO;
    @SerializedName("XCNTS")
    private String XCNTS;
    @SerializedName("YDNTS")
    private String YDNTS;


    public String getLBRRY_SEQ_NO() {
        return LBRRY_SEQ_NO;
    }

    public void setLBRRY_SEQ_NO(String LBRRY_SEQ_NO){
        this.LBRRY_SEQ_NO = LBRRY_SEQ_NO;
    }

    public String getLBRRY_NAME() {
        return LBRRY_NAME;
    }

    public void setLBRRY_NAME(String LBRRY_NAME){
        this.LBRRY_NAME = LBRRY_NAME;
    }

    public String getGU_CODE() {
        return GU_CODE;
    }

    public void setGU_CODE(String GU_CODE){
        this.GU_CODE = GU_CODE;
    }

    public String getCODE_VALUE() {
        return CODE_VALUE;
    }

    public void setCODE_VALUE(String CODE_VALUE){
        this.CODE_VALUE = CODE_VALUE;
    }

    public String getADRES() {
        return ADRES;
    }

    public void setADRES(String ADRES){
        this.ADRES = ADRES;
    }

    public String getFDRM_CLOSE_DATE() {
        return FDRM_CLOSE_DATE;
    }

    public void setFDRM_CLOSE_DATE(String FDRM_CLOSE_DATE){
        this.FDRM_CLOSE_DATE = FDRM_CLOSE_DATE;
    }

    public String getTEL_NO() {
        return TEL_NO;
    }

    public void setTEL_NO(String TEL_NO){
        this.TEL_NO = TEL_NO;
    }

    public String getXCNTS() {
        return XCNTS;
    }

    public void setXCNTS(String XCNTS){
        this.XCNTS = XCNTS;
    }

    public String getYDNTS() {
        return YDNTS;
    }

    public void setYDNTS(String YDNTS){
        this.YDNTS = YDNTS;
    }
}
