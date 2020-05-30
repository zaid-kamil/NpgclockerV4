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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

import aryan.digipodium.npgclocker.models.StudentModel;

public class SignupStudentActivity extends AppCompatActivity {

    public static final String STUDENTS = "students";
    private Button nextBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean collegeidExists;
    private Button request;
    private EditText editMobile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_student);
        editMobile = findViewById(R.id.mobile);
        nextBtn = findViewById(R.id.NextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupStudentActivity.this, LoginStudentActivity.class);
                startActivity(intent);
            }
        });

        request = findViewById(R.id.request);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ((EditText) findViewById(R.id.userName)).getText().toString();

                String mobile = editMobile.getText().toString();
                String dob = ((EditText) findViewById(R.id.dob)).getText().toString();
                String collegeid = ((EditText) findViewById(R.id.collegeid)).getText().toString();
                // todo add validation
                if (!isValidMobile(mobile)) {
                    Toast.makeText(SignupStudentActivity.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                    editMobile.requestFocus();
                    return;
                }
                StudentModel std = new StudentModel(name, collegeid, mobile, dob);
                // Add a new document with a generated ID
                checkCollegeId(std);
            }
        });
    }

    private void checkCollegeId(final StudentModel student) {
        DocumentReference document = db.collection(STUDENTS).document(student.collegeid);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        collegeidExists = true;
                        Snackbar.make(nextBtn, "College Id exists", Snackbar.LENGTH_INDEFINITE).show();
                    } else {
                        collegeidExists = false;

                        db.collection(STUDENTS).document(student.collegeid).set(student)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updateUi("success");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(nextBtn, e.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction("Ok", null).show();
                                        updateUi(null);
                                    }
                                });
                    }
                }
            }
        });
    }

    private void updateUi(String msg) {
        if (!msg.equals("success")) {
            //fail
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        } else {
            //pass
            Snackbar.make(nextBtn, "Your request has been submitted.The response will be provided soon to you.", Snackbar.LENGTH_INDEFINITE).show();
            ((EditText) findViewById(R.id.userName)).setText("");
            ((EditText) findViewById(R.id.mobile)).setText("");
            ((EditText) findViewById(R.id.dob)).setText("");
            ((EditText) findViewById(R.id.collegeid)).setText("");
        }
    }

    private boolean isValidMobile(String phone) {
        if (phone.length()>=10 && phone.length() <=13){
            return true;
        }
        return false;
    }
}
