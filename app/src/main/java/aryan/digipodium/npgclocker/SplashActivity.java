package aryan.digipodium.npgclocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    Button button;
    Animation frombutton, fromtop;
    ImageView logo;
    String[] permissions = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_main);
        TextView textView4 = findViewById(R.id.textView4);
        TextView textView8 = findViewById(R.id.textView8);
        button = findViewById(R.id.request);
        frombutton = AnimationUtils.loadAnimation(this, R.anim.frombutton);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        logo = findViewById(R.id.logo);
        button.setAnimation(frombutton);

        textView4.setAnimation(fromtop);
        textView8.setAnimation(frombutton);
        logo.setAnimation(fromtop);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(SplashActivity.this, permissions)) {
                    Intent intent = new Intent(SplashActivity.this, ChoiceActivity.class);
                    startActivity(intent);
                }else{
                    EasyPermissions.requestPermissions(SplashActivity.this,"please allow permissions",892,permissions);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Snackbar.make(button, "Click the button to continue", BaseTransientBottomBar.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Snackbar.make(button, "Permission are required to app to work", BaseTransientBottomBar.LENGTH_LONG).show();
    }
}
