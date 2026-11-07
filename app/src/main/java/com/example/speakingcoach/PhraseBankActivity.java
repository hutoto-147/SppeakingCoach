package com.example.speakingcoach;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Locale;

public class PhraseBankActivity extends Activity {
    private SpeechHelper speech;
    private LinearLayout results;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        speech = new SpeechHelper(this);
        LinearLayout page = Ui.page(this);
        android.widget.Button back = Ui.button(this, "بازگشت به صفحه اصلی", false);
        back.setOnClickListener(v -> finish());
        Ui.add(page, back);
        Ui.gap(page, 16);
        Ui.add(page, Ui.text(this, "بانک عبارت‌های B2–C1", 24, Ui.NAVY, true));
        Ui.gap(page, 6);
        Ui.add(page, Ui.text(this, "عبارت‌ها، معنی فارسی و مثال‌ها؛ برای شنیدن تلفظ روی هر کارت بزن.", 13, Ui.MUTED, false));
        Ui.gap(page, 13);
        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextDirection(android.view.View.TEXT_DIRECTION_LTR);
        search.setTextSize(16);
        search.setHint("Search a phrase...");
        search.setPadding(Ui.dp(this, 12), Ui.dp(this, 10), Ui.dp(this, 12), Ui.dp(this, 10));
        search.setBackground(Ui.bg(this, Ui.WHITE, 12));
        Ui.add(page, search);
        Ui.gap(page, 14);
        results = Ui.column(this);
        Ui.add(page, results);
        showResults("");
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int st, int before, int count) {
                showResults(s.toString());
            }
            @Override public void afterTextChanged(Editable e) { }
        });
    }

    private void showResults(String query) {
        results.removeAllViews();
        int found = 0;
        String normalized = query.toLowerCase(Locale.ROOT).trim();
        try {
            JSONArray lessons = LessonRepository.all(this);
            for (int i = 0; i < lessons.length(); i++) {
                JSONObject lesson = lessons.getJSONObject(i);
                JSONArray phrases = lesson.getJSONArray("phrases");
                for (int j = 0; j < phrases.length(); j++) {
                    JSONObject p = phrases.getJSONObject(j);
                    String en = p.getString("en");
                    String fa = p.getString("fa");
                    String example = p.getString("example");
                    String combined = (en + " " + fa + " " + example).toLowerCase(Locale.ROOT);
                    if (!combined.contains(normalized)) continue;
                    found++;
                    LinearLayout card = Ui.card(this, Ui.WHITE);
                    Ui.add(card, Ui.english(this, "🔊  " + en, 17, Ui.BLUE, true));
                    Ui.gap(card, 5);
                    Ui.add(card, Ui.text(this, fa, 14, Ui.MUTED, false));
                    Ui.gap(card, 5);
                    Ui.add(card, Ui.english(this, example, 13, Ui.INK, false));
                    Ui.gap(card, 5);
                    Ui.add(card, Ui.text(this, "Lesson " + lesson.getInt("id"), 11, Ui.MUTED, false));
                    card.setClickable(true);
                    card.setOnClickListener(v -> speech.speak(en));
                    Ui.add(results, card);
                    Ui.gap(results, 10);
                }
            }
            if (found == 0) Ui.add(results, Ui.text(this, "عبارتی با این جست‌وجو پیدا نشد.", 14, Ui.MUTED, false));
        } catch (Exception e) {
            Ui.add(results, Ui.text(this, "خطا در بارگذاری عبارت‌ها", 14, Ui.INK, false));
        }
    }

    @Override
    protected void onDestroy() {
        if (speech != null) speech.shutdown();
        super.onDestroy();
    }
}
