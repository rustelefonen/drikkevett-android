package rustelefonen.no.drikkevett_android.tabs.calc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.calc.fragments.BeerScrollAdapter;
import rustelefonen.no.drikkevett_android.util.BacUtility;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

public class BacCalcFragment extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String PER_MILLE = "\u2030";

    public TextView bacLabel;
    public TextView labelHours;
    public TextView labelBeerNrUnits;
    public TextView labelWineNrUnits;
    public TextView labelDrinkNrUnits;
    public TextView labelShotNrUnits;
    private TextView labelQuotes;

    public Button addButton;
    public Button removeButton;

    private SeekBar seekBar;
    public ViewPager beerScroll;
    public RadioGroup pageIndicatorGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bac_calc_frag, container, false);
        setHasOptionsMenu(true);
        initWidgets(view);
        setListeners();
        fillWidgets();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.bac_calc_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                removeAddedBeverages();
                refreshFragment();
                return false;
            case R.id.action_contact:
                NavigationUtil.navigateToContactInformation(getContext());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    int tryParseInt(String value) {
        try {return Integer.parseInt(value);}
        catch(NumberFormatException ignored) {return 0;}
    }

    double tryParseDouble(String value) {
        try {return Double.parseDouble(value);}
        catch(NumberFormatException ignored) {return 0.0;}
    }

    private void updateBac() {
        double beerUnits = tryParseDouble(labelBeerNrUnits.getText().toString());
        double wineUnits = tryParseDouble(labelWineNrUnits.getText().toString());
        double drinkUnits = tryParseDouble(labelDrinkNrUnits.getText().toString());
        double shotUnits = tryParseDouble(labelShotNrUnits.getText().toString());

        User user = ((MainActivity)getActivity()).getUser();

        boolean gender = user.getGender().equals("Mann");
        double weight = user.getWeight();
        double hours = (double) (seekBar.getProgress() + 1);

        double bac = BacUtility.calculateBac(beerUnits, wineUnits, drinkUnits, shotUnits, hours, gender, weight);

        String formattedBac = new DecimalFormat("#.##").format(bac) + PER_MILLE;
        bacLabel.setText(formattedBac);
        bacLabel.setTextColor(BacUtility.getQuoteTextColorBy(bac));
        labelQuotes.setText(BacUtility.getQuoteTextBy(bac));
        labelQuotes.setTextColor(BacUtility.getQuoteTextColorBy(bac));
    }

    private void initWidgets(View view){
        pageIndicatorGroup = (RadioGroup) view.findViewById(R.id.page_indicator_radio);
        labelHours = (TextView) view.findViewById(R.id.textViewHours);
        seekBar = (SeekBar) view.findViewById(R.id.seekBarBacCalc);
        labelBeerNrUnits = (TextView) view.findViewById(R.id.textViewBeerUnits);
        labelWineNrUnits = (TextView) view.findViewById(R.id.textViewWineUnits);
        labelDrinkNrUnits = (TextView) view.findViewById(R.id.textViewDrinkUnits);
        labelShotNrUnits = (TextView) view.findViewById(R.id.textViewShotUnits);
        bacLabel = (TextView) view.findViewById(R.id.bac_calc_bac_label);
        beerScroll = (ViewPager) view.findViewById(R.id.beer_scroll);
        labelQuotes = (TextView) view.findViewById(R.id.text_view_quotes);
        addButton = (Button) view.findViewById(R.id.bac_calc_add_button);
        removeButton = (Button) view.findViewById(R.id.bac_calc_remove_button);
    }

    private void setListeners() {
        pageIndicatorGroup.setOnCheckedChangeListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        beerScroll.addOnPageChangeListener(this);
        addButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
    }

    private void fillWidgets() {
        beerScroll.setAdapter(new BeerScrollAdapter(this.getChildFragmentManager()));
        beerScroll.setCurrentItem(0);
        pageIndicatorGroup.check(pageIndicatorGroup.getChildAt(0).getId());
        labelBeerNrUnits.setText("0");
        labelWineNrUnits.setText("0");
        labelDrinkNrUnits.setText("0");
        labelShotNrUnits.setText("0");

        updateBac();
    }

    private void removeAddedBeverages() {
        labelBeerNrUnits.setText("0");
        labelWineNrUnits.setText("0");
        labelDrinkNrUnits.setText("0");
        labelShotNrUnits.setText("0");

        seekBar.setProgress(0);
        labelHours.setText("Promillen om 1 time");
        updateBac();
    }

    private void refreshFragment() {
        updateBac();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (position == 0) pageIndicatorGroup.check(R.id.radio_one);
        else if (position == 1) pageIndicatorGroup.check(R.id.radio_two);
        else if (position == 2) pageIndicatorGroup.check(R.id.radio_three);
        else if (position == 3) pageIndicatorGroup.check(R.id.radio_four);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getCheckedRadioButtonId();
        if (id == R.id.radio_one) beerScroll.setCurrentItem(0);
        else if (id == R.id.radio_two) beerScroll.setCurrentItem(1);
        else if (id == R.id.radio_three) beerScroll.setCurrentItem(2);
        else if (id == R.id.radio_four) beerScroll.setCurrentItem(3);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int hours = i + 1;
        String formattedString = hours == 1 ? "Promillen om " + hours + " time" : "Promillen om " + hours + " timer";
        labelHours.setText(formattedString);
        updateBac();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bac_calc_add_button) {
            int index = beerScroll.getCurrentItem();
            boolean wasUpdated = false;

            if (index == 0) {
                int beerUnits = tryParseInt(labelBeerNrUnits.getText().toString());
                if (beerUnits < 20) {
                    String incrementedText = "" + (beerUnits + 1);
                    labelBeerNrUnits.setText(incrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 1) {
                int wineUnits = tryParseInt(labelWineNrUnits.getText().toString());
                if (wineUnits < 20) {
                    String incrementedText = "" + (wineUnits + 1);
                    labelWineNrUnits.setText(incrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 2) {
                int drinkUnits = tryParseInt(labelDrinkNrUnits.getText().toString());
                if (drinkUnits < 20) {
                    String incrementedText = "" + (drinkUnits + 1);
                    labelDrinkNrUnits.setText(incrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 3) {
                int shotUnits = tryParseInt(labelShotNrUnits.getText().toString());
                if (shotUnits < 20) {
                    String incrementedText = "" + (shotUnits + 1);
                    labelShotNrUnits.setText(incrementedText);
                    wasUpdated = true;
                }
            }

            if (wasUpdated) {
                updateBac();
            }
        }
        else if (v.getId() == R.id.bac_calc_remove_button) {
            int index = beerScroll.getCurrentItem();
            boolean wasUpdated = false;

            if (index == 0) {
                int beerUnits = tryParseInt(labelBeerNrUnits.getText().toString());
                if (beerUnits > 0) {
                    String decrementedText = "" + (beerUnits - 1);
                    labelBeerNrUnits.setText(decrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 1) {
                int wineUnits = tryParseInt(labelWineNrUnits.getText().toString());
                if (wineUnits > 0) {
                    String decrementedText = "" + (wineUnits - 1);
                    labelWineNrUnits.setText(decrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 2) {
                int drinkUnits = tryParseInt(labelDrinkNrUnits.getText().toString());
                if (drinkUnits > 0) {
                    String decrementedText = "" + (drinkUnits - 1);
                    labelDrinkNrUnits.setText(decrementedText);
                    wasUpdated = true;
                }
            }
            else if (index == 3) {
                int shotUnits = tryParseInt(labelShotNrUnits.getText().toString());
                if (shotUnits > 0) {
                    String decrementedText = "" + (shotUnits - 1);
                    labelShotNrUnits.setText(decrementedText);
                    wasUpdated = true;
                }
            }

            if (wasUpdated) {
                updateBac();
            }
        }
    }
}