package rustelefonen.no.drikkevett_android.information;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rustelefonen.no.drikkevett_android.R;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by simenfonnes on 26.07.2016.
 */

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String SERVER_URL = "http://www.rustelefonen.no/";
    private static final String QUESTIONS_URL = "http://www.rustelefonen.no/besvarte-sporsmal-og-svar/";

    public Spinner ageSpinner;
    public Spinner genderSpinner;
    public Spinner countySpinner;
    public TextView questionLink;
    public Button submitButton;
    public TextInputEditText titleInput;
    public TextInputEditText contentInput;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);

        ageSpinner = (Spinner) findViewById(R.id.age_list);
        genderSpinner = (Spinner) findViewById(R.id.gender_list);
        countySpinner = (Spinner) findViewById(R.id.county_list);
        titleInput = (TextInputEditText) findViewById(R.id.title_input);
        contentInput = (TextInputEditText) findViewById(R.id.content_input);
        questionLink = (TextView) findViewById(R.id.info_link);
        submitButton = (Button) findViewById(R.id.submit_form_btn);


        ageSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_layout, makeAgeList()));
        genderSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_layout, getResources().getStringArray(R.array.genders)));
        countySpinner.setAdapter(new ArrayAdapter<>(this, R.layout.spinner_layout, getResources().getStringArray(R.array.counties)));

        questionLink.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        insertToolbar();

    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Send spørsmål");

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

    private List<String> makeAgeList(){
        List<String> ageList = new ArrayList<>(58);
        ageList.add("Velg alder");

        for(int i = 13; i < 70; i++){
            ageList.add(""+i);
        }
        return ageList;
    }

    public void sendForm() {
        if (!validateForm()) return;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sender spørsmål!");
        progressDialog.show();
        sendFormRequest(ageSpinner.getSelectedItem().toString(),
                genderSpinner.getSelectedItem().toString(),
                countySpinner.getSelectedItem().toString(),
                titleInput.getText().toString(),
                contentInput.getText().toString() + "\n\nSendt fra Android-applikasjonen.");
    }

    public void loadQuestionsSite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(QUESTIONS_URL));
        startActivity(intent);
    }

    private boolean validateForm() {
        if (ageSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Du må oppgi alder", LENGTH_SHORT).show();
        }
        else if(genderSpinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Du må oppgi kjønn", LENGTH_SHORT).show();
        }
        else if(countySpinner.getSelectedItemPosition() == 0){
            Toast.makeText(this, "Du må velge et fylke", LENGTH_SHORT).show();
        }
        else if (titleInput.getText().length() <= 0){
            titleInput.setError("Fyll inn tittel");
        }
        else if (contentInput.getText().length() <= 0){
            contentInput.setError("Skriv inn et spørsmål");
        }
        else{
            return true;
        }

        return false;
    }

    public void sendFormRequest(String age, String gender , String county, String title, String content) {
        QuestionRemote questionRemote = new Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(QuestionRemote.class);


        Call<ResponseBody> call = questionRemote.postQuestion(age, gender, county, title, content, "5", "17", "Send inn ditt spørsmål!");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(QuestionActivity.this, "Skjemaet er nå innsendt!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(QuestionActivity.this, "Noe gikk galt, prøv igjen!", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.info_link) loadQuestionsSite();
        else if (id == R.id.submit_form_btn) sendForm();
    }

    public interface QuestionRemote {
        @FormUrlEncoded
        @POST("/still-sporsmal")
        Call<ResponseBody> postQuestion(@Field("user-submitted-age") String age,
                                        @Field("user-submitted-sex") String gender,
                                        @Field("user-submitted-county") String county,
                                        @Field("user-submitted-title") String title,
                                        @Field("user-submitted-content") String content,
                                        @Field("user-submitted-captcha") String captcha,
                                        @Field("user-submitted-category") String category,
                                        @Field("user-submitted-post") String post);
    }

    private void hasChanged() {
        super.onBackPressed();
    }
}
