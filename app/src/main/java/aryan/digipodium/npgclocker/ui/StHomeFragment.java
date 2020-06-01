package aryan.digipodium.npgclocker.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import aryan.digipodium.npgclocker.Helper;
import aryan.digipodium.npgclocker.R;
import aryan.digipodium.npgclocker.models.Document;
import aryan.digipodium.npgclocker.models.FolderModel;

import static android.app.Activity.RESULT_OK;

public class StHomeFragment extends Fragment {


    private static final int REQUEST_IMAGE_GET = 812;
    private Button btnUpload;
    private ImageView imgSelect;
    private Spinner spFolder;
    private EditText editFile;
    private TextView textFileState;
    private ProgressBar pb;
    private boolean isFileSelected = false;
    private List<FolderModel> folderList;
    private StorageReference storage;
    private boolean isFolderSelected = false;
    private Uri fullPhotoUri;

    public static StHomeFragment newInstance() {
        return new StHomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.st_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnUpload = view.findViewById(R.id.btnUpload);
        imgSelect = view.findViewById(R.id.imgSelect);
        spFolder = view.findViewById(R.id.spFolder);
        editFile = view.findViewById(R.id.editFile);
        textFileState = view.findViewById(R.id.textFileState);
        pb = view.findViewById(R.id.pb);
        storage = FirebaseStorage.getInstance().getReference();

        folderList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("folders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                folderList.clear();
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        if (document.getId().contains(Helper.getStudentId(getActivity()))) {
                            folderList.add(document.toObject(FolderModel.class));
                        }
                    }
                    final ArrayAdapter<FolderModel> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, folderList);
                    adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                    spFolder.setAdapter(adapter);
                }
            }
        });

        imgSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFiles();
            }
        });
        spFolder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FolderModel folderModel = folderList.get(position);
                String folderName = folderModel.name;
                isFolderSelected = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                String filename = editFile.getText().toString();
                if (isFileSelected && isFolderSelected && filename.length() > 0) {
                    int pos = spFolder.getSelectedItemPosition();
                    String folderName = folderList.get(pos).name;

                    final String ext = getFileExtention(fullPhotoUri);
                    final String fullFilename = filename + "." + ext;
                    final StorageReference fileRef = storage.child(folderName).child(fullFilename);
                    fileRef.putFile(fullPhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Document data = new Document(fullFilename, uri.toString(), ext, Helper.getStudentId(getActivity()));
                                    FirebaseFirestore.getInstance().collection("files").document().set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Snackbar.make(btnUpload, "Successfully uploaded", BaseTransientBottomBar.LENGTH_LONG).show();
                                            } else {
                                                Snackbar.make(btnUpload, task.getException().getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                                            }
                                            pb.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                            isFolderSelected = false;
                            spFolder.setSelection(0);
                            isFileSelected = false;
                            textFileState.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.GONE);
                            Snackbar.make(btnUpload, e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();
                            isFolderSelected = false;
                            spFolder.setSelection(0);
                            isFileSelected = false;
                            textFileState.setText("");
                        }
                    });

                } else {
                    Snackbar.make(btnUpload, "please select a file, folder and filename", BaseTransientBottomBar.LENGTH_LONG).show();
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }

    private String getFileExtention(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap typeMap = MimeTypeMap.getSingleton();
        return typeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void selectFiles() {
        Intent intent;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        }
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("return-data", true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_GET);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            fullPhotoUri = data.getData();
            textFileState.setText("file:" + fullPhotoUri.toString());
            Snackbar.make(btnUpload, "file selected" + fullPhotoUri.toString(), BaseTransientBottomBar.LENGTH_LONG).show();
            isFileSelected = true;

        }
    }
}
