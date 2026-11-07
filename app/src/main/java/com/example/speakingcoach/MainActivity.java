package com.example.speakingcoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.res.ColorStateList;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
    }

    @Override
    protected void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        LinearLayout page = Ui.page(this);
        LinearLayout hero = Ui.card(this, Ui.NAVY);
        Ui.add(hero, Ui.text(this, "Speaking Coach", 26, Ui.WHITE, true));
        Ui.gap(hero, 8);
        Ui.add(hero, Ui.text(this, "مربی شخصی مکالمه انگلیسی | تمرین آفلاین", 14, 0xFFCEE0FC, false));
        Ui.gap(hero, 12);

        try {
            JSONArray lessons = LessonRepository.all(this);
            int completed = 0;
            for (int i = 0; i < lessons.length(); i++) {
                if (ProgressStore.completed(this, lessons.getJSONObject(i).getInt("id"))) completed++;
            }
            Ui.add(hero, Ui.text(this, completed + " از " + lessons.length() + " درس تکمیل شده", 15, Ui.WHITE, true));
            Ui.gap(hero, 9);
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(Math.max(1, lessons.length()));
            bar.setProgress(completed);
            bar.setProgressTintList(ColorStateList.valueOf(0xFF70E1C1));
            bar.setProgressBackgroundTintList(ColorStateList.valueOf(0xFF476589));
            Ui.add(hero, bar);
            Ui.add(page, hero);
            Ui.gap(page, 16);

            android.widget.Button bank = Ui.button(this, "بانک عبارت‌های B2–C1  ←", false);
            bank.setOnClickListener(v -> startActivity(new Intent(this, PhraseBankActivity.class)));
            Ui.add(page, bank);
            Ui.gap(page, 20);
            Ui.add(page, Ui.text(this, "مسیر یادگیری", 22, Ui.INK, true));
            Ui.gap(page, 4);
            Ui.add(page, Ui.text(this, "برای شروع، روی یکی از درس‌ها بزن. پاسخ‌ها روی همین گوشی ذخیره می‌شوند.", 13, Ui.MUTED, false));
            Ui.gap(page, 13);

            for (int i = 0; i < lessons.length(); i++) {
                JSONObject lesson = lessons.getJSONObject(i);
                final int lessonId = lesson.getInt("id");
                boolean done = ProgressStore.completed(this, lessonId);
                LinearLayout card = Ui.card(this, Ui.WHITE);
                card.setClickable(true);
                card.setFocusable(true);
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LessonActivity.class);
                    intent.putExtra("lesson_id", lessonId);
                    startActivity(intent);
                });
                String mark = done ? "✓  " : "○  ";
                TextView title = Ui.text(this, mark + lesson.getString("title"), 18, done ? Ui.GREEN : Ui.NAVY, true);
                Ui.add(card, title);
                Ui.gap(card, 5);
                Ui.add(card, Ui.text(this, "Lesson " + lessonId + " · " + lesson.getString("level"), 12, Ui.BLUE, true));
                Ui.gap(card, 4);
                Ui.add(card, Ui.text(this, lesson.getString("subtitle"), 13, Ui.MUTED, false));
                Ui.add(page, card);
                Ui.gap(page, 11);
            }
            Ui.add(page, Ui.text(this, "تلفظ با موتور صوتی گوشی اجرا می‌شود؛ میکروفون به سرویس تشخیص گفتار نصب‌شده وابسته است.", 12, Ui.MUTED, false));
        } catch (Exception e) {
            Ui.add(page, Ui.text(this, "خطا در بارگذاری درس‌ها: " + e.getMessage(), 15, Ui.INK, false));
        }
    }
}
