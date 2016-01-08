package com.quanliren.quan_one.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.quanliren.quan_one.activity.WelcomeActivity_;
import com.quanliren.quan_one.application.AppClass_;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;
    Context activity;

    public DefaultExceptionHandler(Context activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {
            StringWriter stackTrace = new StringWriter();
            ex.printStackTrace(new PrintWriter(stackTrace));
            System.err.println(stackTrace);// You can use LogCat too

            Intent intent = new Intent(activity, WelcomeActivity_.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    AppClass_.getInstance().getBaseContext(), 0, intent, intent.getFlags());

            //Following code will restart your application after 2 seconds
            AlarmManager mgr = (AlarmManager) AppClass_.getInstance().getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    pendingIntent);

            //This will stop your application and take out from it.
            System.exit(2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}