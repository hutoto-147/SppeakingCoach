package com.example.speakingcoach;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;
import java.util.Locale;

final class SpeechHelper {
    private final Context context;
    private TextToSpeech tts;
    private boolean ready;

    SpeechHelper(Context context) {
        this.context = context;
        tts = new TextToSpeech(context, status -> {
            if (status == TextToSpeech.SUCCESS && tts != null) {
                int result = tts.setLanguage(Locale.US);
                ready = result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED;
            }
        });
    }

    void speak(String english) {
        if (!ready || tts == null) {
            Toast.makeText(context, "موتور تلفظ انگلیسی آماده نیست؛ زبان انگلیسی TTS را در تنظیمات گوشی بررسی کن.", Toast.LENGTH_LONG).show();
            return;
        }
        tts.speak(english, TextToSpeech.QUEUE_FLUSH, null, "speak_" + System.nanoTime());
    }

    void shutdown() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        ready = false;
    }
}
