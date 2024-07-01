package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myapplication.utils.ContactUtils;
import com.example.myapplication.utils.SoundSearcher;
import com.example.myapplication.utils.StorageUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContactFragment extends Fragment {
    private static final int REQUEST_PERMISSION_CODE = 100;

    private List<Contact> originalContacts;
    private List<Contact> contacts;
    private ContactAdapter adapter;
    private ActivityResultLauncher<Intent> addContactLauncher;
    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        ListView listView = view.findViewById(R.id.listView);
        SearchView searchView = view.findViewById(R.id.searchView);
        ImageButton addButton = view.findViewById(R.id.addButton);
        ImageButton filterButton = view.findViewById(R.id.filterButton);

        /// JSON 데이터를 SharedPreferences로부터 불러오기
        String jsonContacts = StorageUtils.loadContacts(getContext());
        if (jsonContacts == null) {
            jsonContacts = loadJSONFromResource(R.raw.contacts);
            StorageUtils.saveContacts(getContext(), jsonContacts); // 처음 불러올 때 SharedPreferences에 저장
        }

        originalContacts = ContactUtils.parseContacts(jsonContacts);
        contacts = new ArrayList<>(originalContacts);

        // Sort contacts in alphabetical order
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });

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

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
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
                            // Sort the list after adding a new contact
                            Collections.sort(contacts, new Comparator<Contact>() {
                                @Override
                                public int compare(Contact c1, Contact c2) {
                                    return c1.getName().compareTo(c2.getName());
                                }
                            });
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );

        return view;
    }


    private void showCustomDialog() {
        // Create the custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);

        // Set up the buttons
        ImageButton buttonCancel = dialog.findViewById(R.id.button_cancel);
        ImageButton buttonOk = dialog.findViewById(R.id.button_ok);

        CheckBox checkBox1 = dialog.findViewById(R.id.checkBox1);
        CheckBox checkBox2 = dialog.findViewById(R.id.checkBox2);
        CheckBox checkBox3 = dialog.findViewById(R.id.checkBox3);
        CheckBox checkBox4 = dialog.findViewById(R.id.checkBox4);
        CheckBox checkBox5 = dialog.findViewById(R.id.checkBox5);
        CheckBox checkBox6 = dialog.findViewById(R.id.checkBox6);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Close the dialog
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Filter contacts based on selected checkboxes
                List<Contact> filteredContacts = new ArrayList<>();
                for (Contact contact : originalContacts) {
                    boolean matchesFilter = false;
                    if (checkBox1.isChecked()) {
                        matchesFilter = true;
                    }
                    if (checkBox2.isChecked() && contact.getRole().equals("운영진")) {
                        matchesFilter = true;
                    }
                    if (checkBox3.isChecked() && contact.getGroup().equals("1분반")) {
                        matchesFilter = true;
                    }
                    if (checkBox4.isChecked() && contact.getGroup().equals("2분반")) {
                        matchesFilter = true;
                    }
                    if (checkBox5.isChecked() && contact.getGroup().equals("3분반")) {
                        matchesFilter = true;
                    }
                    if (checkBox6.isChecked() && contact.getGroup().equals("4분반")) {
                        matchesFilter = true;
                    }
                    if (matchesFilter) {
                        filteredContacts.add(contact);
                    }
                }
                contacts.clear();
                contacts.addAll(filteredContacts);
                // Sort the filtered list
                Collections.sort(contacts, new Comparator<Contact>() {
                    @Override
                    public int compare(Contact c1, Contact c2) {
                        return c1.getName().compareTo(c2.getName());
                    }
                });
                adapter.notifyDataSetChanged();

                dialog.dismiss(); // Close the dialog
            }
        });

        dialog.show();
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
        // Sort the list after filtering
        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });
        adapter.notifyDataSetChanged();
    }
}
