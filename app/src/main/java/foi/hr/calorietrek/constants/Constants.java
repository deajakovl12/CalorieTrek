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
}