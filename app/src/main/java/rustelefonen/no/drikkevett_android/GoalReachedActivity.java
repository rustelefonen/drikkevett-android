package rustelefonen.no.drikkevett_android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.settings.GoalSettingsActivity;
import rustelefonen.no.drikkevett_android.tabs.home.BacHomeFragment;
import rustelefonen.no.drikkevett_android.tabs.home.BarChartController;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;

import static android.app.PendingIntent.getActivity;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalAverageHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalHighestBac;

/**
 * Created by RUStelefonen on 21.07.2016.
 */

public class GoalReachedActivity extends Activity {

    private TextView txtViewCosts, txtViewHighestBAC, txtViewHighestAverageBAC, txtViewGreeting;
    private BarChart historyBarChart;
    private Button btnScreenshot, btnRestart, btnContinue;
    public static final String ID = "GoalReached";

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reached_goal_date);


        initWidets();

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }

        fillWidgets();
        haveUserSetNewGoal();

        btnScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //captureScreenshot();
            }
        });
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartSettings();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueWithNewDate();
            }
        });
    }

    private List<History> getHistoryList() {
        SuperDao superDao = new SuperDao(GoalReachedActivity.this);
        HistoryDao historyDao = superDao.getHistoryDao();
        List<History> historyList = historyDao.queryBuilder().list();
        superDao.close();
        return historyList;
    }

    private void fillWidgets(){
        List<History> historyList = getHistoryList();
        txtViewCosts.setText(getTotalCost(historyList) + ",-");
        txtViewHighestBAC.setText(getTotalHighestBac(historyList) + "");
        txtViewHighestAverageBAC.setText(getTotalAverageHighestBac(historyList) + "");
        txtViewGreeting.setText(didUserReachGoal(getTotalAverageHighestBac(historyList), user) + "");

        BarChartController chartController = new BarChartController(historyBarChart, user, getHistoryList());
        chartController.setData();
        chartController.styleBarChart();
    }

    private void captureScreenshot(){
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void restartSettings(){
        Intent intent = new Intent(this, GoalSettingsActivity.class);
        intent.putExtra(GoalSettingsActivity.ID, user);
        startActivity(intent);
    }

    private void continueWithNewDate(){
        Intent intent = new Intent(this, GoalSettingsActivity.class);
        intent.putExtra(GoalSettingsActivity.ID, user);
        startActivity(intent);
    }

    private void initWidets(){
        txtViewCosts = (TextView) findViewById(R.id.costs_txtView_goalReached);
        txtViewHighestBAC = (TextView) findViewById(R.id.highestBAC_txtView_goalReached);
        txtViewHighestAverageBAC = (TextView) findViewById(R.id.highestAverageBAC_txtView_goalReached);
        txtViewGreeting = (TextView) findViewById(R.id.txtViewGreeting_goalReached);
        historyBarChart = (BarChart) findViewById(R.id.goal_reached_bar_chart);
        btnScreenshot = (Button) findViewById(R.id.btnScreenshot_GR);
        btnRestart = (Button) findViewById(R.id.btnRestart_GR);
        btnContinue = (Button) findViewById(R.id.btnContinue_GR);
    }

    private String didUserReachGoal(double avergeBAC, User user){
        String output = "";
        if(user.getGoalBAC() <= avergeBAC){
            output = "Gratulerer sjef!";
        } else {
            output = "Måldato nådd!";
        }
        return output;
    }

    private void haveUserSetNewGoal(){
        /*
        Date currentDate = new Date();
        if(user.getGoalDate().after(currentDate)){
            finish();
        }*/
    }

    @Override
    public void onBackPressed() {}
}
