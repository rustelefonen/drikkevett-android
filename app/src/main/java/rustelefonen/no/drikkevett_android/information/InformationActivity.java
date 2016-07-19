package rustelefonen.no.drikkevett_android.information;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
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
    public ImageView informationImageView;

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
        informationImageView = (ImageView) findViewById(R.id.information_image);
    }

    private void fillWidgets() {
        informationNameTextView.setText(information.getName());
        informationContentTextView.setText(information.getContent());


        byte[] image = information.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image , 0, image.length);
        if (bitmap != null) informationImageView.setImageBitmap(bitmap);
        else System.out.println("bitmap er null");
    }
}
