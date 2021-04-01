package com.lit.whgnav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import java.util.ArrayList;

public class Karte extends AppCompatActivity
{

    private ArrayList<int[]> ug1, eg, og1, og2;
    private ArrayList<String> pathDescription;
    private ArrayList<Room> pathRooms;
    private ArrayList<String> etagen;
    private NavEngine nav;
    private Spinner etagenSpinner;
    private CustomView custom;
    private ArrayList<int[]> roomsxy;
    private int[] startEndFloor;
    private boolean tourSfloor;
    private FloatingActionButton fab;
    private TourGuide mTourGuideHandler;

    //TODO: NN Zoomable custom view

    //TODO: NN Live Standort auf Karte

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        pathDescription = new ArrayList<>();
        if(getIntent().hasExtra("pathList"))
        {
            pathDescription = getIntent().getExtras().getStringArrayList("pathList");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte);
        etagenSpinner = findViewById(R.id.etage);
        custom = findViewById(R.id.custo);
        etagen = new ArrayList<>();
        nav = new NavEngine();
        roomsxy = new ArrayList<>();
        pathRooms = new ArrayList<>();
        og2 = new ArrayList<>();
        og1 = new ArrayList<>();
        eg = new ArrayList<>();
        ug1 = new ArrayList<>();
        startEndFloor = new int[2];
        tourSfloor = false;
        fab = findViewById(R.id.fabO);


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStartOv", true);

        if (firstStart)
        {
            showfloorTour();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStartOv", false);
            editor.apply();
        }

        fab.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v)
            {
                if(tourSfloor)
                {
                    mTourGuideHandler.cleanUp();
                    tourSfloor = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                }

            }
        });


        spinnerInit();
        listinit();
        custom.setxyPos(ug1, eg, og1, og2, startEndFloor);

        etagenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                int hFloor = 0;
                switch (etagenSpinner.getSelectedItem().toString())
                {
                    case("2. Obergeschoss"):
                        hFloor = 3;
                        break;

                    case("1. Obergeschoss"):
                        hFloor = 2;
                        break;

                    case("Erdgeschoss"):
                        hFloor = 1;
                        break;

                    case("1. Untergeschoss"):
                        hFloor = 0;
                        break;
                }
                custom.floorChoice(hFloor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

    }

    @SuppressLint("RestrictedApi")
    private void showfloorTour()
    {
        tourSfloor = true;
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        Overlay ov = new Overlay();
        ov.mDisableClick = false;
        Pointer ptn = new Pointer();
        ptn.mGravity = Gravity.LEFT;
        ptn.setColor(Color.rgb(46, 125, 191));
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(ptn)
                .setToolTip(new ToolTip()
                        .setTitle("Etagen")
                        .setDescription(getString(R.string.tour_floor_spinner)))
                .setOverlay(ov)
                .playOn(etagenSpinner);
    }

    /**
     * Füllt den Spinner
     */
    public void spinnerInit()
    {
        etagen.add("2. Obergeschoss");
        etagen.add("1. Obergeschoss");
        etagen.add("Erdgeschoss");
        etagen.add("1. Untergeschoss");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(Karte.this, android.R.layout.simple_spinner_item, etagen);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etagenSpinner.setAdapter(adapter);
    }

    /**
     *
     */
    public void listinit()
    {
        double untenlinksx = 7.617975;
        double untenlinksy = 51.946686;
        double faktorY = 847457.6271186440677966101;
        double faktorX = 419450.054373155196520118;
        ArrayList<Room> ug1rooms, egrooms, og1rooms, og2rooms;
        ug1rooms = new ArrayList<>();
        egrooms = new ArrayList<>();
        og1rooms = new ArrayList<>();
        og2rooms = new ArrayList<>();
        int j = 0;
        startEndFloor[0] = 1;
        for(int i = 0; i< pathDescription.size(); i++)
        {


            while(!pathDescription.get(i).equals(nav.getRooms().get(j).toString()))
            {
                j++;
            }

            if(i==0)
            {
                startEndFloor[0]=nav.getRooms().get(j).getFloor();
            }

            if(i== pathDescription.size()-1)
            {
                startEndFloor[1]=nav.getRooms().get(j).getFloor();
            }

            switch (nav.getRooms().get(j).getFloor())
            {
                case (0):
                    addroom(i, j, ug1rooms);
                    break;
                case (1):
                    addroom(i, j, egrooms);
                    break;
                case (2):
                    addroom(i, j, og1rooms);
                    break;
                case (3):
                    addroom(i, j, og2rooms);
                    break;
            }
            j=0;
        }
        ug1 = roomlistToArrayList(ug1rooms);
        eg = roomlistToArrayList(egrooms);
        og1 = roomlistToArrayList(og1rooms);
        og2 = roomlistToArrayList(og2rooms);
        if(startEndFloor == null)
        {
            etagenSpinner.setSelection(2);
        }
        else
        {
            etagenSpinner.setSelection(3-startEndFloor[0]);
        }
    }

    public void addroom(int i, int j, ArrayList<Room> pList)
    {
        if(pList.size()>0)
        {
            if(!pList.get(pList.size()-1).isConnectedTo(nav.getRooms().get(j)))
            {
                pList.add(null);
                pList.add(nav.getRooms().get(j));
            }
            else
            {
                pList.add(nav.getRooms().get(j));
            }
        }
        else
        {
            pList.add(nav.getRooms().get(j));
        }
        int k = 0;
        Room hRoom;
        if(i<(pathDescription.size()-1))
        {
            while(!pathDescription.get(i+1).equals(nav.getRooms().get(k).toString()))
            {
                k++;
            }
            hRoom = nav.getRooms().get(k);

            if(hRoom.getFloor()>nav.getRooms().get(j).getFloor())
            {
                nav.getRooms().get(j).setRelativeToNextRoom(2);
            }
            else if(hRoom.getFloor()<nav.getRooms().get(j).getFloor())
            {
                nav.getRooms().get(j).setRelativeToNextRoom(1);
            }
            else
            {
                nav.getRooms().get(j).setRelativeToNextRoom(0);
            }
        }
        else
        {
            nav.getRooms().get(j).setRelativeToNextRoom(0);
        }
    }

    public ArrayList<int[]> roomlistToArrayList(ArrayList<Room> pRoomlist)
    {
        ArrayList<int[]> returns = new ArrayList<>();
        for(int i= 0; i<pRoomlist.size();i++)
        {
            if(pRoomlist.get(i)!=null)
            {
                //og2.add(new int[]{(int)(900-((og2rooms.get(i).getLatitude()-untenlinksy)*faktorY)), (int)(((og2rooms.get(i).getLongitude()-untenlinksx)*faktorX))});
                returns.add(new int[]{pRoomlist.get(i).getCords()[0], pRoomlist.get(i).getCords()[1], pRoomlist.get(i).getRelativeToNextRoom()});
            }
            else
            {
                returns.add(null);
            }
        }
        return returns;
    }
}
