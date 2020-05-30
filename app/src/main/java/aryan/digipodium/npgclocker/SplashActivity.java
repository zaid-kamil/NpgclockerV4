package aryan.digipodium.npgclocker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {


    Button button;
    Animation frombutton,fromtop;
    ImageView logo;

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
                Intent intent = new Intent(SplashActivity.this,ChoiceActivity.class);
                startActivity(intent);
            }
        });
    }
}
