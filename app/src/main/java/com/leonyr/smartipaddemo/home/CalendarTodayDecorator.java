package com.leonyr.smartipaddemo.home;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.leonyr.calendarview.CalendarDay;
import com.leonyr.calendarview.DayViewDecorator;
import com.leonyr.calendarview.DayViewFacade;
import com.leonyr.smartipaddemo.R;

/**
 * ==============================================================
 * Description:
 * <p>
 * Created by leonyr on 2019/8/15
 * (C) Copyright LeonyR Corporation 2014 All Rights Reserved.
 * ==============================================================
 */
public class CalendarTodayDecorator implements DayViewDecorator {

    private final CalendarDay today;
    private final Drawable todayBackground;

    public CalendarTodayDecorator(Context ctx) {
        today = CalendarDay.today();
        todayBackground = ctx.getResources().getDrawable(R.drawable.circle_today_background);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return today.equals(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(todayBackground);
    }
}
