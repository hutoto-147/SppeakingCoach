package com.example.speakingcoach;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

final class Ui {
    static final int NAVY = Color.rgb(16, 43, 85);
    static final int BLUE = Color.rgb(37, 99, 235);
    static final int INK = Color.rgb(34, 48, 71);
    static final int MUTED = Color.rgb(93, 111, 133);
    static final int BG = Color.rgb(244, 247, 252);
    static final int WHITE = Color.WHITE;
    static final int PALE = Color.rgb(229, 239, 255);
    static final int GREEN = Color.rgb(14, 126, 93);

    private Ui() { }

    static int dp(Context c, int value) {
        return (int) (value * c.getResources().getDisplayMetrics().density + 0.5f);
    }

    static GradientDrawable bg(Context c, int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(c, radius));
        return d;
    }

    static TextView text(Context c, String s, int sp, int color, boolean bold) {
        TextView t = new TextView(c);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setLineSpacing(dp(c, 3), 1.08f);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    static LinearLayout column(Context c) {
        LinearLayout l = new LinearLayout(c);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    static LinearLayout page(Activity a) {
        ScrollView scroll = new ScrollView(a);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        LinearLayout page = column(a);
        page.setPadding(dp(a, 18), dp(a, 22), dp(a, 18), dp(a, 28));
        scroll.addView(page);
        a.setContentView(scroll);
        return page;
    }

    static LinearLayout card(Activity a, int background) {
        LinearLayout card = column(a);
        card.setPadding(dp(a, 18), dp(a, 18), dp(a, 18), dp(a, 18));
        card.setBackground(bg(a, background, 16));
        card.setElevation(dp(a, 2));
        return card;
    }

    static Button button(Activity a, String title, boolean primary) {
        Button b = new Button(a);
        b.setText(title);
        b.setTextAllCaps(false);
        b.setTextSize(14);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setTextColor(primary ? WHITE : NAVY);
        b.setBackground(bg(a, primary ? BLUE : PALE, 12));
        b.setPadding(dp(a, 12), dp(a, 9), dp(a, 12), dp(a, 9));
        b.setMinimumHeight(dp(a, 50));
        b.setGravity(Gravity.CENTER);
        return b;
    }

    static void add(LinearLayout parent, View child) {
        parent.addView(child, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    static void gap(LinearLayout parent, int heightDp) {
        View v = new View(parent.getContext());
        parent.addView(v, new LinearLayout.LayoutParams(1, dp(parent.getContext(), heightDp)));
    }

    static TextView english(Context c, String value, int sp, int color, boolean bold) {
        TextView t = text(c, value, sp, color, bold);
        t.setTextDirection(View.TEXT_DIRECTION_LTR);
        t.setGravity(Gravity.START);
        return t;
    }
}
