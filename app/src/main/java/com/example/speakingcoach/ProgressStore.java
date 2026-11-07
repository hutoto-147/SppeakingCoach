package com.example.speakingcoach;

import android.content.Context;
import android.content.SharedPreferences;

final class ProgressStore {
    private static final String PREFS = "speaking_coach_progress_v1";
    private ProgressStore() { }
    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
    static boolean completed(Context c, int id) {
        return prefs(c).getBoolean("completed_" + id, false);
    }
    static void setCompleted(Context c, int id) {
        prefs(c).edit().putBoolean("completed_" + id, true).apply();
    }
    static String getAnswer(Context c, int lessonId, int questionIndex) {
        return prefs(c).getString("answer_" + lessonId + "_" + questionIndex, "");
    }
    static void saveAnswer(Context c, int lessonId, int questionIndex, String text) {
        prefs(c).edit().putString("answer_" + lessonId + "_" + questionIndex, text).apply();
    }
    static boolean hasAnswer(Context c, int lessonId, int numQuestions) {
        for (int i = 0; i < numQuestions; i++) {
            if (!getAnswer(c, lessonId, i).trim().isEmpty()) return true;
        }
        return false;
    }
}
