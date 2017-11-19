package com.hushquiet.mailclient.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hushquiet.mailclient.Activities.Interfaces.ICallBackMessage;
import com.hushquiet.mailclient.Activities.Interfaces.OnFragmentInteractionListener;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Helpers.MessageContainer;
import com.hushquiet.mailclient.R;

public class DraftsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ListView listViewMessages;
    private MessageContainer messageContainer;
    private Context context;
    private ArrayAdapter<String> adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ICallBackMessage mListener;

    public DraftsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DraftsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DraftsFragment newInstance(String param1, String param2) {
        DraftsFragment fragment = new DraftsFragment();
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
        final View root = inflater.inflate(R.layout.fragment_drafts, container, false);
        listViewMessages = (ListView)root.findViewById(R.id.listViewDraftMessages);
        context = root.getContext();
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        listViewMessages.setAdapter(adapter);
        setAdapter();

        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (messageContainer != null) {
                    mListener.setMessageFragment(messageContainer.getMessage(i), MainActivity.DRAFTSFRAGMENT);
                }
            }
        });
        return root;
    }

    private void setAdapter() {
        DB db = DB.getInstance(context);
        messageContainer = db.getMessages(DB.DRAFTS);
        if (messageContainer != null) {
            for (int i = 0; i < messageContainer.getCount(); i++) {
                adapter.add(messageContainer.getMessage(i).to.equals("") ? i + "" : messageContainer.getMessage(i).to);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ICallBackMessage) {
            mListener = (ICallBackMessage) context;
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
