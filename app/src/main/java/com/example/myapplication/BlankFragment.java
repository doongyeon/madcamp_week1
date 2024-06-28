package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class BlankFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private RecyclerView recyclerView;
    private EditText editTextEvent;
    private Button buttonAddEvent;
    private EventAdapter eventAdapter;
    private List<String> eventList;
    private Map<String, List<String>> eventsMap;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        calendarView = view.findViewById(R.id.calendar_view);
        recyclerView = view.findViewById(R.id.recycler_view);
        editTextEvent = view.findViewById(R.id.edit_text_event);
        buttonAddEvent = view.findViewById(R.id.button_add_event);

        eventList = new ArrayList<>();
        eventsMap = new HashMap<>();
        eventAdapter = new EventAdapter(eventList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(eventAdapter);

        Calendar today = Calendar.getInstance();
        selectedDate = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(today.getTime());

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedDate = date.getDay() + "/" + (date.getMonth() + 1) + "/" + date.getYear();
                updateEventList();
            }
        });

        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = editTextEvent.getText().toString();
                if (!event.isEmpty() && selectedDate != null) {
                    List<String> events = eventsMap.getOrDefault(selectedDate, new ArrayList<>());
                    events.add(event);
                    eventsMap.put(selectedDate, events);
                    updateEventList();
                    editTextEvent.setText("");

                    // 오늘 날짜를 강조하기 위해 데코레이터 추가
                    HashSet<CalendarDay> dates = new HashSet<>();
                    dates.add(CalendarDay.today());
                    calendarView.addDecorator(new EventDecorator(0xFF00FF00, dates));

                    Toast.makeText(getContext(), "Event added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please select a date and enter an event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 오늘 날짜를 강조하기 위해 데코레이터 추가
        HashSet<CalendarDay> dates = new HashSet<>();
        dates.add(CalendarDay.today());
        calendarView.addDecorator(new EventDecorator(0xFF00FF00, dates));

        // 오늘 날짜를 선택된 상태로 설정
        calendarView.setDateSelected(CalendarDay.today(), true);

        return view;
    }

    private void updateEventList() {
        eventList.clear();
        if (selectedDate != null && eventsMap.containsKey(selectedDate)) {
            eventList.addAll(eventsMap.get(selectedDate));
        }
        eventAdapter.notifyDataSetChanged();
    }
}
