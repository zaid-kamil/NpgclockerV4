package aryan.digipodium.npgclocker.ui;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import aryan.digipodium.npgclocker.Helper;
import aryan.digipodium.npgclocker.R;
import aryan.digipodium.npgclocker.models.Document;

public class FcHistoryFragment extends Fragment {

    RecyclerView rvHistory;
    private CollectionReference dbRef;
    private ArrayList<Document> filesList;
    private ProgressBar pb;
    private long downloadId;

    public static FcHistoryFragment newInstance() {
        return new FcHistoryFragment();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == id) {
                Toast.makeText(getActivity(), "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        dbRef = FirebaseFirestore.getInstance().collection("files");
        getActivity().registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return inflater.inflate(R.layout.fc_history_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvHistory = view.findViewById(R.id.rvHistory);
        pb = view.findViewById(R.id.pb);
        filesList = new ArrayList<>();
        rvHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        getData();
    }

    private void getData() {
        dbRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    filesList.clear();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        filesList.add(document.toObject(Document.class));
                    }
                    FilesAdapter adapter = new FilesAdapter(getContext(), R.layout.row_files, filesList);
                    rvHistory.setAdapter(adapter);
                }
                pb.setVisibility(View.GONE);
            }
        });
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
        public FilesAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(layout, parent, false);
            return new FilesAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FilesAdapter.Holder holder, int position) {
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

    private void deleteDocument(Document doc) {
        dbRef.whereEqualTo("filename", doc.filename).whereEqualTo("uploaderid", Helper.getStudentId(getActivity())).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String id = document.getId();
                        dbRef.document(id).delete();
                    }
                    getData();
                }
            }
        });
    }

    private void downloadDocument(Document doc) {
        File file = new File(getActivity().getExternalFilesDir(null), doc.filename);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(doc.fileurl))
                .setTitle(doc.filename)
                .setDescription("downloading please wait...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "npgc_locker")
                .setAllowedOverMetered(true).
                        setAllowedOverRoaming(true);
        DownloadManager service = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = service.enqueue(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(onDownloadComplete);
    }
}
