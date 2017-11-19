package com.hushquiet.mailclient.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hushquiet.mailclient.Activities.Interfaces.ICallBackMessage;
import com.hushquiet.mailclient.Helpers.MyMessage;
import com.hushquiet.mailclient.Mail.Mail;
import com.hushquiet.mailclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView textViewSubject;
    private TextView textViewBody;
    private TextView textViewFrom;
    private TextView textViewApps;
    private TextView textViewTo;
    private Context context;
    private MyMessage message;
    private ICallBackMessage mListener;
    private LinearLayout layout;
    private LinearLayout layoutHorizontal;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setMessage(MyMessage message) {
        this.message = message;
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
        final View root = inflater.inflate(R.layout.fragment_message, container, false);
        context = root.getContext();
        textViewApps = (TextView)root.findViewById(R.id.textViewApps);
        textViewBody = (TextView)root.findViewById(R.id.textViewBody);
        textViewFrom = (TextView)root.findViewById(R.id.textViewFrom);
        textViewSubject = (TextView)root.findViewById(R.id.textViewSubject);
        textViewTo = (TextView)root.findViewById(R.id.textViewTo);
        layout = (LinearLayout)root.findViewById(R.id.linerLayout);
        new Thread(new Runnable() {
            public void run() {
                while (layout.getWidth() == 0) {

                }
                addApps();
            }
        }).start();
        textViewSubject.setText("Тема: " + (message.subject == null ? "" : message.subject));
        textViewFrom.setText("От: " + (message.from == null ? "" : message.from));
        textViewTo.setText("Кому: " + (message.to == null ? "" : message.to));
        textViewBody.setText("\n" + (message.body == null ? "" : message.body) + "\n");

        if (message != null) {
            String text;
            if (message.sign == 1)
                text = "Данное письмо подписано";
            else if (message.sign == 0)
                text = "Данное письмо не имеет подписи";
            else
                text = "Данное письмо было изменено злоумышленником";

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Подпись")
                    .setMessage(text)
                    .setCancelable(false)
                    .setNegativeButton("Продолжить",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void addApps () {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message.bitmap != null) {
                    ImageView imageView = new ImageView(context);
                    imageView.setImageBitmap(message.bitmap);
                    int scaleHeight = message.bitmap.getWidth() / (layout.getWidth() / 3);
                    message.bitmap = Bitmap.createScaledBitmap(message.bitmap, layout.getWidth() / 3,
                            message.bitmap.getHeight() / (scaleHeight > 0 ? scaleHeight : 1), false);
                    layoutHorizontal = new LinearLayout(context);
                    layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                    layoutHorizontal.setPadding(5, 5, 5, 5);
                    layoutHorizontal.addView(imageView);
                    layout.addView(layoutHorizontal);
                }
            }
        });



       /* } else {
            layoutHorizontal = (LinearLayout) layout.getChildAt(layout.getChildCount() - 1);
            layoutHorizontal.setPadding(20, 5, 20, 5);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutHorizontal.getWidth(), layoutHorizontal.getHeight());
            params.setMargins(5, 5, 50, 5);
            layoutHorizontal.setLayoutParams(params);
            layoutHorizontal.addView(imageView);
        }*/
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
