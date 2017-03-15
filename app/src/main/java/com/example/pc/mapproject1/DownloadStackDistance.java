package com.example.pc.mapproject1;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Lớp DownloadStackDistance sẽ thực hiện quá trình download dữ liệu về từ
 * Web Service thông qua phương thức downloadUrl. Sau khi tiến trình
 * download thành công sẽ gọi lớp ParserTaskDistance để hiển thị ra chuỗi định
 * dạng JSON đã phân tích thông qua lớp DirectionJsonParse. Ở đây ta sẽ hiển
 * thị khoảng cách và thời giữa 2 địa điểm đã đánh dấu.
 */

public class DownloadStackDistance extends AsyncTask<String,Void,String>
{
    private GoogleMap m;
    private TextView tv;
    DownloadStackDistance(GoogleMap map, TextView tv){
        this.m=map;
        this.tv = tv;

    }
    @Override
    protected String doInBackground(String... params) {
        String data = "";
        try{
            data = downloadUrl(params[0]);
        }catch(Exception e){
        }
        return data;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ParserTaskDistance parserTask1 = new ParserTaskDistance(m, tv);
        parserTask1.execute(result);
    }
    private String downloadUrl(String strUrl) throws IOException {
        System.out.println(strUrl);
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
