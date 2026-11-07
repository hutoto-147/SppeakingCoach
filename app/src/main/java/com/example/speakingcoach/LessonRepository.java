package com.example.speakingcoach;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class LessonRepository {
    private static JSONArray all;
    private LessonRepository() { }

    static synchronized JSONArray all(Context context) throws IOException, JSONException {
        if (all != null) return all;
        try (InputStream input = context.getAssets().open("lessons.json");
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = input.read(buffer)) != -1) output.write(buffer, 0, n);
            JSONObject data = new JSONObject(new String(output.toByteArray(), StandardCharsets.UTF_8));
            all = data.getJSONArray("lessons");
            return all;
        }
    }

    static JSONObject find(Context context, int lessonId) throws IOException, JSONException {
        JSONArray arr = all(context);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject lesson = arr.getJSONObject(i);
            if (lesson.getInt("id") == lessonId) return lesson;
        }
        return null;
    }
}
