package com.example.myapplication.decorators;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.HashMap;

public class MultipleDotDecorator implements DayViewDecorator {

    private final HashMap<CalendarDay, Integer> datesWithDots;
    private final Paint paint;

    public MultipleDotDecorator(HashMap<CalendarDay, Integer> datesWithDots) {
        this.datesWithDots = datesWithDots;
        this.paint = new Paint();
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return datesWithDots.containsKey(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        CalendarDay date = getDateFromDayViewFacade(view);
        if (date != null && datesWithDots.containsKey(date)) {
            view.addSpan(new MultipleDotSpan());
        }
    }

    private CalendarDay getDateFromDayViewFacade(DayViewFacade view) {
        for (CalendarDay day : datesWithDots.keySet()) {
            if (shouldDecorate(day)) {
                return day;
            }
        }
        return null;
    }

    private class MultipleDotSpan implements LineBackgroundSpan {

        @Override
        public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNumber) {
            float radius = 10f;
            float paddingTop = 15f;
            float y = baseline + radius + paddingTop;
            float x = (left + right) / 2;
            canvas.drawCircle(x, y, radius, paint);
        }
    }
}
