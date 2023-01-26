package com.example.thewisemansapp.util;

import com.example.thewisemansapp.model.Advice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpUtils {

    private static Executor executor = Executors.newSingleThreadExecutor();

    public static JSONObject sendGetRequest(String url) throws JSONException {
        StringBuilder result = new StringBuilder();
        try {
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) new URL(url)
                        .openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader isw = new InputStreamReader(in);
                int data = isw.read();
                while (data != -1) {
                    result.append((char) data);
                    data = isw.read();
                }
                return jsonify(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonify(result.toString());
    }

    private static JSONObject jsonify(String string) throws JSONException {
        return new JSONObject(string);
    }

    public static void getRandomAdvice(HttpCallback<Advice> callback) {
        executor.execute(() -> {
            try {
                JSONObject adviceObject = sendGetRequest("https://api.adviceslip.com/advice");
                Advice advice = new Advice();
                advice.setAdvice(adviceObject.getJSONObject("slip").getString("advice"));
                advice.setId(adviceObject.getJSONObject("slip").getInt("id"));
                callback.onResponse(advice);
            } catch (JSONException e) {
                callback.onError(e);
            }
        });
    }

    public static void searchAdvice(String searchParam, HttpCallback<List<Advice>> callback){
        executor.execute(() -> {
            try {
                JSONObject adviceObject = sendGetRequest("https://api.adviceslip.com/advice/search/" + searchParam);
                JSONArray slips = adviceObject.getJSONArray("slips");
                List<Advice> advices = new ArrayList<>();
                for (int i = 0; i < slips.length(); i++) {
                    JSONObject slip = slips.getJSONObject(i);
                    Advice advice = new Advice();
                    advice.setAdvice(slip.getString("advice"));
                    advice.setId(slip.getInt("id"));
                    advices.add(advice);
                }
                callback.onResponse(advices);
            } catch (JSONException e) {
                callback.onError(e);
            }
        });
    }
}
