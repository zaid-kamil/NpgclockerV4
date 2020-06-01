package aryan.digipodium.npgclocker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChoiceActivity extends AppCompatActivity {

    private Button btnFaculty;
    private Button btnStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        btnStudent = findViewById(R.id.btnStudent);
        btnFaculty = findViewById(R.id.btnFaculty);
        btnFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this, LoginFacultyActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this, SignupStudentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
