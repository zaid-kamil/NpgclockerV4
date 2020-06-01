package aryan.digipodium.npgclocker.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import aryan.digipodium.npgclocker.models.Document;

public class StHistoryFragment extends Fragment {

    RecyclerView rvHistory;
    private CollectionReference dbRef;
    private ArrayList<Document> filesList;
    private ProgressBar pb;

    public static StHistoryFragment newInstance() {
        return new StHistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dbRef = FirebaseFirestore.getInstance().collection("files");
        return inflater.inflate(R.layout.st_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvHistory = view.findViewById(R.id.rvHistory);
        pb = view.findViewById(R.id.pb);
        filesList = new ArrayList<>();
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (getArguments() != null) {
            StHistoryFragmentArgs args = StHistoryFragmentArgs.fromBundle(getArguments());
            final String name = args.getName();
            if (name.equals("na")) {
                dbRef.whereEqualTo("uploaderid", Helper.getStudentId(getActivity())).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e == null) {
                            filesList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                filesList.add(document.toObject(Document.class));
                            }
                            FilesAdapter adapter = new FilesAdapter(getContext(), R.layout.row_files, filesList);
                            rvHistory.setAdapter(adapter);
                        }
                        pb.setVisibility(View.GONE);
                    }
                });
            } else {
                dbRef.whereEqualTo("uploaderid", Helper.getStudentId(getActivity())).whereEqualTo("folder", name).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e == null) {
                            filesList.clear();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                filesList.add(document.toObject(Document.class));
                            }
                            FilesAdapter adapter = new FilesAdapter(getContext(), R.layout.row_files, filesList);
                            rvHistory.setAdapter(adapter);
                        }
                        pb.setVisibility(View.GONE);
                    }
                });
            }
        }

    }

    private void deleteDocument(Document doc) {

    }

    private void downloadDocument(Document doc) {

    }

    private class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.Holder> {

        private final Context context;
        private final int layout;
        private final LayoutInflater inflater;
        private List<Document> list;

        public FilesAdapter(Context context, int layout, List<Document> list) {
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
            Document model = list.get(position);
            holder.textName.setText(model.filename);
            holder.textCategory.setText(model.filetype);
            holder.imgDelete.setTag(model);
            holder.imgDownload.setTag(model);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            TextView textName, textCategory;
            ImageView imgDownload, imgDelete;

            public Holder(@NonNull View v) {
                super(v);
                textName = v.findViewById(R.id.textName);
                textCategory = v.findViewById(R.id.textCategory);
                imgDelete = v.findViewById(R.id.imgDelete);
                imgDownload = v.findViewById(R.id.imgDownload);
                imgDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Document doc = (Document) v.getTag();
                        downloadDocument(doc);
                    }
                });
                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Document doc = (Document) v.getTag();
                        deleteDocument(doc);
                    }
                });
            }
        }
    }
}
