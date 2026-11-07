package com.example.speakingcoach;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

public class LessonActivity extends Activity {
    private static final int SPEECH_REQUEST = 100;
    private SpeechHelper speech;
    private int lessonId;
    private int questionIndex;
    private JSONArray questions;
    private TextView questionNumber, questionText, questionTip;
    private EditText answer;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        lessonId = getIntent().getIntExtra("lesson_id", -1);
        speech = new SpeechHelper(this);
        build();
    }

    private void build() {
        LinearLayout page = Ui.page(this);
        try {
            JSONObject lesson = LessonRepository.find(this, lessonId);
            if (lesson == null) throw new IllegalArgumentException("درس پیدا نشد.");
            questions = lesson.getJSONArray("questions");
            Button back = Ui.button(this, "بازگشت به فهرست", false);
            back.setOnClickListener(v -> finish());
            Ui.add(page, back);
            Ui.gap(page, 15);
            LinearLayout hero = Ui.card(this, Ui.NAVY);
            Ui.add(hero, Ui.text(this, "LESSON " + lessonId + "  ·  " + lesson.getString("level"), 12, 0xFFB6DBFF, true));
            Ui.gap(hero, 9);
            Ui.add(hero, Ui.text(this, lesson.getString("title"), 23, Ui.WHITE, true));
            Ui.gap(hero, 7);
            Ui.add(hero, Ui.text(this, lesson.getString("goal"), 14, Ui.WHITE, false));
            Ui.add(page, hero);
            Ui.gap(page, 18);

            Ui.add(page, Ui.text(this, "۱. مکالمه نمونه", 20, Ui.INK, true));
            Ui.gap(page, 9);
            LinearLayout conversationCard = Ui.card(this, Ui.WHITE);
            String conversation = lesson.getString("conversation");
            Ui.add(conversationCard, Ui.english(this, conversation, 15, Ui.INK, false));
            Ui.gap(conversationCard, 13);
            Button playConversation = Ui.button(this, "▶ شنیدن مکالمه", false);
            playConversation.setOnClickListener(v -> speech.speak(conversation));
            Ui.add(conversationCard, playConversation);
            Ui.add(page, conversationCard);
            Ui.gap(page, 18);

            Ui.add(page, Ui.text(this, "۲. عبارت‌های پیشرفته", 20, Ui.INK, true));
            Ui.gap(page, 5);
            Ui.add(page, Ui.text(this, "روی هر عبارت بزن تا تلفظ آن را بشنوی.", 13, Ui.MUTED, false));
            Ui.gap(page, 9);
            JSONArray phrases = lesson.getJSONArray("phrases");
            for (int i = 0; i < phrases.length(); i++) {
                JSONObject p = phrases.getJSONObject(i);
                String en = p.getString("en");
                LinearLayout phrase = Ui.card(this, Ui.WHITE);
                phrase.setClickable(true);
                phrase.setOnClickListener(v -> speech.speak(en));
                Ui.add(phrase, Ui.english(this, "🔊  " + en, 17, Ui.BLUE, true));
                Ui.gap(phrase, 6);
                Ui.add(phrase, Ui.text(this, p.getString("fa"), 14, Ui.MUTED, false));
                Ui.gap(phrase, 6);
                Ui.add(phrase, Ui.english(this, p.getString("example"), 13, Ui.INK, false));
                Ui.add(page, phrase);
                Ui.gap(page, 9);
            }
            Ui.gap(page, 9);

            Ui.add(page, Ui.text(this, "۳. نوبت توست!", 20, Ui.INK, true));
            Ui.gap(page, 9);
            LinearLayout practice = Ui.card(this, Ui.WHITE);
            questionNumber = Ui.text(this, "", 12, Ui.BLUE, true);
            questionText = Ui.english(this, "", 17, Ui.NAVY, true);
            questionTip = Ui.text(this, "", 13, Ui.MUTED, false);
            Ui.add(practice, questionNumber);
            Ui.gap(practice, 8);
            Ui.add(practice, questionText);
            Ui.gap(practice, 8);
            Ui.add(practice, questionTip);
            Ui.gap(practice, 13);
            answer = new EditText(this);
            answer.setTextSize(16);
            answer.setMinLines(3);
            answer.setMaxLines(7);
            answer.setGravity(android.view.Gravity.TOP | android.view.Gravity.START);
            answer.setHint("Type or dictate your answer in English...");
            answer.setTextDirection(View.TEXT_DIRECTION_LTR);
            answer.setBackground(Ui.bg(this, Ui.BG, 12));
            answer.setPadding(Ui.dp(this, 12), Ui.dp(this, 12), Ui.dp(this, 12), Ui.dp(this, 12));
            Ui.add(practice, answer);
            Ui.gap(practice, 12);
            Button mic = Ui.button(this, "🎙 پاسخ با میکروفون", false);
            mic.setOnClickListener(v -> startSpeech());
            Ui.add(practice, mic);
            Ui.gap(practice, 9);
            Button next = Ui.button(this, "ذخیره پاسخ و سؤال بعدی  ←", true);
            next.setOnClickListener(v -> {
                saveCurrent();
                questionIndex = (questionIndex + 1) % questions.length();
                showQuestion();
                Toast.makeText(this, "پاسخ ذخیره شد", Toast.LENGTH_SHORT).show();
            });
            Ui.add(practice, next);
            Ui.gap(practice, 9);
            Button complete = Ui.button(this, "✓ تکمیل درس و بازگشت", false);
            complete.setOnClickListener(v -> completeLesson());
            Ui.add(practice, complete);
            Ui.add(page, practice);
            Ui.gap(page, 15);
            Ui.add(page, Ui.text(this, "پاسخ‌ها و پیشرفت در حافظه محلی این گوشی ثبت می‌شود. میکروفون ممکن است برای تشخیص گفتار به اینترنت احتیاج داشته باشد.", 12, Ui.MUTED, false));
            showQuestion();
        } catch (Exception e) {
            Ui.add(page, Ui.text(this, "خطا در بازکردن درس: " + e.getMessage(), 15, Ui.INK, false));
        }
    }

    private void showQuestion() {
        try {
            JSONObject q = questions.getJSONObject(questionIndex);
            questionNumber.setText("QUESTION " + (questionIndex + 1) + " / " + questions.length());
            questionText.setText(q.getString("prompt"));
            questionTip.setText("راهنما: " + q.getString("tip"));
            answer.setText(ProgressStore.getAnswer(this, lessonId, questionIndex));
        } catch (Exception e) {
            Toast.makeText(this, "سؤال قابل بارگذاری نیست", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveCurrent() {
        if (answer != null && questions != null) {
            ProgressStore.saveAnswer(this, lessonId, questionIndex, answer.getText().toString().trim());
        }
    }

    private void completeLesson() {
        saveCurrent();
        if (!ProgressStore.hasAnswer(this, lessonId, questions.length())) {
            Toast.makeText(this, "اول حداقل به یک سؤال پاسخ بده.", Toast.LENGTH_LONG).show();
            return;
        }
        ProgressStore.setCompleted(this, lessonId);
        Toast.makeText(this, "آفرین! درس تکمیل شد.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void startSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer in English");
        try {
            startActivityForResult(intent, SPEECH_REQUEST);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "سرویس تشخیص گفتار روی این گوشی پیدا نشد؛ پاسخ را تایپ کن.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty() && answer != null) {
                String previous = answer.getText().toString().trim();
                answer.setText(previous.isEmpty() ? result.get(0) : previous + " " + result.get(0));
                answer.setSelection(answer.length());
                saveCurrent();
            }
        }
    }

    @Override
    protected void onPause() {
        saveCurrent();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (speech != null) speech.shutdown();
        super.onDestroy();
    }
}
