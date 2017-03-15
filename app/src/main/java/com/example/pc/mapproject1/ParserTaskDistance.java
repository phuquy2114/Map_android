package com.example.pc.mapproject1;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.Duration;

public class ParserTaskDistance extends AsyncTask<String, Integer,
        List<List<HashMap<String,String>>>> {

    private GoogleMap map;
    private TextView tvShow;
    ParserTaskDistance(GoogleMap m, TextView tvShow ){
        this.map=m;
        //this.context = context;
        this.tvShow = tvShow;
    }
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String...
                                                                         jsonData) {
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try{
            jObject = new JSONObject(jsonData[0]);
            DirectionJsonParse parser = new DirectionJsonParse();
            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>>
                                         result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        //MarkerOptions markerOptions = new MarkerOptions();
        String distance = "";
        String duration = "";
        if(result.size()<1){
            return;
        }
        for(int i=0;i<result.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = result.get(i);
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);
                if(j==0){
                    distance = (String)point.get("Distance");
                    continue;
                }else if(j==1){
                    duration = (String)point.get("Duration");
                    continue;
                }
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
                //lineOptions.add(position);
            }
            lineOptions.addAll(points);
            lineOptions.width(7);
            lineOptions.color(Color.RED);
        }
        tvShow.setText("Distance: " + distance + ", Duration: " + duration);

        map.addPolyline(lineOptions);
    }
}