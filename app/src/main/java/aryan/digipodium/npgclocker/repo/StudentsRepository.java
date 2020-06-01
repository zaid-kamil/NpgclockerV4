package aryan.digipodium.npgclocker.repo;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import aryan.digipodium.npgclocker.models.StudentModel;

public class StudentsRepository {

    public static final String STUDENTS_COLLECTION = "students";
    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private final FirebaseStorage storage;

    public StudentsRepository() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
    }

    public Task<Void> saveStudent(StudentModel model) {
        return db.collection(STUDENTS_COLLECTION).document(model.collegeid).set(model);
    }
}



