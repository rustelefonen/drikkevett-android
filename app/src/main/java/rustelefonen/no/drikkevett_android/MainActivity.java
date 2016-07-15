package rustelefonen.no.drikkevett_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;
import rustelefonen.no.drikkevett_android.settings.UserSettingsActivity;
import rustelefonen.no.drikkevett_android.tabs.calc.BacCalcFragment;
import rustelefonen.no.drikkevett_android.tabs.dayAfter.BacDayAfterFragment;
import rustelefonen.no.drikkevett_android.tabs.history.BacHistoryFragment;
import rustelefonen.no.drikkevett_android.tabs.home.BacHomeFragment;
import rustelefonen.no.drikkevett_android.tabs.home.SuperDao;
import rustelefonen.no.drikkevett_android.tabs.planParty.BacPlanPartyFragment;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String ID = "MainActivity";

    //Fields
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static final String[] IMAGE_DIALOG = new String[]{"Ta nytt bilde", "Velg bilde", "Avbryt"};
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    public TabLayout tabLayout;

    public ImageView profileImage;

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
        Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                intent = new Intent(this, UserSettingsActivity.class);
                intent.putExtra(UserSettingsActivity.ID, getUser());
                break;
            case R.id.nav_second_fragment:
                intent = new Intent(this, UserSettingsActivity.class);
                break;
            case R.id.nav_third_fragment:
                intent = new Intent(this, UserSettingsActivity.class);
                break;
            default:
                intent = new Intent(this, UserSettingsActivity.class);
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();

        startActivity(intent);
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

        drawerToggle = setupDrawerToggle();

        SuperDao superDao = new SuperDao(this);
        UserDao userDao = superDao.getUserDao();
        List<User> users = userDao.queryBuilder().list();
        superDao.close();

        if (users.size() <= 0) {
            System.out.println("Ingen brukere...");
        } else {
            System.out.println("userCount: " + users.size());
        }
        User tmpUser = users.get(0);
        if (tmpUser == null) {
            System.out.println("Brukern er null");
        } else {
            System.out.println("Brukern er ikke null");
        }
        user = tmpUser;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_battery_charging_full_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_list_black_24dp);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_mood_bad_black_24dp);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_access_time_black_24dp);

        mViewPager.addOnPageChangeListener(this);

        onPageSelected(0);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private boolean hasImageAtPath (String path) {
        File file = new File(path);
        String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};

        for (String extension : okFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
            return true;
        } else if (id == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLaunchGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(intent, 0);
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
        } else if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

            }
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
        String title = position == 0 ? "Home"
                : position == 1 ? "Promillekalkulator"
                : position == 2 ? "Planlegg kvelden"
                : position == 3 ? "Dagen Derpå"
                : position == 4 ? "Historikk"
                : "";

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0: return new BacHomeFragment();
                case 1: return new BacCalcFragment();
                case 2: return new BacPlanPartyFragment();
                case 3: return new BacDayAfterFragment();
                case 4: return new BacHistoryFragment();
                default: return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() { return 5; }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

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


}
