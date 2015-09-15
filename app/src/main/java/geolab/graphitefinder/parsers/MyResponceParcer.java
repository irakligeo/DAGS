package geolab.graphitefinder.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import geolab.graphitefinder.model.GraphiteItemModel;

public class MyResponceParcer {

    public static ArrayList<GraphiteItemModel> getData(JSONArray response){
        ArrayList<GraphiteItemModel> data = new ArrayList<>();
        System.out.println(response);
        for(int i = 0; i < response.length(); i++)
        {
            try {
                JSONObject object = response.getJSONObject(i);

                String imgUrl = object.getString("pic_url");
                String imgTitle = object.getString("title");
                String imgDescription = object.getString("description");
                String imgAuthor = object.getString("user_id");
                String imgUploadDate = object.getString("create_date");
                double longitude = object.getDouble("longitude");
                double latitude = object.getDouble("latitude");

                GraphiteItemModel graphite = new GraphiteItemModel(imgTitle,imgDescription,imgUrl,imgAuthor,imgUploadDate,longitude,latitude);
                data.add(graphite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                //reverse ArrayList for displaying current uploaded post
                Collections.reverse(data);
            }
        }
        return data;
    }
}
