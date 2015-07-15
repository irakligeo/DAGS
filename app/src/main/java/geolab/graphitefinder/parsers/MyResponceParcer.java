package geolab.graphitefinder.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import geolab.graphitefinder.model.GraphiteItemModel;

public class MyResponceParcer {

    public static ArrayList<GraphiteItemModel> getData(JSONArray response){
        ArrayList<GraphiteItemModel> data = new ArrayList<>();
        System.out.println(response);
        for(int i = 0; i < response.length(); i++)
        {
            try {
                JSONObject object = response.getJSONObject(i);
                GraphiteItemModel graphite = new GraphiteItemModel();
                // parse logic goes here :)
                data.add(graphite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
