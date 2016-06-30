package rustelefonen.no.drikkevett_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by simenfonnes on 30.06.2016.
 */

public class Settings extends AppCompatActivity {

    /*private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        setTitle("Innstillinger");

        TextView textView = (TextView) findViewById(R.id.person_name);
        textView.setText("Brukerinnstillinger");


        ImageView imageView = (ImageView) findViewById(R.id.person_photo);
        imageView.setImageResource(R.drawable.test);

        /*mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        String[] lol = new String[]{"Brukerinnstillinger", "Kostnader", "Makspromille", "Veiledning", "Varslinger"};
        mAdapter = new MyAdapter(lol);
        mRecyclerView.setAdapter(mAdapter);*/
    }

    public void test(View view) {
        startActivity(new Intent(this, UserInfo.class));
    }
}
