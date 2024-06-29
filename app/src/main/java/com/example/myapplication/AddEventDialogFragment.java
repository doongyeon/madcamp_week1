package com.example.myapplication;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.model.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AddEventDialogFragment extends DialogFragment {

    public interface AddEventDialogListener {
        void onEventAdded(Event event);
    }

    private AddEventDialogListener listener;
    private String selectedDate;

    public void setAddEventDialogListener(AddEventDialogListener listener) {
        this.listener = listener;
    }

    public static AddEventDialogFragment newInstance(String date) {
        AddEventDialogFragment fragment = new AddEventDialogFragment();
        Bundle args = new Bundle();
        args.putString("selected_date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            selectedDate = getArguments().getString("selected_date");
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_event, null);

        EditText titleEditText = view.findViewById(R.id.editTextTitle);
        EditText contentsEditText = view.findViewById(R.id.editTextContents);

        builder.setView(view)
                .setTitle("Add Event")
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleEditText.getText().toString();
                    String contents = contentsEditText.getText().toString();

                    if (TextUtils.isEmpty(title) || TextUtils.isEmpty(contents)) {
                        Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Event event = new Event(title, contents, "", "", "", selectedDate, "private");
                    if (listener != null) {
                        listener.onEventAdded(event);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}