package com.hushquiet.mailclient.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hushquiet.mailclient.Activities.Interfaces.ICallBackMessage;
import com.hushquiet.mailclient.Activities.Interfaces.IFragment;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Helpers.MailBox;
import com.hushquiet.mailclient.Helpers.MessageContainer;
import com.hushquiet.mailclient.Helpers.MyMessage;
import com.hushquiet.mailclient.Mail.Mail;
import com.hushquiet.mailclient.Mail.ReadMailTask;
import com.hushquiet.mailclient.R;

public class InboxFragment extends Fragment implements IFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView listViewMessages;
    private MessageContainer messageContainer;
    private Context context;
    private ArrayAdapter<String> adapter;
    private ImageView imageView;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ICallBackMessage mListener;

    public InboxFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InboxFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InboxFragment newInstance(String param1, String param2) {
        InboxFragment fragment = new InboxFragment();
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

    public void reload() {
        MailBox mailBox = DB.getInstance(context).getCurrentMailbox();
        if (mailBox != null) {
            new ReadMailTask(getActivity(), this).execute(mailBox.email, mailBox.password);
            messageContainer = DB.getInstance(context).getMessages(DB.INBOX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_inbox, container, false);
        imageView = (ImageView)root.findViewById(R.id.imageView);
        listViewMessages = root.findViewById(R.id.listViewMessages);
        context = root.getContext();
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        listViewMessages.setAdapter(adapter);
        listViewMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context, messageContainer.getMessage(i).body, Toast.LENGTH_LONG).show();
                mListener.setMessageFragment(messageContainer.getMessage(i), MainActivity.INBOXFRAGMENT);
            }
        });
        messageContainer = DB.getInstance(context).getMessages(DB.INBOX);
        if (messageContainer != null) {
            setMessageContainer(messageContainer);
        }
        listViewMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                removeMessage(i);
                return false;
            }
        });
      //  reload();
        return root;
    }

    public void removeMessage(int i) {
        DB db = DB.getInstance(context);
        MyMessage message = messageContainer.getMessage(i);
        adapter.remove(message.from);
        if ( db.deleteMessage(message.id) ) {
            db.addToMessages(message.subject, message.body, message.from, message.to,
                    message.date, DB.DELETED, message.idMailbox, new String[] {message.fileName},
                    message.data, message.sign);
        }
        messageContainer.removeMessage(message);
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

    @Override
    public void setMessageContainer(MessageContainer messageContainer) {
        this.messageContainer = messageContainer;
        final String [] froms = new String[this.messageContainer.getCount()];

        for (int i = 0; i < messageContainer.getCount(); i++) {
            froms[i] = messageContainer.getMessage(i).from;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
                adapter.addAll(froms);
                imageView.setImageBitmap(Mail.bitmap);
            }
        });

    }
}
