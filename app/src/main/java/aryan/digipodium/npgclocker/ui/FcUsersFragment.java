package aryan.digipodium.npgclocker.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import aryan.digipodium.npgclocker.R;
import aryan.digipodium.npgclocker.models.StudentModel;

public class FcUsersFragment extends Fragment {

    private List<StudentModel> approvedList = new ArrayList<>();
    private RecyclerView approvedRecyclerView;
    private approvedAdapter adapter;
    private ProgressBar pb;

    public static FcUsersFragment newInstance() {
        return new FcUsersFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fc_users_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        approvedRecyclerView = view.findViewById(R.id.userList);
        approvedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pb = view.findViewById(R.id.pb);
        FirebaseFirestore.getInstance().collection("students").whereEqualTo("approved", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                approvedList.clear();
                for (DocumentSnapshot document : documents) {
                    StudentModel model = document.toObject(StudentModel.class);
                    if (model != null) {
                        approvedList.add(model);
                    }
                    pb.setVisibility(View.GONE);
                    adapter = new approvedAdapter(getActivity(), R.layout.row_users, approvedList);
                    approvedRecyclerView.setAdapter(adapter);
                }
            }
        });

    }

    private class approvedAdapter extends RecyclerView.Adapter<approvedAdapter.Holder> {

        private final Context context;
        private final int layout;
        private final LayoutInflater inflater;
        private List<StudentModel> list;

        public approvedAdapter(Context context, int layout, List<StudentModel> list) {
            this.context = context;
            this.layout = layout;
            inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @NonNull
        @Override
        public approvedAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(layout, parent, false);
            return new approvedAdapter.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            StudentModel model = list.get(position);
            holder.card.setTag(model);
            holder.stdName.setText(model.username);
            holder.collegeId.setText(model.collegeid);
        }

        public void updateList(List<StudentModel> list) {
            this.list.clear();
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class Holder extends RecyclerView.ViewHolder {
            ConstraintLayout card;
            TextView stdName, collegeId;
            ImageView imgNo, imgYes;
            ProgressBar pb;


            public Holder(@NonNull View v) {
                super(v);
                card = v.findViewById(R.id.card);
                stdName = v.findViewById(R.id.stdName);
                collegeId = v.findViewById(R.id.collegeId);
                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final StudentModel model = (StudentModel) v.getTag();
                        String[] data = new String[]{
                                model.username, model.dob, model.mobile, model.collegeid, model.created_on
                        };
                        new AlertDialog.Builder(context).setPositiveButton("ok", null).setNegativeButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseFirestore.getInstance().collection("students").document(model.collegeid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            View parent = (View) v.getParent();
                                            parent.animate().scaleY(0).setDuration(200).start();
                                            parent.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(context, "could not delete item", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }).setTitle("Student Details")
                                .setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, data), null)
                                .create()
                                .show();
                    }
                });


            }
        }
    }
}


