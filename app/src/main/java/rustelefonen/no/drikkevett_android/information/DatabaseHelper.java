package rustelefonen.no.drikkevett_android.information;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by simenfonnes on 01.08.2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "hap.db";
    private static final int DATABASE_VERSION = 1;
    private boolean shouldUpgrade;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        shouldUpgrade = true;
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        handleOnCreateAndOnUpgrade(true);
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        handleOnCreateAndOnUpgrade(false);
        return super.getReadableDatabase();
    }

    private void handleOnCreateAndOnUpgrade(boolean writeable){
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        boolean dbExists = dbFile.exists();

        if(writeable)super.getWritableDatabase().close(); //force create the db file to get write permission at path, and flip the shouldUpgrade bool if needed
        else super.getReadableDatabase().close();

        if(!dbExists || shouldUpgrade) copyPrePopulatedDbTo(dbFile.getAbsolutePath());
    }

    private void copyPrePopulatedDbTo(String dbPath){
        try{
            InputStream assetDB = context.getAssets().open("databases/"+DATABASE_NAME);
            OutputStream appDB = new FileOutputStream(dbPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = assetDB.read(buffer)) > 0) {
                appDB.write(buffer, 0, length);
            }

            shouldUpgrade = false;
            appDB.flush();
            appDB.close();
            assetDB.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}