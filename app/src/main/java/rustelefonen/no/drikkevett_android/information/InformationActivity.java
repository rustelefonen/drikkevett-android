package rustelefonen.no.drikkevett_android.information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.Information;

/**
 * Created by simenfonnes on 18.07.2016.
 */

public class InformationActivity extends AppCompatActivity {
    public static final String ID = "Information";

    private Information information;

    //Widgets
    public TextView informationNameTextView;
    public TextView informationContentTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_layout);

        Object tmpInformation = getIntent().getSerializableExtra(ID);
        if (tmpInformation != null && tmpInformation instanceof Information) {
            information = (Information) tmpInformation;
        }
        initWidgets();
        fillWidgets();
    }

    private void initWidgets() {
        informationNameTextView = (TextView) findViewById(R.id.information_name_text_view);
        informationContentTextView = (TextView) findViewById(R.id.information_content_text_view);
    }

    private void fillWidgets() {
        informationNameTextView.setText(information.getName());
        informationContentTextView.setText(information.getContent());
    }
}
