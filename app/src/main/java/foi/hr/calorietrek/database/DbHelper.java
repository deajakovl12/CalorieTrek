package foi.hr.calorietrek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
    private static DbHelper sInstance;

    public static final String DATABASE_NAME = "CalorieTrek.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_USER = "user";
    public static final String COL_ID_USER = "id_user";
    public static final String COL_NAME = "name_surname";
    public static final String COL_WEIGHT = "weight";

    public static synchronized DbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + "(" +
                COL_ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " VARCHAR(100) NOT NULL, " +
                COL_WEIGHT + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USER);
        onCreate(db);
    }

    public void insertUser(String nameSurname, String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, nameSurname);
        values.put(COL_WEIGHT, weight);

        long result = db.insert(TABLE_USER, null, values);
    }

    public boolean existingUser(String nameSurname){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM user WHERE name_surname = '" + nameSurname + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0){
            cursor.close();
            db.close();
            insertUser(nameSurname, "55");
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public String returnWeight(String nameSurname){
        String result = "";

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT weight FROM user WHERE name_surname = '" + nameSurname + "' LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("weight"));
        }

        cursor.close();
        db.close();

        return result;
    }

    public boolean updateWeight(String nameSurname, String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE user SET weight = '" + weight + "' WHERE name_surname = '" + nameSurname + "'";
        db.execSQL(query);
        return true;
    }



}
