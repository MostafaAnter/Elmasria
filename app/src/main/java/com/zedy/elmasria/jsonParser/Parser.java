package com.zedy.elmasria.jsonParser;

import com.zedy.elmasria.models.NewsItem;
import com.zedy.elmasria.utils.Constants;
import com.zedy.elmasria.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mostafa_anter on 12/3/16.
 */

public class Parser {
    public static List<NewsItem> parseNews(String feed){
        JSONObject rootObject = null;
        try {
            rootObject = new JSONObject(feed);
            String error = rootObject.optString("error");
            if (error.equalsIgnoreCase("true")){
                return null;
            }
            JSONObject newsObject = rootObject.optJSONObject("news");
            JSONArray dataArray = newsObject.optJSONArray("data");
            List<NewsItem> itemList = new ArrayList<>();
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject itemObject = dataArray.optJSONObject(i);

                String title = itemObject.optString("title");
                String timeStamp = Utils.manipulateDateFormat(itemObject.optString("updated_at"));
                String content = itemObject.optString("content");
                String imageUrl = Constants.baseUrl + itemObject.optString("img");

                itemList.add(new NewsItem(title, timeStamp, content, imageUrl));


            }

            return itemList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
