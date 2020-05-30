package aryan.digipodium.npgclocker.repo;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import aryan.digipodium.npgclocker.models.Document;

public class DocumentRepository {

    public static final String DOCUMENTS = "documents";
    public static final String FILES = "files";
    private final FirebaseFirestore db;
    private final FirebaseUser user;
    private final FirebaseStorage storage;

    public DocumentRepository() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();
    }

    public void saveDocument(Uri fileUri) {
        UploadTask uploadTask = storage.getReference(user.getDisplayName() + "/" + FILES).putFile(fileUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                db.collection(DOCUMENTS).document(user.getEmail()).collection(FILES).document().set(new Document("document", downloadUrl.getResult().toString(), "PDF", user.getUid()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public CollectionReference getSavedDocuments() {
        return db.collection(DOCUMENTS).document(user.getEmail()).collection(FILES);
    }

    public Task<Void> deleteDocument(String docId) {
        DocumentReference document = db.collection(DOCUMENTS).document(user.getEmail()).collection(FILES).document(docId);
        Task<Void> task = document.delete();
//        storage.getReferenceFromUrl(url).delete()
        return task;
    }
}
