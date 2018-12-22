package com.example.z7381.facetoface.Admin;

import com.baidu.aip.face.AipFace;

import org.json.JSONObject;

import java.util.HashMap;



public class Delete {
    public static boolean Delete(AipFace client,String userId,String groupId){

        HashMap<String, String> options = new HashMap<String, String>();
        JSONObject res=client.deleteUser(groupId,userId,options);
        System.out.println(userId);
        System.out.println(res);

        if (res.optString("error_code").equals("0")==true){
            return true;
        }else
            return false;
        }
}
