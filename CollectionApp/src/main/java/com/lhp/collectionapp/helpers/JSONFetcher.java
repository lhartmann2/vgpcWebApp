package com.lhp.collectionapp.helpers;

import com.lhp.collectionapp.entity.SearchResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONFetcher {

    final static String TOKEN = "e7220b8d16dae8967678256005837e4f6ea5ae54";

    public static Map<String, String> getGameInfoById(int gameId) {
        String urlStr = "https://www.pricecharting.com/api/product?t="+TOKEN+"&id="+gameId;
        return getGameInfo(urlStr);
    }

    public static Map<String, String> getGameInfoByName(String gameName) {
        String urlStr = "https://www.pricecharting.com/api/product?t="+TOKEN+"&q="+gameName;
        return getGameInfo(urlStr);
    }

    public static List<SearchResult> searchGamesByName(String gameName) {
        String urlStr = "https://www.pricecharting.com/api/products?t="+TOKEN+"&q="+gameName;
        return searchResults(urlStr);
    }

    private static Map<String, String> getGameInfo(String urlStr) {
        Map<String, String> map = new HashMap<>();
        try {
            URL url = new URL(urlStr);
            URLConnection request = url.openConnection();
            String JSON = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine();

            JSONObject obj = new JSONObject(JSON);

            map.put("status", obj.getString("status"));
            map.put("gameId", obj.getString("id"));
            map.put("product-name", obj.getString("product-name"));
            map.put("console-name", obj.getString("console-name"));
            map.put("releaseDate", obj.getString("release-date"));
            map.put("newPrice", String.valueOf(obj.getInt("new-price")));
            map.put("cibPrice", String.valueOf(obj.getInt("cib-price")));
            map.put("loosePrice", String.valueOf(obj.getInt("loose-price")));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return map;
    }

    private static List<SearchResult> searchResults(String urlStr) {
        List<SearchResult> results = new ArrayList<>();
        try {
            URL url = new URL(urlStr);
            URLConnection request = url.openConnection();
            String JSON = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine();

            JSONObject mainObj = new JSONObject(JSON);
            JSONArray products = mainObj.getJSONArray("products");
            if(!products.isEmpty()) {
                int index = 0;
                do {
                    JSONObject obj = products.getJSONObject(index);
                    String gameId = obj.getString("id");
                    String gameName = obj.getString("product-name");
                    String console = obj.getString("console-name");
                    String releaseDate = obj.getString("release-date");
                    results.add(new SearchResult(Integer.parseInt(gameId), gameName, console, releaseDate));
                    index++;
                } while(true);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        } catch(JSONException ex) {
            //End of array
        }
        return results;
    }
}
