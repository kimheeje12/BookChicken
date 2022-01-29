package com.example.bookchicken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;

public class NaverApi {

    public static String main(String apiURL, String query, int start) { // 결과값을 받기 위해서 return

        String clientId = "3X4s9I8hBJKZaZljSwK3";
        String clientSecret = "cJV0U7d2iZ";

        try{
            String text = URLEncoder.encode(query, "UTF-8");
            apiURL += "query=" + text; // json 결과
            apiURL += "&start=" + start;
            apiURL += "&display=10";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream())); // 정상호출
            }else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream())); // 에러호출
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = br.readLine())!=null){
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        }catch(Exception e){
            return e.toString();
        }
    }
}
