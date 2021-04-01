package com.lit.whgnav;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;

public class Intro extends AppIntro
{
    //TODO: N Schrieb halt was in die Anfangs-Activiy https://www.paypal.me/fabianuhlit

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance(
                "Welcome...",
                "This is the first slide of the example"
        ));
        addSlide(AppIntroFragment.newInstance(
                "...Let's get started!",
                "This is the last slide, I won't annoy you more :)"
        ));
    }

    @Override
    public void onSkipPressed(Fragment currentFragment)
    {
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment)
    {
        finish();
    }
}