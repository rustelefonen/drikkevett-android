package rustelefonen.no.drikkevett_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import rustelefonen.no.drikkevett_android.information.QuestionActivity;

/**
 * Created by simenfonnes on 31.07.2016.
 */

public class ContactActivity extends AppCompatActivity {

    public Button questionButton;
    public Button callButton;

    private static final String NUMBER = "08588";
    private static final String TITLE = "Kontakt oss";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout);
        questionButton = (Button) findViewById(R.id.contact_send_question);
        callButton = (Button) findViewById(R.id.contact_call);
        setupToolbar();
    }

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + NUMBER));
        startActivity(intent);
    }

    public void openQuestionPage(View view) {
        Intent intent = new Intent(this, QuestionActivity.class);
        startActivity(intent);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle(TITLE);

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
