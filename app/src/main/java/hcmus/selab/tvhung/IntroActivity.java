package hcmus.selab.tvhung;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SampleSlide().newInstance(R.layout.slide_01));
        addSlide(new SampleSlide().newInstance(R.layout.slide_02));
        addSlide(new SampleSlide().newInstance(R.layout.slide_03));

    }
        @Override
        public void onSkipPressed(Fragment currentFragment) {
            super.onSkipPressed(currentFragment);
            // Do something when users tap on Skip button.
            startShopping();
        }

        @Override
        public void onDonePressed(Fragment currentFragment) {
            super.onDonePressed(currentFragment);
            // Do something when users tap on Done button.
            startShopping();
        }

    private void startShopping() {
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
    }

    @Override
        public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
            super.onSlideChanged(oldFragment, newFragment);
            // Do something when the slide changes.
        }
    }