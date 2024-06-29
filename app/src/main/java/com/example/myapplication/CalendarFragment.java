package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.decorators.EventDecorator;
import com.example.myapplication.decorators.TodayDecorator;
import com.example.myapplication.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarFragment extends Fragment implements AddEventDialogFragment.AddEventDialogListener {

    private MaterialCalendarView calendarView;
    private TextView noEventTextView;
    private ListView listViewEvents;
    private ImageButton addButton;
    private List<Event> events;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate selectedDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        listViewEvents = view.findViewById(R.id.eventListView);
        noEventTextView = view.findViewById(R.id.noEventTextView);
        addButton = view.findViewById(R.id.buttonAddEvent);

        loadEvents();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            displayEventsForDate(selectedDate);
        });

        addButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                String date = selectedDate.toString();
                AddEventDialogFragment dialog = AddEventDialogFragment.newInstance(date);
                dialog.setAddEventDialogListener(CalendarFragment.this);
                dialog.show(getParentFragmentManager(), "AddEventDialog");
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

    private void displayEventsForDate(LocalDate date) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            if (eventDate.equals(date)) {
                filteredEvents.add(event);
            }
        }

        if (filteredEvents.isEmpty()) {
            noEventTextView.setVisibility(View.VISIBLE);
            listViewEvents.setVisibility(View.INVISIBLE);
        } else {
            noEventTextView.setVisibility(View.INVISIBLE);
            listViewEvents.setVisibility(View.VISIBLE);

            EventAdapter adapter = new EventAdapter(getContext(), filteredEvents);
            listViewEvents.setAdapter(adapter);

            listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event clickedEvent = (Event) parent.getItemAtPosition(position);
                    Intent intent = new Intent(getContext(), EventInfoActivity.class);
                    intent.putExtra("event", clickedEvent);
                    startActivity(intent);
                }
            });
        }
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
