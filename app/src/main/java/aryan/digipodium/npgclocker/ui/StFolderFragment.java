package aryan.digipodium.npgclocker.ui;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import aryan.digipodium.npgclocker.Helper;
import aryan.digipodium.npgclocker.R;
import aryan.digipodium.npgclocker.models.FolderModel;
import aryan.digipodium.npgclocker.models.StudentModel;
import aryan.digipodium.npgclocker.models.StudentVerfied;

public class StFolderFragment extends Fragment {


    private RecyclerView folderRv;
    private FloatingActionButton fab;
    private EditText editNewFolder;
    private CollectionReference folderRef;
    private ArrayList<FolderModel> folderList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        folderRef = FirebaseFirestore.getInstance().collection("folders");
        return inflater.inflate(R.layout.st_folder_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        folderRv = view.findViewById(R.id.folderRv);
        fab = view.findViewById(R.id.fab);
        editNewFolder = view.findViewById(R.id.editNewFolder);
        final ProgressBar pb = view.findViewById(R.id.pb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                String name = editNewFolder.getText().toString();
                folderRef.document(Helper.getStudentId(getActivity())  + name).set(new FolderModel(name)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(fab, "folder created", BaseTransientBottomBar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(fab, "folder could not be created", BaseTransientBottomBar.LENGTH_LONG).show();
                        }
                        pb.setVisibility(View.GONE);
                        editNewFolder.setText("");
                    }
                });
            }
        });
        folderList = new ArrayList<>();
        folderRv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        folderRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null) {
                    folderList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        if(document.getId().equals(Helper.getStudentId(getActivity()))){
                            folderList.add(document.toObject(FolderModel.class));
                        }
                    }
                    FolderAdapter adapter = new FolderAdapter(getContext(), R.layout.row_folders, folderList);
                    folderRv.setAdapter(adapter);
                }
            }
        });

    }

    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.Holder> {

        private final Context context;
        private final int layout;
        private final LayoutInflater inflater;
        private List<FolderModel> list;

        public FolderAdapter(Context context, int layout, List<FolderModel> list) {
            this.context = context;
            this.layout = layout;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(layout, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            FolderModel model = list.get(position);
            holder.textFolder.setText(model.name);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView textFolder;

            public Holder(@NonNull View v) {
                super(v);
                textFolder = v.findViewById(R.id.textFolder);
            }
        }
    }
}
