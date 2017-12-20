package com.jstech.tom;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by SONY on 2017-12-20.
 */

public class RequestHttpUrlConnection {

    /*
    *
    *   통신 전용 클래스.
    *   POST 형식 사용
    *   데이터 포맷은 json으로 통일.
    *   -> strParam 은 파라미터로 json 형식의 데이터 포맷사용.
    *
    * */
    public String request(String strUrl, String strParam)
    {
        //  HttpURLConnection 참조 변수
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection)url.openConnection();

            //urlConnection 설정
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept-Charset", "UTF-8");
            urlConnection.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            //Parameter 전달 및 데이터 읽기
            String strParams = strParam;
            if(strParams == null)
            {
                strParams = "";
            }

            OutputStream outputstream = urlConnection.getOutputStream();
            outputstream.write(strParams.getBytes("UTF-8"));    //출력 스트림에 입력
            outputstream.flush();
            outputstream.close();

            //연결 확인 및 실패 시 종료.
            if(urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                Log.e("Connection", "Failed");
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

            String strLine;
            String strPage = "";

            while((strLine = br.readLine()) != null)
            {
                strPage += strLine;
            }

            //  결과물 리턴
            return strPage;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }

        return null;
    }


}
