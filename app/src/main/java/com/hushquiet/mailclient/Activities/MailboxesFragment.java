package com.hushquiet.mailclient.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hushquiet.mailclient.Activities.Interfaces.OnFragmentInteractionListener;
import com.hushquiet.mailclient.Mail.AuthMailTask;
import com.hushquiet.mailclient.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MailboxesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MailboxesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonAdd;

    private OnFragmentInteractionListener mListener;

    public MailboxesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MailboxesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MailboxesFragment newInstance(String param1, String param2) {
        MailboxesFragment fragment = new MailboxesFragment();
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
        final View root = inflater.inflate(R.layout.fragment_mailboxes, container, false);

        editTextEmail = root.findViewById(R.id.editTextEmail);
        editTextPassword = root.findViewById(R.id.editTextPassword);
        buttonAdd = root.findViewById(R.id.buttonAdd);
        final MailboxesFragment mailboxesFragment = this;

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthMailTask authMailTask = new AuthMailTask(getActivity(),
                        mailboxesFragment);
                authMailTask.execute(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            }
        });

        return root;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(int state) {
        if (mListener != null) {
            mListener.onFragmentInteraction(1);
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
