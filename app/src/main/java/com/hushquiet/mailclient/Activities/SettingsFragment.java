package com.hushquiet.mailclient.Activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hushquiet.mailclient.Activities.Interfaces.OnFragmentInteractionListener;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Mail.Settings;
import com.hushquiet.mailclient.R;

public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextImapServer, editTextImapPort;
    private EditText editTextSmptServer, editTextSmtpPort;
    private CheckBox checkBoxCrypt;
    private CheckBox checkBoxSign;
    private Button buttonSave;
    private Spinner spinnerMailBoxes;
    private Context context;
    private ArrayAdapter<String> adapter;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_settings, container, false);
        context = root.getContext();
        DB db = DB.getInstance(root.getContext());
        Settings settings = new Settings(db.getSettings(db.getAuthUserID()));
        editTextName = (EditText)root.findViewById(R.id.editTextName);
        buttonSave = (Button)root.findViewById(R.id.buttonSave);
        editTextLastName = (EditText)root.findViewById(R.id.editTextLastName);

        editTextImapPort = (EditText)root.findViewById(R.id.editTextImapPort);
        editTextImapServer = (EditText)root.findViewById(R.id.editTextImapServer);
        editTextSmptServer = (EditText)root.findViewById(R.id.editTextSmtpServer);
        editTextSmtpPort = (EditText)root.findViewById(R.id.editTextSmtpPort);

        editTextName = (EditText)root.findViewById(R.id.editTextName);
        checkBoxCrypt = (CheckBox)root.findViewById(R.id.checkBoxCrypt);
        checkBoxSign = (CheckBox)root.findViewById(R.id.checkBoxSign);
        spinnerMailBoxes = (Spinner)root.findViewById(R.id.spinnerMailBoxes);
        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMailBoxes.setAdapter(adapter);
        updateSpinner();
        spinnerMailBoxes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                updateSpinner();
                return false;
            }
        });

        int item = -1;

        Cursor cursor = db.getUser(db.getAuthUserID());
        editTextName.setText(cursor.getString(cursor.getColumnIndex(DB.USERS_NAME)));
        editTextLastName.setText(cursor.getString(cursor.getColumnIndex(DB.USERS_LAST_NAME)));

        cursor = db.getSettings(db.getAuthUserID());

        editTextSmtpPort.setText(settings.smtpPort + "");
        editTextSmptServer.setText(settings.smtpServer);
        editTextImapServer.setText(settings.imapServer);
        editTextImapPort.setText(settings.imapPort + "");
        if (cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_CRYPT)) == 0) {
            checkBoxCrypt.setChecked(false);
        } else checkBoxCrypt.setChecked(true);

        if (cursor.getInt(cursor.getColumnIndex(DB.SETTINGS_SIGN)) == 0) {
            checkBoxSign.setChecked(false);
        } else checkBoxSign.setChecked(true);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DB db = DB.getInstance(root.getContext());
                int id = db.getAuthUserID();
                int sign = checkBoxSign.isChecked() ? 1 : 0;
                int crypt = checkBoxCrypt.isChecked() ? 1 : 0;
                String name = editTextName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                if (db.updateSettings(id, sign, crypt, db.getIdMailBox(spinnerMailBoxes.getSelectedItem().toString()),
                        Integer.parseInt(editTextSmtpPort.getText().toString()),
                        Integer.parseInt(editTextImapPort.getText().toString()),
                        editTextImapServer.getText().toString(),
                        editTextSmptServer.getText().toString())
                        && db.updateUser(id, name, lastName)) {
                    Toast.makeText(root.getContext(), "Данные обновлены.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    // переписать здесь ошибка выбора почтового ящика
    public void updateSpinner() {
        DB db = DB.getInstance(context);
        Cursor cursor = db.getMailBoxes(db.getAuthUserID());

        int i = 0;
        String text;
        while(cursor.moveToNext()) {
            text = cursor.getString(cursor.getColumnIndex(DB.MAILBOXES_MAIL));
            if (adapter.getCount() == i)
                adapter.add(text);
            i++;
        }

        int selected = db.getMailFromSetting(db.getAuthUserID());
        String mail;
        if (selected > -1) {
            mail = db.getMailboxById(selected);
            for (i = 0; i < spinnerMailBoxes.getCount(); i++) {
                if (spinnerMailBoxes.getItemAtPosition(i).equals(mail));
                    spinnerMailBoxes.setSelection(i);
            }

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
