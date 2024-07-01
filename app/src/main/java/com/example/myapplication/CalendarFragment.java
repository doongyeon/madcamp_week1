package com.example.myapplication;

import android.app.Activity;
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
import java.util.HashMap;
import java.util.List;

public class CalendarFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = view.findViewById(R.id.calendarView);
        listViewEvents = view.findViewById(R.id.eventListView);
        noEventTextView = view.findViewById(R.id.noEventTextView);
        addButton = view.findViewById(R.id.buttonAddEvent);
        filterEventLayout = view.findViewById(R.id.filterEventLayout);
        filterEventIcon = view.findViewById(R.id.filterEventIcon);
        filterEventText = view.findViewById(R.id.filterEventText);

        loadEvents();

        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            selectedDate = LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
            displayEventsForDate(selectedDate, false);
        });

        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), EventInfoActivity.class);
                intent.putExtra("event", clickedEvent);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDate != null) {
                    Intent intent = new Intent(getContext(), AddEventActivity.class);
                    String date = selectedDate.toString(); // LocalDate를 String으로 변환
                    intent.putExtra("selectedDate", date);
                    addEventLauncher.launch(intent);
            }
            }
        });

        filterEventLayout.setOnClickListener(v -> {
            showOnlyFavorites = !showOnlyFavorites; // 관심 이벤트만 보기 토글
            displayEventsForDate(selectedDate, showOnlyFavorites);
            updateFilterButtonText();
        });

        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Event newEvent = (Event) result.getData().getSerializableExtra("newEvent");
                        if (newEvent != null) {
                            events.add(newEvent);
                            addEventDecorators();
                            displayEventsForDate(selectedDate, showOnlyFavorites);
                        }
                    }
                }
        );

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

    private void displayEventsForDate(LocalDate date, boolean onlyFavorites) {
        List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
            if (eventDate.equals(date) && (!onlyFavorites || event.getIsFavorite())) {
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
        calendarView.removeDecorators();  // 기존 데코레이터 제거

        HashMap<CalendarDay, Integer> datesWithDots = new HashMap<>();

        for (Event event : events) {
            try {
                LocalDate eventDate = LocalDate.parse(event.getDate(), dateFormatter);
                CalendarDay calendarDay = CalendarDay.from(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth());

                // 이벤트 수에 따라 dots 정보 추가
                datesWithDots.put(calendarDay, datesWithDots.getOrDefault(calendarDay, 0) + 1);
            } catch (Exception e) {
                Log.e("CalendarFragment", "Error adding event decorator", e);
            }
        }

        calendarView.addDecorator(new MultipleDotDecorator(datesWithDots));

        // 오늘 날짜 강조
        LocalDate today = LocalDate.now();
        CalendarDay todayCalendarDay = CalendarDay.from(today.getYear(), today.getMonthValue(), today.getDayOfMonth());
        calendarView.addDecorator(new TodayDecorator(todayCalendarDay));
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
