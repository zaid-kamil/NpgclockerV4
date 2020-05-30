package aryan.digipodium.npgclocker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFacultyActivity extends AppCompatActivity {

    private EditText adminPwd;
    private EditText admin;
    private FirebaseAuth mAuth;
    private Button btnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_login);
        mAuth = FirebaseAuth.getInstance();

        admin = findViewById(R.id.mobile);
        adminPwd = findViewById(R.id.adminPwd);
        btnSignin = findViewById(R.id.btnSignin);
        adminPwd.requestFocus();
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adminPwd.getText().toString().length() > 0) {
                    mAuth.signInWithEmailAndPassword("aryanarvind75@gmail.com", adminPwd.getText().toString()).addOnCompleteListener(LoginFacultyActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                Toast.makeText(LoginFacultyActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(LoginFacultyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
