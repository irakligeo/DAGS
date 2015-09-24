package geolab.dags.parsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import geolab.dags.fragment.ViewPagerFragment;
import geolab.dags.model.GraphiteItemModel;

public class MyResponseParser {

    public static int oldStatusCode = 0, statusCode = 1;


    //function
    public static ArrayList<GraphiteItemModel> getData(JSONArray response){
        ArrayList<GraphiteItemModel> data = new ArrayList<>();
        System.out.println(response);
        String res = "";
//      statusCode = response.getJSONObject(0).getInt("android");
        try {
            for (int i = 1; i <= response.length(); i++) {

                int marker_id = response.getJSONObject(i).getInt("marker_id");
                String imgUrl = response.getJSONObject(i).getString("pic_url");
                String imgTitle = response.getJSONObject(i).getString("title");
                String imgDescription = response.getJSONObject(i).getString("description");
                String imgAuthor = response.getJSONObject(i).getString("u_name");
                String imgUploadDate = response.getJSONObject(i).getString("create_date");
                int likesCount = response.getJSONObject(i).getInt("likes");
                double longitude = response.getJSONObject(i).getDouble("longitude");
                double latitude = response.getJSONObject(i).getDouble("latitude");
                String hashtag = response.getJSONObject(i).getString("hashtag");

                String currentDateTime = new Date().toString();
                res = formatUploadPostTime(imgUploadDate);


                GraphiteItemModel graphite = new GraphiteItemModel(imgTitle, imgDescription, imgUrl, imgAuthor, res, longitude, latitude,likesCount,hashtag, marker_id);
                data.add(graphite);
            }
        }catch (JSONException e) {
                e.printStackTrace();
        }
        //reverse ArrayList for displaying current uploaded post
        Collections.reverse(data);
        return data;
    }


    public static String formatUploadPostTime(String uploadDate){
        String dateStart = uploadDate;
        String res = "";
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        String currDate = format.format(new Date());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(currDate);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            System.out.print(diffDays + " days, ");
            System.out.print(diffHours + " hours, ");
            System.out.print(diffMinutes + " minutes, ");
            System.out.print(diffSeconds + " seconds.");

            if(diffDays > 0){
                res = diffDays+" days ago";
                return res;
            }else if(diffHours > 0){
                res = diffHours + " hours ago";
                return res;
            }else if (diffMinutes > 0) {
                res = diffMinutes + " minutes ago";
                return res;
            }else if(diffSeconds > 0 ) {
                res = diffSeconds + " seconds ago";
                return res;
            }else {
                return uploadDate;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

}
