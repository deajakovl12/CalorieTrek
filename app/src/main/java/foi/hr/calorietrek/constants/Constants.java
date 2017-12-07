package foi.hr.calorietrek.constants;

/**
 * Created by juras on 2017-11-30.
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
        //regularni interval u milisekundama u kojem će fused location provider client zahtijevati novu lokaciju
        public static int UPDATE_INTERVAL = 5000;
        //ukoliko se "ispravna" lokacija ukaže i prije regularnog intervala aplikacija fused location provider client će postaviti novu lokaciju i prije regularnog intervala
        public static int FASTEST_UPDATE_INTERVAL = 2500;
        //točnost gps lokacija koje uređaj dobiva, o čemu ovisi i potrošnja baterije  high-100, balanced-102,  low-104, noPower-105
        public  static  int ACCURACY = 100;
    }

    public interface PHOTOPARAMETERS{
        public static int PHOTO_HEIGHT=200;
        public static int PHOTO_WIDTH=200;
    }

    public interface CALORIES {
        public static int JOULES = 4184;
    }

}
