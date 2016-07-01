package rustelefonen.no.drikkevett_android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import rustelefonen.no.drikkevett_android.db.DaoMaster;
import rustelefonen.no.drikkevett_android.db.DaoSession;
import rustelefonen.no.drikkevett_android.db.User;
import rustelefonen.no.drikkevett_android.db.UserDao;

public class MainActivity extends AppCompatActivity {

    //Fields
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static final String[] IMAGE_DIALOG = new String[]{"Ta nytt bilde", "Velg bilde", "Avbryt"};
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

    public ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.bac_home_frag, null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //profileImage = (ImageView) relativeLayout.findViewById(R.id.profile_image);

        /*Uri photoToInsert = getPhotoFileUri(photoFileName);

        String dir = photoToInsert.getPath().replace("/photo.jpg", "");
        File photoFolder = new File(dir);

        System.out.println("alle filer");

        System.out.println(photoToInsert.getPath());
        for (File file : photoFolder.listFiles()) {
            System.out.println(file.getName());
        }

        if (photoToInsert != null) {

            System.out.println("har bilde der: " + hasImageAtPath(photoToInsert.getPath()));

            Uri takenPhotoUri = getPhotoFileUri(photoFileName);

            Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
            System.out.println("bitmap count: " + takenImage.getByteCount());
            //profileImage.setImageBitmap(takenImage);

            ImageView ivPreview = (ImageView) relativeLayout.findViewById(R.id.profile_image);
            ivPreview.setImageBitmap(takenImage);
            System.out.println("Prøver å sette inn bilde");
        }*/
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Settings.class));
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch(position){
                case 0:
                    setTitle("Hjem");
                    return new BacHomeFragment();
                case 1:
                    setTitle("Promillekalkulator");
                    return new BacCalcFragment();
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
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    private void dbThings() {
        String DB_NAME = "my-db";
        SQLiteDatabase db;

        SQLiteDatabase.CursorFactory cursorFactory = null;
        final DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DB_NAME, cursorFactory);
        db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        UserDao userDao = daoSession.getUserDao();

        User newUser = new User();
        newUser.setAge(14);

        userDao.insert(newUser);

        List<User> userList = userDao.queryBuilder().list();

        for (User user : userList) {
            System.out.println(user.getAge());
        }
    }
}
