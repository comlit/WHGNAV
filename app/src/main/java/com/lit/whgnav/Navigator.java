package com.lit.whgnav;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class Navigator extends AppCompatActivity
{
    //Initiierung der Variablen
    private Spinner destination, current;
    private Button btnOk, locationSet, showMap;
    private Room rDestination, rCurrent;
    private NavEngine nav = new NavEngine();
    private double longitude, latitude;
    private int floor;
    private ArrayList<String> pathDescription;
    private ArrayList<Room> spinnerRooms = new ArrayList<>();
    private TextView description;
    public RadioButton floorChecked;
    private boolean tourSdestin, tourScur, tourBtnLoc, tourBtnOk;
    private FloatingActionButton fab;
    private TourGuide mTourGuideHandler;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private ProgressBar pb;


    //Methode wird ausgeführt wenn der Navigator ausgeführt wird
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Konstruktor der Oberklasse wird ausgeführt
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        //Buttons und Spinner werden den xml-Objekten zugeordnet
        destination = findViewById(R.id.destination);
        current = findViewById(R.id.current);
        btnOk = findViewById(R.id.ok);
        locationSet = findViewById(R.id.location);
        description = findViewById(R.id.descriptionTv);
        showMap = findViewById(R.id.showOnMap);
        fab = findViewById(R.id.fabN);
        pb = findViewById(R.id.progress);
        tourBtnLoc = false;
        tourBtnOk = false;
        tourScur = false;
        tourSdestin = false;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }
        };

        if (ContextCompat.checkSelfPermission(Navigator.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
        fillSpinner();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        final boolean firstStart = prefs.getBoolean("firstStartNav", true);

        if (firstStart) {
            showcurTour();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStartNav", false);
            editor.apply();
        }

        //Abfrage ob Bestätigen-Button gedrückt wurde
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            //Methode wird ausgeführt wenn Button gedrückt wird
            @Override
            public void onClick(View v)
            {
                confirm();
            }
        });

        //Abfrage ob Lokalisierungs-Button gedrückt wurde
        locationSet.setOnClickListener(new View.OnClickListener()
        {
            //Methode wird ausgeführt wenn Button gedrückt wird
            @Override
            public void onClick(View v)
            {
                localise();
            }
        });

        //Abfrage ob Auf-Karte-Anzeigen-Button gedrückt wurde
        showMap.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showOnMap();
            }
        });

        fab.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v)
            {
                if (tourScur) {
                    mTourGuideHandler.cleanUp();
                    tourScur = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    showdestinTour();
                } else if (tourSdestin) {
                    tourSdestin = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    mTourGuideHandler.cleanUp();
                    showlocTour();
                } else if (tourBtnLoc) {
                    tourBtnLoc = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    mTourGuideHandler.cleanUp();
                    showokTour();
                } else if (tourBtnOk) {
                    tourBtnOk = false;
                    fab.setVisibility(View.INVISIBLE);
                    fab.setClickable(false);
                    mTourGuideHandler.cleanUp();
                }
            }
        });
    }

    /**
     * Füllen der Spinner mit der Raumliste
     */
    public void fillSpinner()
    {
        //Wenn hier Änderungen gemacht werden dann auch bei NavEngine machen
        spinnerRooms.add(nav.getRooms().get(0));
        spinnerRooms.add(nav.getRooms().get(53));
        spinnerRooms.add(nav.getRooms().get(126));
        spinnerRooms.add(nav.getRooms().get(143));
        spinnerRooms.add(nav.getRooms().get(127));
        spinnerRooms.add(nav.getRooms().get(128));
        spinnerRooms.add(nav.getRooms().get(129));
        spinnerRooms.add(nav.getRooms().get(64));
        spinnerRooms.addAll(nav.getRooms().subList(28, 49));
        spinnerRooms.addAll(nav.getRooms().subList(80, 98));
        spinnerRooms.add(nav.getRooms().get(120));
        spinnerRooms.add(nav.getRooms().get(123));
        spinnerRooms.addAll(nav.getRooms().subList(106, 108));
        spinnerRooms.addAll(nav.getRooms().subList(1, 7));
        spinnerRooms.addAll(nav.getRooms().subList(10, 16));
        spinnerRooms.addAll(nav.getRooms().subList(19, 25));
        spinnerRooms.addAll(nav.getRooms().subList(56, 64));
        spinnerRooms.addAll(nav.getRooms().subList(68, 73));
        spinnerRooms.addAll(nav.getRooms().subList(74, 79));
        spinnerRooms.add(nav.getRooms().get(108));
        spinnerRooms.remove(nav.getRooms().get(89));
        spinnerRooms.remove(nav.getRooms().get(39));

        final ArrayAdapter<Room> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerRooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        current.setAdapter(adapter);
        destination.setAdapter(adapter);
    }

    /**
     * App wechselt zu Map-Activity
     */
    public void showOnMap()
    {
        Intent intent = new Intent(Navigator.this, Karte.class);
        intent.putStringArrayListExtra("pathList", pathDescription);
        startActivity(intent);
    }

    /**
     * Aktuell ausgewählte Räume werden an die Navengine weitergegeben und die Wegbeschreibung wird ausgegeben
     */
    public void confirm()
    {
        //Variablen werden mit aktuell ausgewählten Räumen gefuüllt
        rDestination = getselected(destination);
        rCurrent = getselected(current);

        //Wenn die Dummie-Objekte ausgewählt werden wird ein Toast ausgegeben und die Variablen werden wieder auf null gesetzt
        if (rDestination.getRaumId().equals("Bitte Raum auswählen") || rCurrent.getRaumId().equals("Bitte Raum auswählen")) {
            Toast.makeText(Navigator.this, "Bitte wählen Sie einen Raum aus", Toast.LENGTH_LONG).show();
            rDestination = null;
            rCurrent = null;
        } else {
            if (!(rDestination == rCurrent)) {
                //Ausgwählte Räume werden an NavEngine weitergeben
                pb.setVisibility(View.VISIBLE);
                pathDescription = nav.findShortestPath(rCurrent, rDestination);
                pb.setVisibility(View.INVISIBLE);

                //Beschreibung des kürzesten Wegs wird Angezeigt
                description.setText(pathDescriptionToString());

                //Buuton zum öffnen der Karte wird angezeigt
                showMap.setVisibility(View.VISIBLE);
                showMap.setClickable(true);
            } else {
                Toast.makeText(Navigator.this, "Bitte wählen verschiedene Räume aus", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Nächster Raum wird abgefragt und im Spinner an die erste Stelle gesetzt
     */
    public void localise()
    {
        //Abfrage ob die App auf die GPS-Daten zugreifen kann
        if (ContextCompat.checkSelfPermission(Navigator.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();

            AlertDialog.Builder builder = new AlertDialog.Builder(Navigator.this);
            builder.setTitle("Bitte Etage Auswählen");

            final View floorPopupLayout = getLayoutInflater().inflate(R.layout.floorpupup, null);
            builder.setView(floorPopupLayout);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    RadioGroup rd = floorPopupLayout.findViewById(R.id.radioGroup);
                    if (rd.getCheckedRadioButtonId() != 0) {
                        floorChecked = floorPopupLayout.findViewById(rd.getCheckedRadioButtonId());
                        switch ((String) floorChecked.getText()) {
                            case "2. Obergeschoss":
                                floor = 3;
                                break;
                            case "1. Obergeschoss":
                                floor = 2;
                                break;
                            case "Erdgeschoss":
                                floor = 1;
                                break;
                            case "1. Untergeschoss":
                                floor = 0;
                                break;

                        }
                        setClosest();
                    }


                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            ActivityCompat.requestPermissions(Navigator.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /**
     * Weg-Liste wird zu String konvertiert
     * TODO: N ausgabe vom Weg nur debug tool vor release entfernen
     *
     * @return Wegliste als String
     */
    public String pathDescriptionToString()
    {
        String ausgabe = "Von ";

        for (int i = 0; i < pathDescription.size() - 1; i++) {
            if (pathDescription.get(i).contains("UG") && pathDescription.get(i + 1).contains("EG") || pathDescription.get(i).contains("EG") && pathDescription.get(i + 1).contains("1.OG") || pathDescription.get(i).contains("1.OG") && pathDescription.get(i + 1).contains("2.OG")) {
                ausgabe += pathDescription.get(i) + " die Treppe nach oben in Richtung \n";
            } else {
                ausgabe += pathDescription.get(i) + " in Richtung \n";
            }


        }

        ausgabe += pathDescription.get(pathDescription.size() - 1);

        return ausgabe;
    }

    /**
     * Ausgewählter Raum wird zurückgeben
     *
     * @param pSpinner Spinner dessen ausgewähltes Objekt ausgegeben werden soll
     * @return ausgewähltes Raumobjekt
     */
    private Room getselected(Spinner pSpinner)
    {
        return (Room) pSpinner.getSelectedItem();
    }

    /**
     * Nächster Raum wird zurückgeben
     *
     * @return Raumobejkt des nächsten Raums
     */
    public Room getClosest()
    {
        //Koordinaten und Liste der Räume werden an NavEngine weitergegeben. NavEngine gib den nächsten Raum aus
        return nav.getClosestRoom(longitude, latitude, floor);
    }

    /**
     * Findet den nächsten Raum mithilfe der Navengine und setzt ihn an den anfang des Spinners
     */
    public void setClosest()
    {
        //Abfrage ob ein Raum ausgewählt wurde
        if (getClosest() != null) {
            current.setSelection(spinnerRooms.indexOf(getClosest()));

            //debug
            Toast.makeText(Navigator.this," Lat: "+ String.valueOf(latitude)+" Long: "+String.valueOf(longitude)+"Floor: "+String.valueOf(floor), Toast.LENGTH_LONG).show();
        } else {
            //Benutzer wird aufgefordert einen Raum auszuwählen
            Toast.makeText(Navigator.this, "Das Handy konnte nicht lokalisiert werden.\nBitte versuchen Sie es später erneut.", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("RestrictedApi")
    private void showcurTour()
    {
        tourScur = true;
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
                        .setTitle("Start")
                        .setDescription(getString(R.string.tour_current_spinner)))
                .setOverlay(ov)
                .playOn(current);
    }

    @SuppressLint("RestrictedApi")
    private void showokTour()
    {
        tourBtnOk = true;
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        Overlay ov = new Overlay();
        ov.mDisableClick = false;
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle("Bestätigen")
                        .setDescription(getString(R.string.tour_ok_button)))
                .setOverlay(ov)
                .playOn(btnOk);
    }

    @SuppressLint("RestrictedApi")
    private void showdestinTour()
    {
        tourSdestin = true;
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
                        .setTitle("Ziel")
                        .setDescription(getString(R.string.tour_destin_spinner)))
                .setOverlay(ov)
                .playOn(destination);
    }

    @SuppressLint("RestrictedApi")
    private void showlocTour()
    {
        tourBtnLoc = true;
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        Overlay ov = new Overlay();
        ov.mDisableClick = false;
        mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip()
                        .setTitle("Lokalisieren")
                        .setDescription(getString(R.string.tour_loc_button)))
                .setOverlay(ov)
                .playOn(locationSet);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (ContextCompat.checkSelfPermission(Navigator.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    /**
     * Wird automatisch aufgerufen wenn Berechtigung erfragt wird(Parameter werden vom Android-System weitergegeben)
     *
     * @param requestCode  Code des Abfrage(in diesem Falle immer 1 da nur eine Berechtigung erfragt wird)
     * @param permissions  Art der Berechtigung
     * @param grantResults Gibt an ob Berechtigung erteilt wurde oder nicht
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
             {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startLocationUpdates();
                    localise();
                }
                else
                {
                    Toast.makeText(Navigator.this, "Bitte lassen Sie die App auf die Standortinformationen zugreifen", Toast.LENGTH_LONG).show();
                }
             }
        }
    }
}