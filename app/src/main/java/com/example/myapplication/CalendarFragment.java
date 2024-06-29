package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.model.Event;
import com.example.myapplication.decorators.EventDecorator;
import com.example.myapplication.decorators.TodayDecorator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView eventDetailsTextView;
    private List<Event> events;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        eventDetailsTextView = view.findViewById(R.id.eventDetailsTextView);

        loadEvents();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                displayEventsForDate(date);
            }
        });

        addEventDecorators();
        selectTodayDate();

        return view;
    }

    private void loadEvents() {
        try {
            InputStream is = getResources().openRawResource(R.raw.events);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Type eventType = new TypeToken<ArrayList<Event>>() {}.getType();
            events = new Gson().fromJson(reader, eventType);
            reader.close();
        } catch (Exception e) {
            Log.e("CalendarFragment", "Error reading events.json", e);
            events = new ArrayList<>();
        }
    }

    private void displayEventsForDate(CalendarDay date) {
        StringBuilder eventDetails = new StringBuilder();
        LocalDate selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
        for (Event event : events) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            if (eventDate.equals(selectedDate)) {
                eventDetails.append("Title: ").append(event.getTitle()).append("\n")
                        .append("Contents: ").append(event.getContents()).append("\n\n");
            }
        }
        if (eventDetails.length() == 0) {
            eventDetails.append("No events for this date.");
        }
        eventDetailsTextView.setText(eventDetails.toString());
    }

    private void addEventDecorators() {
        for (Event event : events) {
            try {
                LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
                CalendarDay calendarDay = CalendarDay.from(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth());
                calendarView.addDecorator(new EventDecorator(calendarDay));
            } catch (Exception e) {
                Log.e("CalendarFragment", "Error adding event decorator", e);
            }
        }
    }

    private void selectTodayDate() {
        CalendarDay today = CalendarDay.today();
        calendarView.setDateSelected(today, true);
        displayEventsForDate(today);
        calendarView.addDecorator(new TodayDecorator(today));
    }
}
