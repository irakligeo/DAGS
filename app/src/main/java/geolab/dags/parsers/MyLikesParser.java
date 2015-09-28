package geolab.dags.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import geolab.dags.model.UserLikes;


public class MyLikesParser {
    public static ArrayList<UserLikes> parseLikesData(JSONArray array){
        ArrayList<UserLikes> likesArrayList = new ArrayList<>();
        UserLikes userLikes;
        for (int i = 0; i < array.length(); ++i){
            try {
                JSONObject jsonObject = array.getJSONObject(i);

                String id = jsonObject.getString("id");
                String markerId = jsonObject.getString("marker_id");
                String userId = jsonObject.getString("user_id");

                userLikes = new UserLikes(id,userId,markerId);
                likesArrayList.add(userLikes);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return likesArrayList;
    }
}
