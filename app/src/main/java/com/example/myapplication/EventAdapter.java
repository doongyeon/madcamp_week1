package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.Event;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

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
        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);

        eventTitleTextView.setText(event.getTitle());
        eventContentsTextView.setText(event.getContents());

        if (event.getType().equals("public")) {
            convertView.setBackgroundResource(R.drawable.public_box);
        } else if (event.getType().equals("private")) {
            convertView.setBackgroundResource(R.drawable.contact_box);
        }

        updateFavoriteIcon(favoriteButton, event.getIsFavorite());

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setFavorite(!event.getIsFavorite());
                updateFavoriteIcon(favoriteButton, event.getIsFavorite());
                Toast.makeText(context, event.getIsFavorite() ? "관심 이벤트에 추가되었습니다" : "관심 이벤트에서 삭제되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventInfoActivity.class);
                intent.putExtra("event", event);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void updateFavoriteIcon(ImageButton button, boolean isFavorite) {
        if(isFavorite) {
            button.setImageResource(R.drawable.outline_favorite_24);
        } else {
            button.setImageResource(R.drawable.favorite_24px);
        }
    }
}
