package aryan.digipodium.npgclocker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginStudentActivity extends AppCompatActivity {
    private TextView signup;
    private Button btnSignin;
    private EditText editID;
    private EditText editPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_student);
        editID = findViewById(R.id.editID);
        editPass = findViewById(R.id.editPass);
        signup = findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginStudentActivity.this, SignupStudentActivity.class);
                startActivity(intent);
            }
        });
        btnSignin = findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String id = editID.getText().toString();
                String password = editPass.getText().toString();
                if (id.length() > 0 && password.length() > 6) {
                    FirebaseFirestore.getInstance().collection("students_verified").whereEqualTo("id", id).whereEqualTo("password", password).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e == null) {
                                int size = queryDocumentSnapshots.getDocuments().size();
                                if (size == 1) {
                                    Helper.setStudentId(LoginStudentActivity.this, id);
                                    gotoHome();
                                } else {
                                    Snackbar.make(editID, "invalid Credentials", BaseTransientBottomBar.LENGTH_LONG).show();
                                }
                            } else {
                                Snackbar.make(editID, "invalid Credentials", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void gotoHome() {
        Helper.setUserType(this,1);
        Intent intent = new Intent(LoginStudentActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
