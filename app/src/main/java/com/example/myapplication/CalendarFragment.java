package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements AddEventDialogFragment.AddEventDialogListener {

    private MaterialCalendarView calendarView;
    private TextView eventDetailsTextView;
    private List<Event> events;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        eventDetailsTextView = view.findViewById(R.id.eventDetailsTextView);
        Button addButton = view.findViewById(R.id.buttonAddEvent);

        loadEvents();

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
                displayEventsForDate(selectedDate);
            }
        });

        addButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                String date = selectedDate.toString();
                AddEventDialogFragment dialog = AddEventDialogFragment.newInstance(date);
                dialog.setAddEventDialogListener(CalendarFragment.this);
                dialog.show(getFragmentManager(), "AddEventDialog");
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

            // 로그 추가
            for (Event event : events) {
                Log.d("CalendarFragment", "Loaded event: " + event.getTitle() + " on " + event.getDate());
            }
        } catch (Exception e) {
            Log.e("CalendarFragment", "Error reading events.json", e);
            events = new ArrayList<>();
        }
    }

    private void displayEventsForDate(LocalDate date) {
        StringBuilder eventDetails = new StringBuilder();
        Log.d("CalendarFragment", "Selected date: " + date.toString()); // 선택된 날짜 로그 추가
        for (Event event : events) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            Log.d("CalendarFragment", "Comparing with event date: " + eventDate.toString()); // 이벤트 날짜 로그 추가
            if (eventDate.equals(date)) {
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
        LocalDate today = LocalDate.now();
        CalendarDay calendarDay = CalendarDay.from(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        calendarView.setDateSelected(calendarDay, true);
        selectedDate = today;
        displayEventsForDate(today);
        calendarView.addDecorator(new TodayDecorator(calendarDay));
    }

    @Override
    public void onEventAdded(Event event) {
        events.add(event);
        addEventDecorators();
        displayEventsForDate(LocalDate.parse(event.getDate(), dateFormatter));
    }
}
