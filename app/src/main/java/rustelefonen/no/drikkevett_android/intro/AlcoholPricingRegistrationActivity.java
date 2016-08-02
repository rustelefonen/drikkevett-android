package rustelefonen.no.drikkevett_android.intro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.InputFilterMinMax;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.User;

/**
 * Created by simenfonnes on 13.07.2016.
 */

public class AlcoholPricingRegistrationActivity extends AppCompatActivity {

    public static final String ID = "AlcoholPricingRegistration";

    public EditText beerEditText;
    public EditText wineEditText;
    public EditText drinkEditText;
    public EditText shotEditText;

    private User user;

    private static final int LOWEST_PRICE = 1;
    private static final int HIGHEST_PRICE = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alcohol_pricing_registration_layout);

        InputFilter [] inputFilter = new InputFilter[]{ new InputFilterMinMax("1", "1000")};

        beerEditText = (EditText) findViewById(R.id.alco_reg_beer_edit_text);
        beerEditText.setFilters(inputFilter);
        beerEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        beerEditText.setTypeface(Typeface.DEFAULT);

        wineEditText = (EditText) findViewById(R.id.alco_reg_wine_edit_text);
        wineEditText.setFilters(inputFilter);
        wineEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        wineEditText.setTypeface(Typeface.DEFAULT);

        drinkEditText = (EditText) findViewById(R.id.alco_reg_drink_edit_text);
        drinkEditText.setFilters(inputFilter);
        drinkEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        drinkEditText.setTypeface(Typeface.DEFAULT);

        shotEditText = (EditText) findViewById(R.id.alco_reg_shot_edit_text);
        shotEditText.setFilters(inputFilter);
        shotEditText.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        shotEditText.setTypeface(Typeface.DEFAULT);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }
    }

    private int validateInputText(String inputText) {
        if (inputText.isEmpty()) return -1;
        try {
            return Integer.parseInt(inputText);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public void next(View view) {
        int beerPrice = validateInputText(beerEditText.getText().toString());
        if (beerPrice == -1) {
            Toast.makeText(this, "Du må registrere øl for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (beerPrice < LOWEST_PRICE || beerPrice > HIGHEST_PRICE) {
                Toast.makeText(this, "Ølpris må være mellom 1 og 1000.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setBeerPrice(beerPrice);
            }
        }

        int winePrice = validateInputText(wineEditText.getText().toString());
        if (winePrice == -1) {
            Toast.makeText(this, "Du må registrere vin for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (winePrice < LOWEST_PRICE || winePrice > HIGHEST_PRICE) {
                Toast.makeText(this, "Vinpris må være mellom 1 og 1000.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setWinePrice(winePrice);
            }
        }

        int drinkPrice = validateInputText(drinkEditText.getText().toString());
        if (drinkPrice == -1) {
            Toast.makeText(this, "Du må registrere drink for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (drinkPrice < LOWEST_PRICE || drinkPrice > HIGHEST_PRICE) {
                Toast.makeText(this, "Drinkpris må være mellom 1 og 1000.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setDrinkPrice(drinkPrice);
            }
        }

        int shotPrice = validateInputText(shotEditText.getText().toString());
        if (shotPrice == -1) {
            Toast.makeText(this, "Du må registrere shot for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (shotPrice < LOWEST_PRICE || shotPrice > HIGHEST_PRICE) {
                Toast.makeText(this, "Shotpris må være mellom 1 og 1000.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                user.setShotPrice(shotPrice);
            }
        }

        Intent intent = new Intent(this, GoalRegistrationActivity.class);
        intent.putExtra(GoalRegistrationActivity.ID, user);
        startActivity(intent);
    }

    public void setDefault(View view) {
        beerEditText.setText(Integer.toString(70));
        wineEditText.setText(Integer.toString(65));
        drinkEditText.setText(Integer.toString(110));
        shotEditText.setText(Integer.toString(100));
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
