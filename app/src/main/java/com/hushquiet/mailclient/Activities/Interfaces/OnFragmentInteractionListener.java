package com.hushquiet.mailclient.Activities.Interfaces;

import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Алексей on 09.11.2017.
 */

public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(int state);
    public Cursor getPath(Uri uri);
}
