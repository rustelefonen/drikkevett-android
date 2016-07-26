package rustelefonen.no.drikkevett_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.extra.SourcesActivity;
import rustelefonen.no.drikkevett_android.information.InformationCategoryActivity;
import rustelefonen.no.drikkevett_android.intro.GoalRegistrationActivity;
import rustelefonen.no.drikkevett_android.settings.AlcoholPricingSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.GoalSettingsActivity;
import rustelefonen.no.drikkevett_android.settings.UserSettingsActivity;
import rustelefonen.no.drikkevett_android.tabs.calc.BacCalcFragment;
import rustelefonen.no.drikkevett_android.tabs.dayAfter.BacDayAfterFragment;
import rustelefonen.no.drikkevett_android.tabs.history.BacHistoryFragment;
import rustelefonen.no.drikkevett_android.tabs.home.BacHomeFragment;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.tabs.planParty.BacPlanPartyFragment;
import rustelefonen.no.drikkevett_android.util.NotificationUtil;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String ID = "MainActivity";

    private static final String[] IMAGE_DIALOG = new String[]{"Ta nytt bilde", "Velg bilde", "Avbryt"};
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    public TabLayout tabLayout;

    private User user;

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    public User getUser() {
        return user;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }


    public void selectDrawerItem(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        /*if (itemId == R.id.drawer_view_switch) {
            menuItem.setChecked(false);
            return;
        }*/
        //menuItem.setChecked(true);
        mDrawer.closeDrawers();
        if (itemId == R.id.drawer_view_information) {
            startActivity(new Intent(this, InformationCategoryActivity.class));
        } else if (itemId == R.id.nav_first_fragment) {
            Intent intent = new Intent(this, UserSettingsActivity.class);
            intent.putExtra(UserSettingsActivity.ID, getUser());
            startActivity(intent);
        } else if (itemId == R.id.nav_second_fragment) {
            Intent intent = new Intent(this, AlcoholPricingSettingsActivity.class);
            intent.putExtra(AlcoholPricingSettingsActivity.ID, getUser());
            startActivity(intent);
        } else if (itemId == R.id.nav_third_fragment) {
            Intent intent = new Intent(this, GoalSettingsActivity.class);
            intent.putExtra(GoalSettingsActivity.ID, getUser());
            startActivity(intent);
        } else if (itemId == R.id.nav_fourth_fragment) {
            Intent intent = new Intent(this, SourcesActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_fifth_fragment) {
            Intent intent = new Intent(this, GoalSettingsActivity.class);
            intent.putExtra(GoalSettingsActivity.ID, getUser());
            startActivity(intent);
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);

        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        setupDrawerContent(nvDrawer);

        /*nvDrawer.getMenu().findItem(R.id.drawer_view_switch).setActionView(new Switch(this));
        ((Switch) nvDrawer.getMenu().findItem(R.id.drawer_view_switch).getActionView()).setChecked(NotificationUtil.getSelected(this));
        ((Switch) nvDrawer.getMenu().findItem(R.id.drawer_view_switch).getActionView()).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationUtil.setSelected(getBaseContext(), isChecked);
            }
        });*/

        drawerToggle = setupDrawerToggle();


        fetchUser();

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        //ICONS http://romannurik.github.io/AndroidAssetStudio/icons-actionbar.html#source.type=image&source.space.trim=1&source.space.pad=0&name=ic_action_test_1&theme=dark&color=33b5e5%2C60

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.per_mille);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_playlist_add_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_mood_bad_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_access_time_black_24dp);

        mViewPager.addOnPageChangeListener(this);

        onPageSelected(0);

        if (user != null) {
            String nickname = user.getNickname();
            if (nickname != null) {
                View headerLayout = nvDrawer.getHeaderView(0);
                TextView headerTextView = (TextView) headerLayout.findViewById(R.id.nav_header);
                headerTextView.setText(nickname);
            }
        }

        insertImageIfExists();

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

    @Override
    protected void onResume() {
        super.onResume();
        fetchUser();

        //Litt hack?
        /*nvDrawer.getMenu().getItem(0).setChecked(false);
        nvDrawer.getMenu().getItem(1).setChecked(false);
        nvDrawer.getMenu().getItem(2).setChecked(false);
        nvDrawer.getMenu().getItem(3).setChecked(false);*/

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        } else if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    private static final int SELECT_IMAGE = 987;

    private void onLaunchGallery(View view) {
        Intent getImageFromGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(getImageFromGalleryIntent, SELECT_IMAGE);
    }

    private void copyFile(File sourceFile, File destFile) {
        if (!sourceFile.exists()) return;

        FileChannel source;
        FileChannel destination;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            if (source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            destination.close();
        } catch (IOException ignored) {}
    }

    public void onLaunchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            Uri takenPhotoUri = getPhotoFileUri(photoFileName);
            Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
            ImageView ivPreview = (ImageView) findViewById(R.id.profile_image);
            ivPreview.setImageBitmap(takenImage);
        } else if (requestCode == 987) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            String picturePath = null;
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            }
            if (picturePath != null) copyFile(new File(picturePath), new File(getPhotoFileUri(photoFileName).getPath()));
        }
    }

    public Uri getPhotoFileUri(String fileName) {
        if (!isExternalStorageAvailable()) return null;
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public void openImageDialog(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profilbilde")
                .setItems(IMAGE_DIALOG, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) onLaunchCamera(view);
                        else if (which == 1) onLaunchGallery(view);
                    }
                });
        builder.create().show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        String title = position == 0 ? "Hjem"
                : position == 1 ? "Promillekalkulator"
                : position == 2 ? "Planlegg kvelden"
                : position == 3 ? "Dagen Derpå"
                : position == 4 ? "Historikk"
                : "";

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
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

    private void insertImageIfExists() {
        Uri photoToInsert = getPhotoFileUri(photoFileName);
        File file = new File(photoToInsert.getPath());

        if (file.exists()) {
            View headerLayout = nvDrawer.getHeaderView(0);
            Bitmap takenImage = BitmapFactory.decodeFile(photoToInsert.getPath());
            ((ImageView) headerLayout.findViewById(R.id.nav_header_image)).setImageBitmap(takenImage);
        } else {
            System.out.println("Bildet eksisterer ikke");
        }
    }
}
