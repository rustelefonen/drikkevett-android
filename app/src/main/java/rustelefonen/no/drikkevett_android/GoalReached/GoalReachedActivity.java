package rustelefonen.no.drikkevett_android.goalreached;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import rustelefonen.no.drikkevett_android.MainActivity;
import rustelefonen.no.drikkevett_android.R;
import rustelefonen.no.drikkevett_android.db.DayAfterBACDao;
import rustelefonen.no.drikkevett_android.db.GraphHistoryDao;
import rustelefonen.no.drikkevett_android.db.History;
import rustelefonen.no.drikkevett_android.db.HistoryDao;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.tabs.home.BarChartController;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.tabs.planParty.Status;
import rustelefonen.no.drikkevett_android.util.NavigationUtil;

import static android.app.PendingIntent.getActivity;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalAverageHighestBac;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalCost;
import static rustelefonen.no.drikkevett_android.tabs.home.HistoryCalculator.getTotalHighestBac;

/**
 * Created by RUStelefonen on 21.07.2016.
 */

public class GoalReachedActivity extends AppCompatActivity {

    private static final String FILENAME = "drikkevett_screenshot";
    private TextView txtViewCosts, txtViewHighestBAC, txtViewHighestAverageBAC, txtViewGreeting;
    private BarChart historyBarChart;
    private Button btnRestart, btnContinue;
    public static final String ID = "GoalReached";

    private Bitmap mbitmap;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reached_goal_date);
        insertToolbar();
        initWidets();

        Object tmpUser = getIntent().getSerializableExtra(ID);
        if (tmpUser != null && tmpUser instanceof User) {
            user = (User) tmpUser;
        }

        fillWidgets();

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertRestart();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertContinue();
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
        Double formatText = getTotalAverageHighestBac(historyList);
        DecimalFormat numberFormat = new DecimalFormat("#.##");
        txtViewHighestAverageBAC.setText(numberFormat.format(formatText) + "");
        txtViewGreeting.setText(didUserReachGoal(getTotalAverageHighestBac(historyList), user));

        BarChartController chartController = new BarChartController(historyBarChart, user, getHistoryList());
        chartController.setData();
        chartController.styleBarChart();
    }

    public void screenShot(View view) {
        mbitmap = getBitmapOFRootView(btnContinue);
        createImage(mbitmap);
        Toast.makeText(this, "Skjermbilde lagt til i Galleri!", Toast.LENGTH_SHORT).show();
    }

    public Bitmap getBitmapOFRootView(View v) {
        View rootview = v.getRootView();
        rootview.setDrawingCacheEnabled(true);
        Bitmap bitmap1 = rootview.getDrawingCache();
        return bitmap1;
    }

    public void createImage(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File file = new File(Environment.getExternalStorageDirectory() +
                "/capturedscreenandroid2.jpg");
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            File imageFile = file;
            MediaScannerConnection.scanFile(this, new String[] { imageFile.getPath() }, new String[] { "image/jpeg" }, null);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertContinue() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alert_builder.setMessage("Er du sikker på at du vil fortsette? ").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                continueWithNewDate();
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("Fortsett");
        alert.show();
    }

    private void alertRestart() {
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        alert_builder.setMessage("Er du sikker på at du vil slette all historikk og starte på nytt?").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                restartSettings();
            }
        }).setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = alert_builder.create();
        alert.setTitle("Start på nytt");
        alert.show();
    }

    private void restartSettings(){
        // clear history table
        SuperDao superDao = new SuperDao(this);
        HistoryDao historyDao = superDao.getHistoryDao();
        GraphHistoryDao graphHistoryDao = superDao.getGraphHistoryDao();
        graphHistoryDao.deleteAll();
        historyDao.deleteAll();
        superDao.close();

        Intent intent = new Intent(this, SetNewGoalActivity.class);
        intent.putExtra(SetNewGoalActivity.ID, user);
        startActivity(intent);
    }

    private void continueWithNewDate(){
        Intent intent = new Intent(this, SetNewGoalActivity.class);
        intent.putExtra(SetNewGoalActivity.ID, user);
        startActivity(intent);
    }

    private void initWidets(){
        txtViewCosts = (TextView) findViewById(R.id.costs_txtView_goalReached);
        txtViewHighestBAC = (TextView) findViewById(R.id.highestBAC_txtView_goalReached);
        txtViewHighestAverageBAC = (TextView) findViewById(R.id.highestAverageBAC_txtView_goalReached);
        txtViewGreeting = (TextView) findViewById(R.id.txtViewGreeting_goalReached);
        historyBarChart = (BarChart) findViewById(R.id.goal_reached_bar_chart);
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
        Date currentDate = new Date();
        if(user.getGoalDate().after(currentDate)){
            finish();
        }
    }

    private void insertToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
        toolbar.setTitle("Måldato");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.goalreached_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_screenshot:
                final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
                        .findViewById(android.R.id.content)).getChildAt(0);
                screenShot(viewGroup);
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {}
}
