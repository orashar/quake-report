package com.example.android.quakereport;

import android.renderscript.ScriptGroup;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {


    private QueryUtils() {
    }



    public static List<Earthquake> fetchEarthquakeData(String requestURl) {
        URL url = createUrl(requestURl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        }catch(IOException e) {
            Log.e("HttpRequestError", "problem making the http request", e);
        }
        List<Earthquake> earthquakes = extractFeatureFromJson(jsonResponse);
        return earthquakes;
    }




    public static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {
        if(TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        List<Earthquake> earthquakes = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            for(int i = 0; i < earthquakeArray.length(); i++) {
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                Double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                Long time = properties.getLong("time");
                String url = properties.getString("url");

                Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                earthquakes.add(earthquake);
            }

        }catch(JSONException e) {
            Log.e("QueryUtils", "PROBLEM PARSING THE EARTHQUAKE JSON RESPONSE", e);
        }

        return earthquakes;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        }catch(MalformedURLException e) {
            Log.e("URLFormationError", "Problem building the url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e("HttpResponseCodeNotOK", "Error Response code" + urlConnection.getResponseCode());
            }

        }catch(IOException e) {
            Log.e("makeHttpRequest", "problem retrieving the earthquake JSON result.", e);
        }finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream !=null) {
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

}
