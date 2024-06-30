package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, List<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
        }
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewPhone = convertView.findViewById(R.id.textViewPhone);
        TextView textViewGroup = convertView.findViewById(R.id.textViewGroup);
        TextView textViewRole = convertView.findViewById(R.id.textViewRole);

        textViewName.setText(contact.getName());
        textViewPhone.setText(contact.getPhone());
        textViewGroup.setText(contact.getGroup());
        textViewRole.setText(contact.getRole());

        if ("운영진".equals(contact.getRole())) {
            textViewRole.setVisibility(View.VISIBLE);
        } else {
            textViewRole.setVisibility(View.INVISIBLE);
        }

        if ("해당없음".equals(contact.getGroup())) {
            textViewGroup.setVisibility(View.GONE);
        } else {
            textViewGroup.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}


