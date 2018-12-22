package com.example.z7381.facetoface.Admin;

import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ReferFace {
    public static String  ReferFace(String pic, AipFace client) {
        HashMap<String, String> options = new HashMap<>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
        options.put("max_user_num", "3");

        String groupId = "ruan2";
        String imageType = "BASE64";
        String face_token=null;
        Double score=0.0;
        JSONObject jsonObject=null;

        JSONObject res=client.search(pic,imageType,groupId,options);
        System.out.println(res);
        String error_code = res.optString("error_code");
        System.out.println(error_code);
        if (error_code.equals("0") == true) {
            JSONArray TEMP = res.optJSONObject("result").optJSONArray("user_list");

            try {
                 jsonObject = TEMP.getJSONObject(0);
                String Tscore = jsonObject.getString("score");
                System.out.println(Tscore);
                score = Double.parseDouble(Tscore);
                } catch (JSONException e) {
                e.printStackTrace();
            }

            if (score>=75){
             face_token = res.optJSONObject("result").optString("face_token");
            }

        }
        return face_token;
    }
}