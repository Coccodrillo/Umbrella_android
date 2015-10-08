package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;

import org.secfirst.umbrella.fragments.TourSlideFragment;
import org.secfirst.umbrella.util.TourViewPager;

public class TourActivity extends BaseActivity implements TourViewPager.OnSwipeOutListener, TourSlideFragment.OnNavigateToMainListener {

    private static final int NUM_PAGES = 5;
    private TourViewPager mPager;
    private CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Button skipBtn = (Button) findViewById(R.id.btn_skip);
        mPager = (TourViewPager) findViewById(R.id.myCustomViewPager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnSwipeOutListener(this);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mIndicator.setCurrentItem(position);
                if (position + 1 == NUM_PAGES) {
                    mPager.setChildId(1);
                } else {
                    mPager.setChildId(0);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 4 && positionOffsetPixels == 0) {
                    skipBtn.setVisibility(View.VISIBLE);
                } else if (position != 4) {
                    skipBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.set_termsAccepted(true);
                navigateToMain();
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tour;
    }

    @Override
    public void onSwipeOutAtEnd() {
        global.set_termsAccepted(true);
        navigateToMain();
    }

    @Override
    public void onNavigationRequested() {
        global.set_termsAccepted(true);
        navigateToMain();
    }

    public void navigateToMain() {
        Intent toMain = new Intent(this, MainActivity.class);
        toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toMain);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return TourSlideFragment.create(position, global);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}