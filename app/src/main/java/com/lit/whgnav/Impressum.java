package com.lit.whgnav;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class Impressum extends AppCompatActivity
{
    //TODO: N schreib halt was rein (spenden) https://www.paypal.me/fabianuhlit

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum);
        tv = findViewById(R.id.textViewImp);
        tv.setText(getString(R.string.impressumSchule));
    }
}
