package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private static final String SHARED_PREFS_NAME = "calendar_events";
    private static final String EVENTS_KEY = "events";

    private Context context;
    private List<Event> events;

    public EventAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Event event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        }

        TextView eventTitleTextView = convertView.findViewById(R.id.eventTitleTextView);
        TextView eventContentsTextView = convertView.findViewById(R.id.eventContentsTextView);
        TextView eventTimeTextView = convertView.findViewById(R.id.eventTimeTextView);
        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);

        eventTitleTextView.setText(event.getTitle());
        eventContentsTextView.setText(event.getContents());

        // 시간을 두 자리 숫자로 포맷팅하여 표시
        if (!event.getTime().isEmpty() && !event.getTime().equals("-")) {
            eventTimeTextView.setVisibility(View.VISIBLE);
            eventTimeTextView.setText(formatTime(event.getTime()));
        } else {
            eventTimeTextView.setVisibility(View.GONE);
        }

        if (event.getType().equals("public")) {
            convertView.setBackgroundResource(R.drawable.public_box);
        } else if (event.getType().equals("private")) {
            convertView.setBackgroundResource(R.drawable.contact_box);
        }

        updateFavoriteIcon(favoriteButton, event.getIsFavorite());

        favoriteButton.setOnClickListener(v -> {
            event.setFavorite(!event.getIsFavorite());
            updateFavoriteIcon(favoriteButton, event.getIsFavorite());
            saveEventsToPreferences();

            LayoutInflater inflater = LayoutInflater.from(context);

            View layout = inflater.inflate(
                    R.layout.custom_toast, null
            );

            TextView text = layout.findViewById(R.id.toast_text);

            Toast toast = new Toast(context);

            text.setText(event.getIsFavorite() ? "관심 이벤트에 추가되었습니다" : "관심 이벤트에서 삭제되었습니다");

            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        });

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventInfoActivity.class);
            intent.putExtra("event", event);
            context.startActivity(intent);
        });

        return convertView;
    }

    private void updateFavoriteIcon(ImageButton button, boolean isFavorite) {
        if (isFavorite) {
            button.setImageResource(R.drawable.outline_favorite_24);
        } else {
            button.setImageResource(R.drawable.favorite_24px);
        }
    }

    private void saveEventsToPreferences() {
        // 기존의 이벤트 목록을 불러옵니다
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        String eventsJson = prefs.getString(EVENTS_KEY, null);
        Type eventType = new TypeToken<List<Event>>() {}.getType();
        List<Event> existingEvents = new Gson().fromJson(eventsJson, eventType);

        // 기존 이벤트 목록을 수정된 이벤트 목록으로 업데이트합니다
        for (int i = 0; i < existingEvents.size(); i++) {
            Event existingEvent = existingEvents.get(i);
            for (Event event : events) {
                if (existingEvent.getTitle().equals(event.getTitle())) { // assuming Event has an getId() method
                    existingEvents.set(i, event);
                    break;
                }
            }
        }

        // 업데이트된 이벤트 목록을 저장합니다
        SharedPreferences.Editor editor = prefs.edit();
        String updatedEventsJson = new Gson().toJson(existingEvents);
        editor.putString(EVENTS_KEY, updatedEventsJson);
        editor.apply();
    }

    // 시간을 두 자리 숫자로 포맷팅하는 메서드
    private String formatTime(String time) {
        if (time.length() == 5) {
            return time;
        } else if (time.length() == 4) {
            return "0" + time;
        } else {
            return time;
        }
    }
}
