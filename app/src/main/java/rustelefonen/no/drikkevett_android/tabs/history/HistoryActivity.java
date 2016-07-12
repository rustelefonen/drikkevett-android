package rustelefonen.no.drikkevett_android.tabs.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.History;

/**
 * Created by simenfonnes on 12.07.2016.
 */

public class HistoryActivity extends AppCompatActivity {

    public static final String ID = "History";

    private History history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_layout);

        Object tmpHistory = getIntent().getSerializableExtra(ID);

        if (tmpHistory != null && tmpHistory instanceof History) {
            history = (History) tmpHistory;
            System.out.println(history.getHighestBAC());
        }
    }
}
