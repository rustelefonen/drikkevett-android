package rustelefonen.no.drikkevett_android.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

/**
 * Created by simenfonnes on 15.07.2016.
 */

public class AlcoholPricingSettingsActivity extends AppCompatActivity {

    public static final String ID = "AlcoholPricingSettings";

    public EditText beerPriceEditText;
    public EditText winePriceEditText;
    public EditText drinkPriceEditText;
    public EditText shotPriceEditText;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alcohol_price_settings_layout);
        insertUserIfExists();
        initWidgets();
        insertToolbar();
        fillWidgets();
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Alkoholpriser");

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

    private void insertUserIfExists() {
        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) user = (User) tmpUser;
    }

    private void initWidgets() {
        beerPriceEditText = (EditText) findViewById(R.id.alco_settings_beer_edit_text);
        winePriceEditText = (EditText) findViewById(R.id.alco_settings_wine_edit_text);
        drinkPriceEditText = (EditText) findViewById(R.id.alco_settings_drink_edit_text);
        shotPriceEditText = (EditText) findViewById(R.id.alco_settings_shot_edit_text);
    }

    private void fillWidgets() {
        beerPriceEditText.setText(Integer.toString(user.getBeerPrice()));
        winePriceEditText.setText(Integer.toString(user.getWinePrice()));
        drinkPriceEditText.setText(Integer.toString(user.getDrinkPrice()));
        shotPriceEditText.setText(Integer.toString(user.getShotPrice()));
    }

    public void setDefault(View view) {
        beerPriceEditText.setText(Integer.toString(0));
        winePriceEditText.setText(Integer.toString(0));
        drinkPriceEditText.setText(Integer.toString(0));
        shotPriceEditText.setText(Integer.toString(0));
    }

    private int getInt(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void hasChanged() {
        int beerPrice = getInt(beerPriceEditText.getText().toString());
        int winePrice = getInt(winePriceEditText.getText().toString());
        int drinkPrice = getInt(drinkPriceEditText.getText().toString());
        int shotPrice = getInt(shotPriceEditText.getText().toString());

        if (beerPrice != user.getBeerPrice() || winePrice != user.getWinePrice() ||
                drinkPrice != user.getDrinkPrice() || shotPrice != user.getShotPrice()) {
            new AlertDialog.Builder(this)
                    .setTitle("Endringer oppdaget")
                    .setMessage("Er du sikker på at du vil gå tilbake? Endringene vil ikke bli lagret.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            goBack();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private void goBack() {
        super.onBackPressed();
    }

    public void saveNewPrices(View view) {
        int beerPrice = getInt(beerPriceEditText.getText().toString());
        if (beerPrice <= 0) {
            Toast.makeText(this, "Ugyldig øl-pris", Toast.LENGTH_SHORT).show();
            return;
        }

        int winePrice = getInt(winePriceEditText.getText().toString());
        if (winePrice <= 0) {
            Toast.makeText(this, "Ugyldig vin-pris", Toast.LENGTH_SHORT).show();
            return;
        }

        int drinkPrice = getInt(drinkPriceEditText.getText().toString());
        if (drinkPrice <= 0) {
            Toast.makeText(this, "Ugyldig drink-pris", Toast.LENGTH_SHORT).show();
            return;
        }

        int shotPrice = getInt(shotPriceEditText.getText().toString());
        if (shotPrice <= 0) {
            Toast.makeText(this, "Ugyldig shot-pris", Toast.LENGTH_SHORT).show();
            return;
        }

        saveUser(beerPrice, winePrice, drinkPrice, shotPrice);

        Toast.makeText(this, "Endringene ble lagret", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void saveUser(int beerPrice, int winePrice, int drinkPrice, int shotPrice) {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();

        List<User> users = userDao.queryBuilder().list();
        if (users.size() <= 0) return;

        User userToEdit = users.get(0);
        userToEdit.setBeerPrice(beerPrice);
        userToEdit.setWinePrice(winePrice);
        userToEdit.setDrinkPrice(drinkPrice);
        userToEdit.setShotPrice(shotPrice);

        userDao.update(userToEdit);

        superDao.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        hasChanged();
    }
}
