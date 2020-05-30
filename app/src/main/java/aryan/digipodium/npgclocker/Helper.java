package aryan.digipodium.npgclocker;

import android.content.Context;
import android.content.SharedPreferences;

public class Helper {

    public static int getUserType(Context c) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        return userPref.getInt("type", 0);
    }

    public static void setUserType(Context c, int userType) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        userPref.edit().putInt("type", userType).apply();
    }


    public static void clearUserType(Context c) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        userPref.edit().clear().apply();
    }
}
