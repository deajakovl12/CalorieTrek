package foi.hr.calorietrek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelperUser extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "user";
    public static final String COL_1 = "id_user";
    public static final String COL_2 = "name_surname";
    public static final String COL_3 = "weight";

    public DbHelperUser(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " VARCHAR(100) NOT NULL, " +
                COL_3 + " INTEGER NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }

    public void insertUser(String nameSurname, String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_2, nameSurname);
        values.put(COL_3, weight);

        long result = db.insert(TABLE_NAME, null, values);
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
