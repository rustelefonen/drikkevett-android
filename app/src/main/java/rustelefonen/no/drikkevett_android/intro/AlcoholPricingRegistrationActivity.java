package rustelefonen.no.drikkevett_android.intro;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.History;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alcohol_pricing_registration_layout);

        beerEditText = (EditText) findViewById(R.id.alco_reg_beer_edit_text);
        wineEditText = (EditText) findViewById(R.id.alco_reg_wine_edit_text);
        drinkEditText = (EditText) findViewById(R.id.alco_reg_drink_edit_text);
        shotEditText = (EditText) findViewById(R.id.alco_reg_shot_edit_text);

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
            System.out.println(user.getAge());
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
            user.setBeerPrice(beerPrice);
        }

        int winePrice = validateInputText(wineEditText.getText().toString());
        if (winePrice == -1) {
            Toast.makeText(this, "Du må registrere vin for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setWinePrice(winePrice);
        }

        int drinkPrice = validateInputText(drinkEditText.getText().toString());
        if (drinkPrice == -1) {
            Toast.makeText(this, "Du må registrere drink for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setDrinkPrice(drinkPrice);
        }

        int shotPrice = validateInputText(shotEditText.getText().toString());
        if (shotPrice == -1) {
            Toast.makeText(this, "Du må registrere shot for å gå videre", Toast.LENGTH_SHORT).show();
            return;
        } else {
            user.setShotPrice(shotPrice);
        }

        System.out.println("Kom seg gjennom!");
        System.out.println(user.getBeerPrice() + " " + user.getWinePrice() + " " + user.getDrinkPrice() + " " + user.getShotPrice() + " ");
    }

    public void setDefault(View view) {
        beerEditText.setText(Integer.toString(0));
        wineEditText.setText(Integer.toString(0));
        drinkEditText.setText(Integer.toString(0));
        shotEditText.setText(Integer.toString(0));
    }
}
