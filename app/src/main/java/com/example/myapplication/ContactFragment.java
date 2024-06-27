package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.utils.ContactUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ContactFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ListView listView = view.findViewById(R.id.listView);

        String jsonContacts = loadJSONFromResource(R.raw.contacts);
        List<Contact> contacts = ContactUtils.parseContacts(jsonContacts);
        ContactAdapter adapter = new ContactAdapter(getContext(), contacts);
        listView.setAdapter(adapter);

        return view;
    }

    private String loadJSONFromResource(int resourceId) {
        StringBuilder json = new StringBuilder();
        try {
            InputStream inputStream = getResources().openRawResource(resourceId);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line);
            }
            bufferedReader.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}

