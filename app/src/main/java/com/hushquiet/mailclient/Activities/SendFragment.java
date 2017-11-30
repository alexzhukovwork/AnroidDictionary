package com.hushquiet.mailclient.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hushquiet.mailclient.Activities.Interfaces.ICallBackMessage;
import com.hushquiet.mailclient.Activities.Interfaces.IFragmentSend;
import com.hushquiet.mailclient.Activities.Interfaces.OnFragmentInteractionListener;
import com.hushquiet.mailclient.Cryptography.DSA;
import com.hushquiet.mailclient.Cryptography.DesEncrypter;
import com.hushquiet.mailclient.Cryptography.RSA;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Helpers.MailBox;
import com.hushquiet.mailclient.Helpers.MyMessage;
import com.hushquiet.mailclient.Helpers.User;
import com.hushquiet.mailclient.Mail.Mail;
import com.hushquiet.mailclient.Mail.SendMailTask;
import com.hushquiet.mailclient.Mail.Settings;
import com.hushquiet.mailclient.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.app.Activity.RESULT_OK;

public class SendFragment extends Fragment implements IFragmentSend {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;

    private EditText editTextTo;
    private EditText editTextBody;
    private EditText editTextSubject;
    private EditText editTextKey;
    private Button buttonSend;
    private Button buttonAdd;
    private ImageView imageView;
    private MailBox mailBox;
    private MyMessage message;
    private LinearLayout layout;
    private LinearLayout layoutHorizontal;
    private Bitmap bitmap;
    private String filePath = null;

    private Settings settings;
    private User user;

    private OnFragmentInteractionListener mListener;

