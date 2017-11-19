package com.hushquiet.mailclient.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hushquiet.mailclient.Activities.Interfaces.ICallBackMessage;
import com.hushquiet.mailclient.Activities.Interfaces.IFragment;
import com.hushquiet.mailclient.Activities.Interfaces.IFragmentSend;
import com.hushquiet.mailclient.Activities.Interfaces.OnFragmentInteractionListener;
import com.hushquiet.mailclient.DB.DB;
import com.hushquiet.mailclient.Helpers.MyMessage;
import com.hushquiet.mailclient.Mail.Mail;
import com.hushquiet.mailclient.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractionListener, ICallBackMessage {

    private DB db;
    private Fragment fragmentInbox, fragmentDeleted, fragmentDrafts,
            fragmentMailboxes, fragmentSend, fragmentSettings,
            fragmentMessage, fragmentSent;
    private Fragment fragment;
    private FloatingActionButton fab;
    public static final int DRAFTSFRAGMENT = 0;
    public static final int INBOXFRAGMENT = 1;
    public static final int DELETEDFRAGMENT = 2;
    public static final int SENTFRAGMENT = 3;

    private void initFragment() {
        Class fragmentClass = null;

        try {
            fragmentClass = InboxFragment.class;
            fragmentInbox = (Fragment)fragmentClass.newInstance();

            fragmentClass = DeletedFragment.class;
            fragmentDeleted = (Fragment)fragmentClass.newInstance();

            fragmentClass = DraftsFragment.class;
            fragmentDrafts = (Fragment)fragmentClass.newInstance();

            fragmentClass = MailboxesFragment.class;
            fragmentMailboxes = (Fragment)fragmentClass.newInstance();

            fragmentClass = SendFragment.class;
            fragmentSend = (Fragment)fragmentClass.newInstance();

            fragmentClass = SettingsFragment.class;
            fragmentSettings = (Fragment)fragmentClass.newInstance();

            fragmentClass = MessageFragment.class;
            fragmentMessage = (Fragment)fragmentClass.newInstance();

            fragmentClass = SentFragment.class;
            fragmentSent = (Fragment)fragmentClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Mail.activity = this;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = fragmentSend;
                setFragment("Отправка сообщения");
                ((SendFragment)fragment).setMessage(null);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        db = DB.getInstance(getApplicationContext());
        initFragment();
        fragment = fragmentInbox;
                setFragment("Входящие");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.reload) {
            ((IFragment)fragment).reload();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Создадим новый фрагмент

        int id = item.getItemId();

        if (id == R.id.nav_inbox) {
            fragment = fragmentInbox;
        } else if (id == R.id.nav_sent) {
            fragment = fragmentSent;
        } else if (id == R.id.nav_drafts) {
            fragment = fragmentDrafts;
        } else if (id == R.id.nav_deleted) {
            fragment = fragmentDeleted;
        } else if (id == R.id.nav_settings) {
            fragment = fragmentSettings;
        } else if (id == R.id.nav_logout) {
            if (db.deleteAuthUser()) {
                Intent intent = new Intent();
                intent.setClass(this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
        } else if (id == R.id.nav_mailboxes) {
            fragment = fragmentMailboxes;
        }

        try {
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                item.setChecked(true);
                setTitle(item.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(String title) {
        try {
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                // Выделяем выбранный пункт меню в шторке
                setTitle(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFragmentInteraction(int state) {

    }

    @Override
    public Cursor getPath(Uri uri) {
       /* final Cursor cursor = getContentResolver().query( uri, null, null, null, null );
        cursor.moveToFirst();
        return cursor;*/
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void setMessageFragment(MyMessage m, int state) {
        switch (state) {
            case DRAFTSFRAGMENT:
                Class fragmentClass = SendFragment.class;
                try {
                    Fragment fr = (Fragment)fragmentClass.newInstance();
                    fragment = fr;
                    setFragment("Черновик");
                    ((SendFragment)fr).setMessage(m);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            case DELETEDFRAGMENT:
                fragment = fragmentMessage;
                setFragment("Сообщение");
                ((MessageFragment)fragment).setMessage(m);
                break;
            case INBOXFRAGMENT:
                fragment = fragmentMessage;
                setFragment("Сообщение");
                ((MessageFragment)fragment).setMessage(m);
                break;
            case SENTFRAGMENT:
                fragment = fragmentMessage;
                setFragment("Отправленные");
                ((MessageFragment)fragment).setMessage(m);
                break;
        }
    }
}
