package rustelefonen.no.drikkevett_android.extra.guidance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioGroup;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.extra.guidance.fragments.GuideScrollAdapter;

/**
 * Created by RUStelefonen on 26.07.2016.
 */

public class Guidance extends AppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private ViewPager guideScroll;
    private RadioGroup pageIndicatorGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidance);
        initWidgets();
        setListeners();
        fillWidgets();
        insertToolbar();
        pageIndicatorGroup.check(pageIndicatorGroup.getChildAt(0).getId());
    }

    private void setListeners() {
        pageIndicatorGroup.setOnCheckedChangeListener(this);
        guideScroll.addOnPageChangeListener(this);
    }

    private void fillWidgets(){
        guideScroll.setAdapter(new GuideScrollAdapter(getSupportFragmentManager()));
        guideScroll.setCurrentItem(0);
    }

    private void initWidgets(){
        guideScroll = (ViewPager) findViewById(R.id.guide_scroll_plan_party);
        pageIndicatorGroup = (RadioGroup) findViewById(R.id.page_indicator_radio_guidance);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 0) pageIndicatorGroup.check(R.id.radio_one_G);
        else if (position == 1) pageIndicatorGroup.check(R.id.radio_two_G);
        else if (position == 2) pageIndicatorGroup.check(R.id.radio_three_G);
        else if (position == 3) pageIndicatorGroup.check(R.id.radio_four_G);
        else if (position == 4) pageIndicatorGroup.check(R.id.radio_five_G);
        else if (position == 5) pageIndicatorGroup.check(R.id.radio_six_G);
        else if (position == 6) pageIndicatorGroup.check(R.id.radio_seven_G);
        else if (position == 7) pageIndicatorGroup.check(R.id.radio_eight_G);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int i) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.radio_one_G) guideScroll.setCurrentItem(0);
        else if (id == R.id.radio_two_G) guideScroll.setCurrentItem(1);
        else if (id == R.id.radio_three_G) guideScroll.setCurrentItem(2);
        else if (id == R.id.radio_four_G) guideScroll.setCurrentItem(3);
        else if (id == R.id.radio_five_G) guideScroll.setCurrentItem(4);
        else if (id == R.id.radio_six_G) guideScroll.setCurrentItem(5);
        else if (id == R.id.radio_seven_G) guideScroll.setCurrentItem(6);
        else if (id == R.id.radio_eight_G) guideScroll.setCurrentItem(7);
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Veiledning");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasChanged();
            }
        });
    }

    private void hasChanged() {
        super.onBackPressed();
    }
}
