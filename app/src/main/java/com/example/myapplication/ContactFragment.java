package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.myapplication.utils.ContactUtils;
import com.example.myapplication.utils.SoundSearcher;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ContactFragment extends Fragment {

    private List<Contact> originalContacts;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    private ActivityResultLauncher<Intent> addContactLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ListView listView = view.findViewById(R.id.listView);
        SearchView searchView = view.findViewById(R.id.searchView);
        Button addButton = view.findViewById(R.id.addButton);

        String jsonContacts = loadJSONFromResource(R.raw.contacts);
        originalContacts = ContactUtils.parseContacts(jsonContacts);
        contacts = new ArrayList<>(originalContacts);
        adapter = new ContactAdapter(getContext(), contacts);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact clickedContact = (Contact) parent.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("contact", clickedContact);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText);
                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddContactActivity.class);
                addContactLauncher.launch(intent);
            }
        });

        addContactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    Contact newContact = (Contact) data.getSerializableExtra("newContact");
                    if (newContact != null) {
                        contacts.add(newContact);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        );

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

    private void filterContacts(String query) {
        contacts.clear();
        if (TextUtils.isEmpty(query)) {
            contacts.addAll(originalContacts);
        } else {
            String initialSoundQuery = SoundSearcher.getInitialSound(query);

            for (Contact contact : originalContacts) {
                String name = contact.getName();
                String initialSoundName = SoundSearcher.getInitialSound(name);

                if (name.contains(query) || initialSoundName.contains(initialSoundQuery)) {
                    contacts.add(contact);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
