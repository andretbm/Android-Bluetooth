package com.example.afs.ihome;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

/**
 * Created by fguimaraes on 11/07/2017.
 */

public class appWidget extends AppWidgetProvider {
    private static final String TAG = "MainActivity";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");
	/*	if (appWidgetIds != null) {
			for (int mAppWidgetId : appWidgetIds) {
				///
			}
		}
	*/
        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }
}
