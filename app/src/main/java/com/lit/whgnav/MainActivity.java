package com.lit.whgnav;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;



public class MainActivity extends AppCompatActivity
{

    private Button btnOv, btnNav;
    private WebView web;
    private TourGuide mTourGuideHandler;
    private boolean tourNav, tourOv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        tourNav = false;
        tourOv = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        web = findViewById(R.id.webv);
        btnOv = findViewById(R.id.btnToOv);
        btnNav = findViewById(R.id.btnToNav);
        fab = findViewById(R.id.fab);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStartMain", true);

        if (firstStart)
        {
            startActivity(new Intent(MainActivity.this, Intro.class));
            showNavTour();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStartMain", false);
            editor.apply();
        }

        web.loadUrl("https://www.wilhelm-hittorf-gymnasium.de/aktuelles/#content");
        //web.scrollTo(0, 200);

        btnOv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, Karte.class);
                startActivity(intent);
            }
        });

        btnNav.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    Intent intent = new Intent(MainActivity.this, Navigator.class);
                    startActivity(intent);
            }
        });

        fab.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view)
            {
                if(tourNav)
                {
                    mTourGuideHandler.cleanUp();
                    tourNav = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    showOvTour();
                }
                else if(tourOv)
                {
                    tourOv = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    mTourGuideHandler.cleanUp();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //TODO: NN Darkmode
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.impressum:
                startActivity(new Intent(MainActivity.this, Impressum.class));
                return true;
            case R.id.app_bar_switch:
                if(item.isChecked())
                {
                    item.setChecked(false);
                    //sharedprefs
                }
                else
                {
                    item.setChecked(true);
                    //sharedprefs
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("RestrictedApi")
    public void showNavTour()
    {
        tourNav = true;
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        Overlay ov = new Overlay();
        ov.mDisableClick = false;
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle("Willkommen")
                        .setDescription(getString(R.string.tour_nav_button)))
                .setOverlay(ov)
                .playOn(btnNav);
    }

    @SuppressLint("RestrictedApi")
    public void showOvTour()
    {
        tourOv = true;
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        Overlay ov = new Overlay();
        ov.mDisableClick = false;
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle("Übersicht")
                        .setDescription(getString(R.string.tour_overview_button)))
                .setOverlay(ov)
                .playOn(btnOv);
    }
}