    public SendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendFragment newInstance(String param1, String param2) {
        SendFragment fragment = new SendFragment();
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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_send, container, false);
        editTextBody = (EditText)root.findViewById(R.id.editTextBody);
        editTextTo = (EditText)root.findViewById(R.id.editTextTo);
        editTextSubject = (EditText)root.findViewById(R.id.editTextSubject);
        editTextKey = (EditText)root.findViewById(R.id.editTextKey);
        editTextKey.setVisibility(View.INVISIBLE);
        buttonSend = (Button)root.findViewById(R.id.buttonSend);
        mailBox = DB.getInstance(root.getContext()).getCurrentMailbox();
        buttonAdd = (Button)root.findViewById(R.id.buttonAdd);
        context = root.getContext();
        layout = (LinearLayout)root.findViewById(R.id.linerLayoutSend);
        final IFragmentSend fragmentSend = this;
        DB db = DB.getInstance(context);
        settings = new Settings(db.getSettings(db.getAuthUserID()));
        user = new User(db.getUser(db.getAuthUserID()));
        if (settings.crypt) {
            editTextKey.setVisibility(View.VISIBLE);
            Button button = new Button(context);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!editTextTo.getText().toString().equals("")) {
                        try {
                            new SendMailTask(getActivity(), fragmentSend).execute(
                                    editTextTo.getText(),
                                    "Ключ",
                                    "Мой ключ для RSA шифрования: " +
                                            RSA.publicKeyToString(RSA.getPublicKey(user.publicKeyRSA)),
                                    mailBox.email,
                                    mailBox.password,
                                    filePath
                            );
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Ошибка")
                                .setMessage("Требуется ввести получателя")
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
                }
            });
            layout.addView(button);
        }

        if (message != null) {
            editTextTo.setText(message.to);
            editTextBody.setText(message.body);
            editTextSubject.setText(message.subject);

            if (message.bitmap != null) {
                new Thread(new Runnable() {
                    public void run() {
                        while (layout.getWidth() == 0) {

                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = new ImageView(context);
                                imageView.setImageBitmap(addBitmap(message.bitmap));
                                layoutHorizontal = new LinearLayout(context);
                                layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                                layoutHorizontal.addView(imageView);
                                layout.addView(layoutHorizontal);
                            }
                        });
                    }
                }).start();
            }
        } else {
            editTextTo.setText("");
            editTextBody.setText("");
            editTextSubject.setText("");
        }

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mailBox != null) {
                    buttonSend.setEnabled(false);
                    byte [] sign = null;
                    try {
                        if (settings.sign)
                            sign = DSA.sign(editTextBody.getText().toString().getBytes(), DSA.getPrivateKey(user.privateKeyDSA));
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    SecretKey secretKey = null;
                    String publicKey = null;
                    String body = editTextBody.getText().toString();
                    String subject = editTextSubject.getText().toString();
                    subject += (settings.sign ? "[SIGNDSA]" : "") + (settings.crypt ? "[CRYPT]" : "");
                    body += (settings.sign ? Base64.encodeToString(user.publicKeyDSA, Base64.DEFAULT) : "") +
                            (settings.sign ? Base64.encodeToString(sign, Base64.DEFAULT) : "");
                    if (settings.crypt) {
                        try {
                            secretKey = KeyGenerator.getInstance("DES").generateKey();
                            DesEncrypter desEncrypter = new DesEncrypter();
                            desEncrypter.setKey(secretKey);
                            body = desEncrypter.encrypt(body);
                            String key = DesEncrypter.keyToString(secretKey);
                            publicKey = editTextKey.getText().toString();
                            publicKey = publicKey.replace(" ", "");
                            key = RSA.encrypt(key, RSA.stringToPublicKey(publicKey));
                            body += "[" + key;
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    }

                    new SendMailTask(getActivity(), fragmentSend).execute(
                            editTextTo.getText(),
                            subject,
                            body,
                            mailBox.email,
                            mailBox.password,
                            filePath
                    );
                } else {
                    Toast.makeText(root.getContext(), "Не выбран текущий ящик!", Toast.LENGTH_LONG);
                }

            }
        });

        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case 1:
            {
                if (resultCode == RESULT_OK)
                {
                    InputStream is = null;
                    try {
                        is = getActivity().getContentResolver().openInputStream(data.getData());
                        bitmap = addBitmap(is);
                        imageView = new ImageView(context);


                        if (layoutHorizontal == null) {
                            imageView.setImageBitmap(bitmap);
                            layoutHorizontal = new LinearLayout(context);
                            layoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                            layoutHorizontal.addView(imageView);
                            layout.addView(layoutHorizontal);

                        } else {
                            layoutHorizontal = (LinearLayout) layout.getChildAt(layout.getChildCount() - 1);
                            imageView = (ImageView)layoutHorizontal.getChildAt(layoutHorizontal.getChildCount() - 1);
                            imageView.setImageBitmap(bitmap);
                            layoutHorizontal.setPadding(20, 5, 20, 5);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutHorizontal.getWidth(), layoutHorizontal.getHeight());
                            params.setMargins(5, 5, 50, 5);
                            layoutHorizontal.setLayoutParams(params);
                        }

                        is.close();
                        final Cursor cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null );
                        cursor.moveToFirst();
                        filePath = getRealPathFromURI(data.getData());
                        cursor.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            }
        }
    }

    private Bitmap addBitmap(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        int scaleHeight = bitmap.getWidth() / (layout.getWidth() / 3);
        return Bitmap.createScaledBitmap(bitmap, layout.getWidth() / 3, bitmap.getHeight() / scaleHeight, false);
    }

    private Bitmap addBitmap(Bitmap bitmap) {
        int scaleHeight = bitmap.getWidth() / (layout.getWidth() / 3);
        return Bitmap.createScaledBitmap(bitmap, layout.getWidth() / 3, bitmap.getHeight() / scaleHeight, false);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Audio.Media.DATA };
        Cursor cursor =  getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    public void addToDrafts () {
        DB db = DB.getInstance(context);
        if (!editTextSubject.getText().toString().equals("") ||
                !editTextBody.getText().toString().equals("") ||
                !editTextTo.getText().toString().equals("") || bitmap != null) {
            try {
                db.addToMessages(editTextSubject.getText().toString(),
                        editTextBody.getText().toString(),
                        mailBox.email,
                        editTextTo.getText().toString(),
                        "",
                        DB.DRAFTS,
                        mailBox.id, filePath != null ?
                        new String []{Mail.getNameFromPath(filePath)} : null,
                        bitmap != null ? Mail.readAllBytes(new FileInputStream(filePath)) : null,
                        0);
            } catch (IOException e) {
                e.printStackTrace();
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

    @Override
    public void setUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonSend.setEnabled(true);
            }
        });
    }

    public void setMessage(MyMessage message) {
        this.message = message;
    }
}
