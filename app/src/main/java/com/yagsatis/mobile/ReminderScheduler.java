package com.yagsatis.mobile;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ReminderScheduler {
    public static final String CHANNEL_ID = "due_reminders";
    private static final String PREFS = "due_reminder_prefs";
    private static final String KEY_JSON = "reminders_json";
    private static final String KEY_ALARMS = "alarm_keys";

    private ReminderScheduler() {}

    public static void ensureChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                NotificationChannel ch = new NotificationChannel(
                        CHANNEL_ID,
                        "Vade Hatırlatmaları",
                        NotificationManager.IMPORTANCE_HIGH
                );
                ch.setDescription("Vadeli satışların yaklaşan ve gelen vade tarihleri");
                nm.createNotificationChannel(ch);
            }
        }
    }

    public static synchronized void sync(Context context, String remindersJson) {
        ensureChannel(context);
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        cancelKnown(context, prefs.getStringSet(KEY_ALARMS, new HashSet<>()));
        Set<String> keys = new HashSet<>();

        try {
            JSONArray arr = new JSONArray(remindersJson);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject r = arr.getJSONObject(i);
                String id = r.optString("id", "");
                String receipt = r.optString("receiptNo", "Vadeli satış");
                String customer = r.optString("customerName", "Müşteri");
                double remaining = r.optDouble("remaining", 0);
                String dueDate = r.optString("dueDate", "");
                if (id.isEmpty() || dueDate.isEmpty() || remaining <= 0) continue;

                Date parsed = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dueDate);
                if (parsed == null) continue;

                Calendar due = Calendar.getInstance();
                due.setTime(parsed);
                due.set(Calendar.HOUR_OF_DAY, 10);
                due.set(Calendar.MINUTE, 0);
                due.set(Calendar.SECOND, 0);
                due.set(Calendar.MILLISECOND, 0);

                Calendar before = (Calendar) due.clone();
                before.add(Calendar.DAY_OF_MONTH, -1);

                long now = System.currentTimeMillis();
                if (before.getTimeInMillis() > now) {
                    String key = id + "_before";
                    schedule(context, key, before.getTimeInMillis(),
                            "Vade yarın",
                            customer + " • " + receipt + " • Kalan: " + formatMoney(remaining));
                    keys.add(key);
                }
                if (due.getTimeInMillis() > now) {
                    String key = id + "_due";
                    schedule(context, key, due.getTimeInMillis(),
                            "Vade bugün",
                            customer + " • " + receipt + " • Tahsil edilecek: " + formatMoney(remaining));
                    keys.add(key);
                }
            }
        } catch (Exception ignored) {
        }

        prefs.edit().putString(KEY_JSON, remindersJson).putStringSet(KEY_ALARMS, keys).apply();
    }

    public static void rescheduleStored(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_JSON, "[]");
        sync(context, json);
    }

    private static void schedule(Context context, String key, long when, String title, String body) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Intent intent = new Intent(context, DueReminderReceiver.class);
        intent.setAction("com.yagsatis.mobile.DUE_" + key);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        intent.putExtra("notification_id", Math.abs(key.hashCode()));
        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                Math.abs(key.hashCode()),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
    }

    private static void cancelKnown(Context context, Set<String> keys) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null || keys == null) return;
        for (String key : keys) {
            Intent intent = new Intent(context, DueReminderReceiver.class);
            intent.setAction("com.yagsatis.mobile.DUE_" + key);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    Math.abs(key.hashCode()),
                    intent,
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );
            if (pi != null) {
                am.cancel(pi);
                pi.cancel();
            }
        }
    }

    private static String formatMoney(double value) {
        return String.format(new Locale("tr", "TR"), "%,.2f TL", value);
    }
}
