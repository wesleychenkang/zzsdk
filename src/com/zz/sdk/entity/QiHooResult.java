package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class QiHooResult implements JsonParseInterface{
    public String codes;
    public String id;
    public String name;
    public String nick;
    public String qiHoo_Name;
	@Override
	public JSONObject buildJson(){
		
		return null;
	}
	@Override
	public void parseJson(JSONObject json) {
		try {
		codes = json.isNull("codes") ? null : json.getJSONArray("codes").getString(0);
		id = json.isNull("id")?null:json.getString("id");
		name = json.isNull("name")?null:json.getString("name");
		nick = json.isNull("nick")?null:json.getString("nick");
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	@Override
	public String getShortName() {
		return null;
	}
	
	public String[] getQiHooMessage(){
		
		return new String[]{};
	}
   public String toString(){
	   
	   return "codes"+codes+"id"+id+"name"+name+"nick"+nick;
   }
}
