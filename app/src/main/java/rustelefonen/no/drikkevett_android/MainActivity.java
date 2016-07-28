package rustelefonen.no.drikkevett_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.extra.SourcesActivity;
import rustelefonen.no.drikkevett_android.extra.guidance.Guidance;
import rustelefonen.no.drikkevett_android.information.InformationCategoryActivity;
import rustelefonen.no.drikkevett_android.information.QuestionActivity;
import rustelefonen.no.drikkevett_android.settings.AlcoholPricingSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.GoalSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.UserSettingsActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.util.ImageUtil;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String ID = "MainActivity";
    private static final String[] IMAGE_DIALOG = new String[]{"Ta nytt bilde", "Velg bilde", "Avbryt"};
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int SELECT_IMAGE = 987;
    private static final int FIRST_TAB_INDEX = 0;
    public String photoFileName = "photo.jpg";

    public TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public Toolbar toolbar;

    private User user;

    public FloatingActionMenu floatingActionMenu;

    private int currentViewpagerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_lol);

        initWidgets();
        setupToolbar();
        setupNavigationDrawer();
        setupViewpager();
        onPageSelected(FIRST_TAB_INDEX);
        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        } else if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        currentViewpagerPosition = position;
        if (position == 0) {
            floatingActionMenu.hideMenu(true);
        } else if (position == 1) {
            floatingActionMenu.showMenu(true);
        } else if (position == 2) {
            floatingActionMenu.showMenu(true);
        } else if (position == 3) {
            floatingActionMenu.showMenu(true);
        } else if (position == 4) {
            floatingActionMenu.hideMenu(true);
        }



        String title = position == 0 ? "Hjem" : position == 1 ? "Promillekalkulator"
                : position == 2 ? "Planlegg kvelden" : position == 3 ? "Dagen Derpå"
                : position == 4 ? "Historikk" : "";

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5 &&
                keyCode == KeyEvent.KEYCODE_BACK &&
                event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Bilde ble ikke tatt!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == 987) {
            if (data == null) return;
            Uri selectedImage = data.getData();
            if (selectedImage == null) return;
            String picturePath = getGalleryPath(selectedImage);
            if (picturePath == null) return;
            Uri newFileUri = ImageUtil.getPhotoFileUri(photoFileName, this);
            if (newFileUri == null) return;
            String newFilePath = newFileUri.getPath();
            if (newFilePath == null) return;
            ImageUtil.copyFile(new File(picturePath), new File(newFilePath));
        }
    }

    private void fetchData() {
        fetchUser();
        insertUserNameIfExists();
        insertImageIfExists();
    }

    private void insertUserNameIfExists() {
        if (user == null) return;
        String nickname = user.getNickname();
        if (nickname == null) return;
        setNavigationDrawerHeaderText(nickname);
    }

    private void setNavigationDrawerHeaderText(String nickname) {
        View headerLayout = navigationView.getHeaderView(0);
        if (headerLayout == null) return;
        TextView headerTextView = (TextView) headerLayout.findViewById(R.id.nav_header);
        if (headerTextView == null) return;
        headerTextView.setText(nickname);
    }

    private void insertImageIfExists() {
        Uri photoToInsert = ImageUtil.getPhotoFileUri(photoFileName, this);
        if (photoToInsert == null) return;
        if (!new File(photoToInsert.getPath()).exists()) return;
        setNavigationDrawerHeaderImage(photoToInsert);
    }

    private void setNavigationDrawerHeaderImage(Uri photoToInsert) {
        View headerLayout = navigationView.getHeaderView(0);
        if (headerLayout == null) return;
        Bitmap takenImage = BitmapFactory.decodeFile(photoToInsert.getPath());
        if (takenImage == null) return;
        ImageView imageView = (ImageView) headerLayout.findViewById(R.id.nav_header_image);
        if (imageView == null) return;
        imageView.setImageBitmap(takenImage);
    }

    public User getUser() {
        return user;
    }

    private void selectDrawerItem(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        drawerLayout.closeDrawers();
        Intent intent = null;
        if (itemId == R.id.drawer_view_information) intent = new Intent(this, InformationCategoryActivity.class);
        else if (itemId == R.id.nav_first_fragment) {
            intent = new Intent(this, UserSettingsActivity.class);
            intent.putExtra(UserSettingsActivity.ID, getUser());
        } else if (itemId == R.id.nav_second_fragment) {
            intent = new Intent(this, AlcoholPricingSettingsActivity.class);
            intent.putExtra(AlcoholPricingSettingsActivity.ID, getUser());
        } else if (itemId == R.id.nav_third_fragment) {
            intent = new Intent(this, GoalSettingsActivity.class);
            intent.putExtra(GoalSettingsActivity.ID, getUser());
        } else if (itemId == R.id.nav_fourth_fragment) intent = new Intent(this, SourcesActivity.class);
        else if (itemId == R.id.nav_fifth_fragment) {
            intent = new Intent(this, Guidance.class);
        }
        if (intent != null) startActivity(intent);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void setTabLayoutIcons() {
        TabLayout.Tab homeTab = tabLayout.getTabAt(FIRST_TAB_INDEX);
        if (homeTab != null) homeTab.setIcon(R.drawable.ic_home_black_24dp);

        TabLayout.Tab calcTab = tabLayout.getTabAt(1);
        if (calcTab != null) calcTab.setIcon(R.drawable.per_mille);

        TabLayout.Tab planPartyTab = tabLayout.getTabAt(2);
        if (planPartyTab != null) planPartyTab.setIcon(R.drawable.ic_playlist_add_black_24dp);

        TabLayout.Tab dayAfterTab = tabLayout.getTabAt(3);
        if (dayAfterTab != null) dayAfterTab.setIcon(R.drawable.ic_mood_bad_black_24dp);

        TabLayout.Tab historyTab = tabLayout.getTabAt(4);
        if (historyTab != null) historyTab.setIcon(R.drawable.ic_access_time_black_24dp);
    }

    private void fetchUser() {
        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        superDao.close();

        if (users.size() > 0) {
            User tmpUser = users.get(0);
            if (tmpUser != null) user = tmpUser;
        }
    }

    private void initWidgets() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nvView);
    }

    private void launchGallery() {
        Intent getImageFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(getImageFromGalleryIntent, SELECT_IMAGE);
    }

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtil.getPhotoFileUri(photoFileName, this));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private String getGalleryPath(Uri imagePath) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(imagePath, filePathColumn, null, null, null);
        if (cursor == null || cursor.getColumnCount() <= 0) return null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    public void openImageDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
        builder.setTitle("Profilbilde")
                .setItems(IMAGE_DIALOG, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) launchCamera();
                        else if (which == 1) launchGallery();
                    }
                });
        builder.create().show();
    }

    private void setupNavigationDrawer() {
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        setupDrawerContent(navigationView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupViewpager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setTabLayoutIcons();

        viewPager.addOnPageChangeListener(this);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public int getCurrentViewpagerPosition() {
        return currentViewpagerPosition;
    }
}