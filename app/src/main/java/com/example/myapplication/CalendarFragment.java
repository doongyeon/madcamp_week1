package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.decorators.EventDecorator;
import com.example.myapplication.decorators.TodayDecorator;
import com.example.myapplication.decorators.MultipleDotDecorator;
import com.example.myapplication.Event;
import android.widget.LinearLayout;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CalendarFragment extends Fragment {

    private static final String SHARED_PREFS_NAME = "calendar_events";
    private static final String EVENTS_KEY = "events";
    private static final String INITIAL_LOAD_KEY = "initial_load";

    private MaterialCalendarView calendarView;
    private TextView noEventTextView, filterEventText;
    private ListView listViewEvents;
    private ImageButton addButton, filterEventIcon;
    private List<Event> events;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private LocalDate selectedDate;
    private LinearLayout filterEventLayout;
    private boolean showOnlyFavorites = false;
    private ActivityResultLauncher<Intent> addEventLauncher;
    private List<Event> newEventsWithoutTime = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initializeViews(view);
        loadEvents();
        setupCalendarView();
        setupListView();
        setupButtons();
        setupAddEventLauncher();
        addEventDecorators();
        selectTodayDate();

        return view;
    }

    private void initializeViews(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        listViewEvents = view.findViewById(R.id.eventListView);
        noEventTextView = view.findViewById(R.id.noEventTextView);
        addButton = view.findViewById(R.id.buttonAddEvent);
        filterEventLayout = view.findViewById(R.id.filterEventLayout);
        filterEventIcon = view.findViewById(R.id.filterEventIcon);
        filterEventText = view.findViewById(R.id.filterEventText);
    }

    private void loadEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        boolean initialLoad = prefs.getBoolean(INITIAL_LOAD_KEY, false);

        if (!initialLoad) {
            try {
                InputStream is = getResources().openRawResource(R.raw.events);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                Type eventType = new TypeToken<ArrayList<Event>>() {}.getType();
                events = new Gson().fromJson(reader, eventType);
                reader.close();

                storeEvents(events); // JSON 파일에서 로드된 이벤트 저장

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(INITIAL_LOAD_KEY, true);
                editor.apply();
            } catch (Exception e) {
                Log.e("CalendarFragment", "Error reading events.json", e);
                events = new ArrayList<>();
            }
        } else {
            loadStoredEvents();
        }
    }

    private void loadStoredEvents() {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String eventsJson = prefs.getString(EVENTS_KEY, null);
        if (eventsJson != null) {
            Type eventType = new TypeToken<ArrayList<Event>>() {}.getType();
            events = new Gson().fromJson(eventsJson, eventType);
        } else {
            events = new ArrayList<>();
        }
    }

    private void storeEvents(List<Event> events) {
        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String eventsJson = new Gson().toJson(events);
        editor.putString(EVENTS_KEY, eventsJson);
        editor.apply();
    }

    private void setupCalendarView() {
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            displayEventsForDate(selectedDate, showOnlyFavorites);
        });
    }

    private void setupListView() {
        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            Event clickedEvent = (Event) parent.getItemAtPosition(position);
            Intent intent = new Intent(getContext(), EventInfoActivity.class);
            intent.putExtra("event", clickedEvent);
            startActivity(intent);
        });
    }

    private void setupButtons() {
        addButton.setOnClickListener(v -> {
            if (selectedDate != null) {
                Intent intent = new Intent(getContext(), AddEventActivity.class);
                String date = selectedDate.toString();
                intent.putExtra("selectedDate", date);
                addEventLauncher.launch(intent);
            }
        });

        filterEventLayout.setOnClickListener(v -> {
            showOnlyFavorites = !showOnlyFavorites;
            displayEventsForDate(selectedDate, showOnlyFavorites);
            updateFilterButtonText();
        });
    }

    private void setupAddEventLauncher() {
        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Event newEvent = (Event) result.getData().getSerializableExtra("newEvent");
                        if (newEvent != null) {
                            if (newEvent.getTime().isEmpty()) {
                                newEventsWithoutTime.add(newEvent);
                            } else {
                                events.add(newEvent);
                                storeEvents(events); // 이벤트 저장
                            }
                            addEventDecorators();
                            displayEventsForDate(selectedDate, showOnlyFavorites);
                        }
                    }
                }
        );
    }

    private void displayEventsForDate(LocalDate date, boolean onlyFavorites) {
        List<Event> filteredEvents = filterEventsForDate(date, onlyFavorites);
        List<Event> filteredNewEventsWithoutTime = filterNewEventsWithoutTimeForDate(date, onlyFavorites);

        // Combine and sort the filtered events
        List<Event> combinedEvents = new ArrayList<>(filteredEvents);
        combinedEvents.addAll(filteredNewEventsWithoutTime);

        Collections.sort(combinedEvents, new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                if (e1.getType().equals("public") && !e2.getType().equals("public")) {
                    return -1;
                }
                if (!e1.getType().equals("public") && e2.getType().equals("public")) {
                    return 1;
                }
                if (e1.getTime().isEmpty() && e2.getTime().isEmpty()) {
                    return 0;
                }
                if (e1.getTime().isEmpty()) {
                    return -1;
                }
                if (e2.getTime().isEmpty()) {
                    return 1;
                }
                return e1.getTime().compareTo(e2.getTime());
            }
        });

        updateEventListView(combinedEvents);
    }

    private List<Event> filterEventsForDate(LocalDate date, boolean onlyFavorites) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            if (eventDate.equals(date) && (!onlyFavorites || event.getIsFavorite())) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    private List<Event> filterNewEventsWithoutTimeForDate(LocalDate date, boolean onlyFavorites) {
        List<Event> filteredNewEventsWithoutTime = new ArrayList<>();
        for (Event event : newEventsWithoutTime) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            if (eventDate.equals(date) && (!onlyFavorites || event.getIsFavorite())) {
                filteredNewEventsWithoutTime.add(event);
            }
        }
        return filteredNewEventsWithoutTime;
    }

    private void updateEventListView(List<Event> filteredEvents) {
        if (filteredEvents.isEmpty()) {
            noEventTextView.setVisibility(View.VISIBLE);
            listViewEvents.setVisibility(View.INVISIBLE);
        } else {
            noEventTextView.setVisibility(View.INVISIBLE);
            listViewEvents.setVisibility(View.VISIBLE);

            EventAdapter adapter = new EventAdapter(getContext(), filteredEvents);
            listViewEvents.setAdapter(adapter);

            listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
                Event clickedEvent = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), EventInfoActivity.class);
                intent.putExtra("event", clickedEvent);
                startActivity(intent);
            });
        }
    }

    private void addEventDecorators() {
        calendarView.removeDecorators();  // 기존 데코레이터 제거

        HashMap<CalendarDay, Integer> datesWithDots = new HashMap<>();

        for (Event event : events) {
            addEventToDecorator(datesWithDots, event);
        }

        for (Event event : newEventsWithoutTime) {
            addEventToDecorator(datesWithDots, event);
        }

        calendarView.addDecorator(new MultipleDotDecorator(datesWithDots));

        // 오늘 날짜 강조
        LocalDate today = LocalDate.now();
        CalendarDay todayCalendarDay = CalendarDay.from(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        calendarView.addDecorator(new TodayDecorator(todayCalendarDay));
    }

    private void addEventToDecorator(HashMap<CalendarDay, Integer> datesWithDots, Event event) {
        try {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            CalendarDay calendarDay = CalendarDay.from(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth());
            datesWithDots.put(calendarDay, datesWithDots.getOrDefault(calendarDay, 0) + 1);
        } catch (Exception e) {
            Log.e("CalendarFragment", "Error adding event decorator", e);
        }
    }

    private void selectTodayDate() {
        LocalDate today = LocalDate.now();
        CalendarDay calendarDay = CalendarDay.from(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        calendarView.setDateSelected(calendarDay, true);
        selectedDate = today;
        displayEventsForDate(today, false);
        calendarView.addDecorator(new TodayDecorator(calendarDay));
    }

    private void updateFilterButtonText() {
        if (showOnlyFavorites) {
            filterEventIcon.setImageResource(R.drawable.favorite_small);
            filterEventText.setText("전체 이벤트 보기");
        } else {
            filterEventIcon.setImageResource(R.drawable.outline_favorite_small);
            filterEventText.setText("관심 이벤트만 보기");
        }
    }
}
