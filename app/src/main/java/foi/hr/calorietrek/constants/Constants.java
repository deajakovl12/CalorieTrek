package foi.hr.calorietrek.constants;

/**
 * Created by juras on 2017-11-30.
 * This class is used for global constant variables. It is used for clean and organized use of constants in CalorieTrek.
 */

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "foi.hr.calorietrek.action.main";
        public static String PLAY_ACTION = "foi.hr.calorietrek.action.play";
        public static String PAUSE_ACTION = "foi.hr.calorietrek.action.pause";
        public static String BROADCAST_ACTION = "foi.hr.calorietrek.action.broadcast";
        public static String STARTFOREGROUND_ACTION = "foi.hr.calorietrek.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "foi.hr.calorietrek.action.stopforeground";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
    public interface GPSPARAMETERS {
        //regular update interval in miliseconds in which time will fused location provider client request location update
        public static int UPDATE_INTERVAL = 7500;
        //if fused location provider client gets new good location in less time than regular interval   ukoliko se "ispravna" lokacija ukaže i prije regularnog intervala aplikacija fused location provider client će postaviti novu lokaciju i prije regularnog intervala
        public static int FASTEST_UPDATE_INTERVAL = 3750;
        //accuracy of GPS locations that device receives, also affects battery life parameters can be  high-100, balanced-102,  low-104, noPower-105
        public  static  int ACCURACY = 100;
        //world record in 100m run meters/second
        public static double  FASTEST_HUMAN_SPEED = 12.4222;
        //average human fastest running speed (debatable) but around  16.09 to 24.14 km/h, taken from higher value in m/s
        public static double AVERAGE_HUMAN_FASTEST_RUNNING_SPEED = 6.7056;
        //GPS-enabled smartphones are typically accurate to within a 4.9 m
        public static double GPS_ERROR_RANGE = 4.9;
        //max cargo weight for human to be able to run at speeds between AVERAGE and FASTEST human speed
        public static int MAX_CARGO_FOR_FAST_RUN = 5;
        public static final int TWO_MINUTES = 1000 * 60 * 2;
    }

    public interface PHOTOPARAMETERS{
        public static int PHOTO_HEIGHT=200;
        public static int PHOTO_WIDTH=200;
    }

    public interface CALORIES {
        public static int JOULES = 4184;
        public static double[][] SLOPE_COEFFICIENT =
        {
            {-0.45,4.51},
            {-0.40,3.82},
            {-0.35,3.53},
            {-0.30,2.85},
            {-0.20,1.78},
            {-0.10,1.18},
            {00.00,2.14},
            {00.10,5.02},
            {00.20,8.64},
            {00.30,12.43},
            {00.35,13.48},
            {00.40,15.36},
            {00.45,18.44}
        };
    }

}
