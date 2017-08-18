package rustelefonen.no.drikkevett_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.extra.guidance.Guidance;
import rustelefonen.no.drikkevett_android.extra.sources.SourcesActivity;
import rustelefonen.no.drikkevett_android.information.InformationCategoryActivity;
import rustelefonen.no.drikkevett_android.settings.AlcoholPricingSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.GoalSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.UserSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.WhoWarningActivity;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.unit.UnitEditActivity;
import rustelefonen.no.drikkevett_android.util.ImageUtil;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String ID = "MainActivity";
    private static final String[] IMAGE_DIALOG = new String[]{"Ta nytt bilde", "Velg bilde", "Avbryt"};
    private final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private static final int SELECT_IMAGE = 987;
    private static final int FIRST_TAB_INDEX = 0;
    private String photoFileName = "photo.jpg";
    private User user;
    private int currentViewpagerPosition;

    //Widgets
    public TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public Toolbar toolbar;
    public NonSwipeableViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentViewpagerPosition = position;
        EventBus.getDefault().post(new SelectedPageEvent(position));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        String title = position == 0 ? "Hjem" : position == 1 ? "Promillekalkulator"
                : position == 2 ? "Drikkeepisode" : position == 3 ? "Historikk" : "";
        actionBar.setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5 && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
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
        System.out.println("fetcher...");
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

        if (photoToInsert == null) return;
        Bitmap decoded = ImageUtil.decodeSampledBitmapFromResource(this, photoToInsert, 100, 100);

        ImageView imageView = (ImageView) headerLayout.findViewById(R.id.nav_header_image);
        if (imageView == null) return;
        imageView.setImageBitmap(decoded);
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
            intent = new Intent(this, UnitEditActivity.class);
        } else if (itemId == R.id.nav_fourth_fragment) intent = new Intent(this, SourcesActivity.class);
        else if (itemId == R.id.nav_fifth_fragment) {
            intent = new Intent(this, Guidance.class);
        } else if (itemId == R.id.drawer_view_contact) {
            intent = new Intent(this, ContactActivity.class);
        } else if (itemId == R.id.nav_sixth_fragment) {
            intent = new Intent(this, WhoWarningActivity.class);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 987);
        } else {
            galleryIntent();
        }
    }

    public void launchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) cameraIntent();
                else checkCameraPermission(permissions, grantResults);
                return;
            }
            case 987: {
                checkGalleryPermission(permissions, grantResults);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) galleryIntent();
                return;
            }
        }
    }

    private void checkCameraPermission(String permissions[], int[] grantResults) {
        for (int i = 0, len = permissions.length; i < len; i++) {

            String permission = permissions[i];

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale( permission );
                }
                if (! showRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                    builder.setTitle("Mangler tillatelse til kamera")
                            .setMessage("For å få tilgang til kameraet må du tillate dette i innstillingene. Ønsker du å gå dit nå?")
                            .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, PackageManager.PERMISSION_GRANTED);
                                }
                            })
                            .setNegativeButton("AVBRYT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
            }
        }
    }

    private void checkGalleryPermission(String [] permissions, int[] grantResults) {
        for (int i = 0, len = permissions.length; i < len; i++) {

            String permission = permissions[i];

            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = false;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    showRationale = shouldShowRequestPermissionRationale( permission );
                }
                if (! showRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
                    builder.setTitle("Mangler tillatelse til galleriet")
                            .setMessage("For å få tilgang til galleriet må du tillate dette i innstillingene. Ønsker du å gå dit nå?")
                            .setPositiveButton("JA", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, PackageManager.PERMISSION_GRANTED);
                                }
                            })
                            .setNegativeButton("AVBRYT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .show();
                }
            }
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, ImageUtil.getPhotoFileUri(photoFileName, this));
        if (intent.resolveActivity(getPackageManager()) == null) return;
        try {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } catch (SecurityException se) {
            se.printStackTrace();
            Toast.makeText(this, "Fikk ikke tilgang til kameraet.", Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryIntent() {
        Intent getImageFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(getImageFromGalleryIntent, SELECT_IMAGE);
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
        viewPager = (NonSwipeableViewPager) findViewById(R.id.container);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_nav_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.bottom_nav_bac_calc:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.bottom_nav_drink_episode:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.bottom_nav_history:
                        viewPager.setCurrentItem(3);
                }
                return false;
            }
        });

        //tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(viewPager);

        //setTabLayoutIcons();

        viewPager.addOnPageChangeListener(this);

    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.textColor));
    }

    public int getCurrentViewpagerPosition() {
        return currentViewpagerPosition;
    }
}