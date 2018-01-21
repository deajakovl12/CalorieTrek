package foi.hr.calorietrek.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import foi.hr.calorietrek.model.TrainingLocationInfo;
import foi.hr.calorietrek.model.TrainingModel;

/*
Database class used for communication between CalorieTrek and SQLite database.
 */
public class DbHelper extends SQLiteOpenHelper{
    private static DbHelper sInstance;

    public static final String DATABASE_NAME = "CalorieTrek.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_USER = "user";
    public static final String COL_ID_USER = "id_user";
    public static final String COL_EMAIL = "email";
    public static final String COL_NAME = "name_surname";
    public static final String COL_WEIGHT = "weight";

    public static final String TABLE_TRAINING = "training";
    public static final String COL_ID_TRAINING = "id_training";
    public static final String COL_FK_USER = "fk_user";
    public static final String COL_DATE = "date";
    public static final String COL_TRAINING_NAME = "training_name";
    public static final String COL_TRAINING_WEIGHT = "weight_training";

    public static final String TABLE_LOCATION = "locations";
    public static final String COL_ID_LOCATION = "id_location";
    public static final String COL_FK_TRAINING = "fk_training";
    public static final String COL_ALTITUDE = "altitude";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_TIME = "time";
    public static final String COL_CARGO_WEIGHT = "cargo_weight_";

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
                COL_EMAIL + " VARCHAR(100) NOT NULL, " +
                COL_WEIGHT + " INTEGER NOT NULL);");
        db.execSQL("CREATE TABLE " + TABLE_TRAINING + "(" +
                COL_ID_TRAINING + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FK_USER + " INTEGER NOT NULL, " +
                COL_DATE + " VARCHAR(19), "+
                COL_TRAINING_NAME + " TEXT,"+
                COL_TRAINING_WEIGHT + " INTEGER NOT NULL,"+
                "FOREIGN KEY ("+COL_FK_USER+") REFERENCES "+TABLE_USER+"("+COL_ID_USER+"));");
        db.execSQL("CREATE TABLE " + TABLE_LOCATION + "(" +
                COL_ID_LOCATION + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FK_TRAINING + " INTEGER NOT NULL, " +
                COL_ALTITUDE + " REAL NOT NULL, "+
                COL_LATITUDE + " REAL NOT NULL, "+
                COL_LONGITUDE + " REAL NOT NULL, "+
                COL_TIME + " REAL NOT NULL,"+
                COL_CARGO_WEIGHT + " INTEGER NOT NULL,"+
                "FOREIGN KEY ("+COL_FK_TRAINING+") REFERENCES "+TABLE_TRAINING+"("+COL_ID_TRAINING+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING);
            onCreate(db);
        }
    }

    public void insertUser(String nameSurname,String email , String weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, nameSurname);
        values.put(COL_EMAIL, email);
        values.put(COL_WEIGHT, weight);

        long result = db.insert(TABLE_USER, null, values);
    }

    public boolean existingUser(String nameSurname, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM user WHERE email = '" + email + "'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0){
            cursor.close();
            db.close();
            insertUser(nameSurname, email,"55");
            return true;
        }
        else{
            cursor.close();
            db.close();
            return false;
        }
    }

    //since method above should be run(and always is) before this one, checking if user exists is not necessary
    public int getUserID(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String table = TABLE_USER;
        String[] columns = {COL_ID_USER};
        String selection = COL_EMAIL+" =?";
        String[] selectionArgs = {email};
        String groupBy = null;
        String having = null;
        String orderBy = null;
        String limit = null;
        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        cursor.moveToFirst();
        Integer result = cursor.getInt(cursor.getColumnIndex(COL_ID_USER));
        cursor.close();
        db.close();
        return result;
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

    public TrainingModel returnLatestTraining(int userID) {
        TrainingModel result = new TrainingModel("", "", new ArrayList<TrainingLocationInfo>(),0);

        SQLiteDatabase db = this.getWritableDatabase();
        String queryTraining = "SELECT id_training,weight_training FROM training WHERE fk_user = '" + userID + "' ORDER BY id_training DESC LIMIT 1";
        Cursor cursor = db.rawQuery(queryTraining, null);

        if (cursor.moveToFirst()){
            int trainingID = cursor.getInt(cursor.getColumnIndex("id_training"));
            int userWeight = cursor.getInt(cursor.getColumnIndex("weight_training"));
            result = new TrainingModel(returnTrainingDate(trainingID), returnTrainingName(trainingID), returnTrainingLocations(trainingID),userWeight);
        }

        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<TrainingModel> returnAllTrainings(int userID) {
        ArrayList<TrainingModel> result = new ArrayList<TrainingModel>();

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM training WHERE fk_user = '" + userID + "'";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()){
            int trainingID = cursor.getInt(cursor.getColumnIndex("id_training"));
            int userWeight = cursor.getInt(cursor.getColumnIndex("weight_training"));
            TrainingModel training = new TrainingModel(returnTrainingDate(trainingID), returnTrainingName(trainingID), returnTrainingLocations(trainingID),userWeight);
            result.add(training);
        }

        cursor.close();
        db.close();
        return result;
    }

    public String returnTrainingName(int trainingID){
        String result = "";

        SQLiteDatabase db = this.getWritableDatabase();
        String queryTraining = "SELECT training_name FROM training WHERE id_training = '" + trainingID + "'";
        Cursor cursor = db.rawQuery(queryTraining, null);

        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("training_name"));
        }
        result = (result == null) ? "" : result;

        cursor.close();
        db.close();
        return result;
    }

    public int returnTrainingWeight(int trainingID){
        int result=0;

        SQLiteDatabase db = this.getWritableDatabase();
        String queryTraining = "SELECT weight_training FROM training WHERE id_training = '" + trainingID + "'";
        Cursor cursor = db.rawQuery(queryTraining, null);

        if (cursor.moveToFirst()){
            result = cursor.getInt(cursor.getColumnIndex("training_name"));
        }

        cursor.close();
        db.close();
        return result;
    }

    public String returnTrainingDate(int trainingID){
        String result = "";

        SQLiteDatabase db = this.getWritableDatabase();
        String queryTraining = "SELECT date FROM training WHERE id_training = '" + trainingID + "'";
        Cursor cursor = db.rawQuery(queryTraining, null);

        if (cursor.moveToFirst()){
            result = cursor.getString(cursor.getColumnIndex("date"));
        }
        result = (result == null) ? "" : result;

        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<TrainingLocationInfo> returnTrainingLocations(int trainingID){
        ArrayList<TrainingLocationInfo> result = new ArrayList<TrainingLocationInfo>();

        SQLiteDatabase db = this.getWritableDatabase();
        String queryTraining = "SELECT * FROM locations WHERE fk_training = '" + trainingID + "'";
        Cursor cursor = db.rawQuery(queryTraining, null);

        while (cursor.moveToNext()){
            Location location = new Location("dummy");
            location.setAltitude(cursor.getDouble(cursor.getColumnIndex("altitude")));
            location.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
            location.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
            location.setTime(cursor.getLong(cursor.getColumnIndex("time")));
            int cargoWeight = cursor.getInt(cursor.getColumnIndex("cargo_weight_"));
            long time = cursor.getInt(cursor.getColumnIndex("time"));
            TrainingLocationInfo trainingLocationInfo=new TrainingLocationInfo(location,cargoWeight,time);
            result.add(trainingLocationInfo);
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

    /*
    public boolean updateEmail(String nameSurname, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE user SET email = '" + email + "' WHERE name_surname = '" + nameSurname + "'";
        db.execSQL(query);
        return true;
    }


    public long insertTraining(int fkUser, double dateTime, String trainingName, int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FK_USER, fkUser);
        values.put(COL_DATE, dateTime);
        values.put(COL_TRAINING_NAME, trainingName);
        values.put(COL_TRAINING_WEIGHT,weight);
        return db.insert(TABLE_TRAINING, null, values);
    }

    public long insertTraining(int fkUser, double dateTime, int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FK_USER, fkUser);
        values.put(COL_DATE, dateTime);
        values.put(COL_TRAINING_WEIGHT,weight);

        return db.insert(TABLE_TRAINING, null, values);
    }
    */

    public long insertTraining(int fkUser, int weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FK_USER, fkUser);
        values.put(COL_TRAINING_WEIGHT,weight);
        return db.insert(TABLE_TRAINING, null, values);
    }
    public  void insertLocation(long fkTraining, Location location, int cargo_weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FK_TRAINING, fkTraining);
        values.put(COL_ALTITUDE, location.getAltitude());
        values.put(COL_LATITUDE, location.getLatitude());
        values.put(COL_LONGITUDE, location.getLongitude());
        values.put(COL_TIME, location.getTime());
        values.put(COL_CARGO_WEIGHT, cargo_weight);
        long result = db.insert(TABLE_LOCATION, null, values);
    }

}
