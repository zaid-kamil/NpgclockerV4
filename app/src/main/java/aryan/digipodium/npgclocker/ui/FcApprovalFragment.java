package aryan.digipodium.npgclocker.ui;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import aryan.digipodium.npgclocker.R;
import aryan.digipodium.npgclocker.models.StudentModel;

public class FcApprovalFragment extends Fragment {


    private List<StudentModel> unapprovedList = new ArrayList<>();
    private RecyclerView unapprovedStudentRecycler;
    private approvalAdapter adapter;
    private ProgressBar bar;

    public static FcApprovalFragment newInstance() {
        return new FcApprovalFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fc_aproval_fragment, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bar = view.findViewById(R.id.bar);
        unapprovedStudentRecycler = view.findViewById(R.id.unapprovedStudentRecycler);
        unapprovedStudentRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseFirestore.getInstance().collection("students").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot document : documents) {
                    StudentModel model = document.toObject(StudentModel.class);
                    if (model != null && !model.approved) {
                        unapprovedList.add(model);
                    }
                }
                bar.setVisibility(View.GONE);
                adapter = new approvalAdapter(getActivity(), R.layout.row_unapproved_student, unapprovedList);
                unapprovedStudentRecycler.setAdapter(adapter);
            }
        });


    }

    private class approvalAdapter extends RecyclerView.Adapter<approvalAdapter.Holder> {

        private final Context context;
        private final int layout;
        private final LayoutInflater inflater;
        private List<StudentModel> list;

        public approvalAdapter(Context context, int layout, List<StudentModel> list) {
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
                imgNo = v.findViewById(R.id.imgNo);
                imgYes = v.findViewById(R.id.imgYes);
                pb = v.findViewById(R.id.pb);

                imgNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        pb.setVisibility(View.VISIBLE);
                        StudentModel model = (StudentModel) card.getTag();
                        String documentId = model.collegeid;
                        FirebaseFirestore.getInstance().collection("students").document(documentId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    View parent = (View) v.getParent().getParent().getParent();
                                    parent.animate().scaleY(0).setDuration(200).start();
                                    parent.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(context, "could not delete item", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                imgYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        pb.setVisibility(View.VISIBLE);
                        StudentModel model = (StudentModel) card.getTag();
                        String documentId = model.collegeid;
                        FirebaseFirestore.getInstance().collection("students").document(documentId).update("approved", true).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    View parent = (View) v.getParent().getParent().getParent();
                                    parent.animate().scaleY(0).setDuration(200).start();
                                    parent.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(context, "could not update request", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
