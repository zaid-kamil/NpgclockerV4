package aryan.digipodium.npgclocker;

import android.content.Context;
import android.content.SharedPreferences;

public class Helper {

    public static int getUserType(Context c) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        return userPref.getInt("type", 0);
    }

    public static String getStudentId(Context c) {
        SharedPreferences stdId = c.getSharedPreferences("student", Context.MODE_PRIVATE);
        return stdId.getString("id", "000000");
    }

    public static void setUserType(Context c, int userType) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        userPref.edit().putInt("type", userType).apply();
    }

    public static void setStudentId(Context c, String id) {
        SharedPreferences userPref = c.getSharedPreferences("student", Context.MODE_PRIVATE);
        userPref.edit().putString("id", id).apply();
    }


    public static void clearUserType(Context c) {
        SharedPreferences userPref = c.getSharedPreferences("userpref", Context.MODE_PRIVATE);
        SharedPreferences stdId = c.getSharedPreferences("student", Context.MODE_PRIVATE);
        userPref.edit().clear().apply();
        stdId.edit().clear().apply();
    }
}
