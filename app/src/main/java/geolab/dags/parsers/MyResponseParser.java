package geolab.dags.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import geolab.dags.fragment.ViewPagerFragment;
import geolab.dags.model.GraphiteItemModel;

public class MyResponseParser {

    public static int oldStatusCode = 0, statusCode = 1;


    //function
    public static ArrayList<GraphiteItemModel> getData(JSONArray response){
        ArrayList<GraphiteItemModel> data = new ArrayList<>();
        System.out.println(response);

//      statusCode = response.getJSONObject(0).getInt("android");
        try {
            for (int i = 1; i <= response.length(); i++) {

                String imgUrl = response.getJSONObject(i).getString("pic_url");
                String imgTitle = response.getJSONObject(i).getString("title");
                String imgDescription = response.getJSONObject(i).getString("description");
                String imgAuthor = response.getJSONObject(i).getString("user_id");
                String imgUploadDate = response.getJSONObject(i).getString("create_date");
                double longitude = response.getJSONObject(i).getDouble("longitude");
                double latitude = response.getJSONObject(i).getDouble("latitude");

                GraphiteItemModel graphite = new GraphiteItemModel(imgTitle, imgDescription, imgUrl, imgAuthor, imgUploadDate, longitude, latitude);
                data.add(graphite);
            }
        }catch (JSONException e) {
                e.printStackTrace();
        }
        //reverse ArrayList for displaying current uploaded post
        Collections.reverse(data);
        return data;
    }
}
