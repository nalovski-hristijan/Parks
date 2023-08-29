package com.hnalovski.parks.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.hnalovski.parks.controller.AppController;
import com.hnalovski.parks.model.Activities;
import com.hnalovski.parks.model.EntranceFees;
import com.hnalovski.parks.model.Images;
import com.hnalovski.parks.model.OperatingHours;
import com.hnalovski.parks.model.Park;
import com.hnalovski.parks.model.StandardHours;
import com.hnalovski.parks.model.Topics;
import com.hnalovski.parks.util.Util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Repository {
    static List<Park> parkList = new ArrayList<>();
    public static void getParks(final AsyncResponse callback, String stateCode) {
        String url = Util.getParksUrl(stateCode);
        Log.d("URL", "getParks: "+url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Park park = new Park();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    park.setId(jsonObject.getString("id"));
                    park.setFullName(jsonObject.getString("fullName"));
                    park.setLatitude(jsonObject.getString("latitude"));
                    park.setLongitude(jsonObject.getString("longitude"));
                    park.setParkCode(jsonObject.getString("parkCode"));
                    park.setStates(jsonObject.getString("states"));

                    // get images along with info for them
                    JSONArray imageList = jsonObject.getJSONArray("images");
                    List<Images> list = new ArrayList<>();
                    for (int j = 0; j < imageList.length(); j++) {
                        Images images = new Images();
                        images.setCredit(imageList.getJSONObject(j).getString("credit"));
                        images.setTitle(imageList.getJSONObject(j).getString("title"));
                        images.setUrl(imageList.getJSONObject(j).getString("url"));
                        list.add(images);
                    }
                    park.setImages(list);
                    park.setWeatherInfo(jsonObject.getString("weatherInfo"));
                    park.setName(jsonObject.getString("name"));
                    park.setDesignation(jsonObject.getString("designation"));

                    //setup activities
                    JSONArray activityArray = jsonObject.getJSONArray("activities");
                    List<Activities> activitiesList = new ArrayList<>();
                    for (int j = 0; j < activityArray.length(); j++) {
                        Activities activities = new Activities();
                        activities.setName(activityArray.getJSONObject(j).getString("name"));
                        activitiesList.add(activities);
                    }
                    park.setActivities(activitiesList);

                    //Topics
                    JSONArray topicsArray = jsonObject.getJSONArray("topics");
                    List<Topics> topicsList = new ArrayList<>();
                    for (int j = 0; j <  topicsArray.length(); j++) {
                        Topics topics = new Topics();
                        topics.setName(topicsArray.getJSONObject(j).getString("name"));
                        topicsList.add(topics);
                    }
                    park.setTopics(topicsList);

                    //operating hours
                    JSONArray opHours = jsonObject.getJSONArray("operatingHours");
                    List<OperatingHours> operatingHoursList = new ArrayList<>();
                    for (int j = 0; j < opHours.length() ; j++) {
                        OperatingHours operatingHours = new OperatingHours();
                        operatingHours.setDescription(opHours.getJSONObject(j).getString("description"));
                        StandardHours standardHours= new StandardHours();
                        JSONObject hours = opHours.getJSONObject(j).getJSONObject("standardHours");

                        standardHours.setWednesday(hours.getString("wednesday"));
                        standardHours.setFriday(hours.getString("monday"));
                        standardHours.setThursday(hours.getString("thursday"));
                        standardHours.setSunday(hours.getString("sunday"));
                        standardHours.setTuesday(hours.getString("tuesday"));
                        standardHours.setFriday(hours.getString("friday"));
                        standardHours.setSaturday(hours.getString("saturday"));

                        operatingHours.setStandardHours(standardHours);

                        operatingHoursList.add(operatingHours);
                    }
                    park.setOperatingHours(operatingHoursList);

                    park.setDirectionsInfo(jsonObject.getString("directionsInfo"));

                    park.setDescription(jsonObject.getString("description"));

                    //entrance fee
                    JSONArray entranceFeesArray = jsonObject.getJSONArray("entranceFees");
                    List<EntranceFees> entranceFeesList = new ArrayList<>();
                    for (int j = 0; j < entranceFeesArray.length(); j++) {
                        EntranceFees entranceFees = new EntranceFees();
                        entranceFees.setCost(entranceFeesArray.getJSONObject(j).getString("cost"));
                        entranceFees.setDescription(entranceFeesArray.getJSONObject(j).getString("description"));
                        entranceFees.setTitle(entranceFeesArray.getJSONObject(j).getString("title"));
                        entranceFeesList.add(entranceFees);
                    }
                    park.setEntranceFees(entranceFeesList);
                    park.setWeatherInfo(jsonObject.getString("weatherInfo"));

                    parkList.add(park);
                } if (null != callback) {
                    callback.processPark(parkList);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, Throwable::printStackTrace);
        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}
