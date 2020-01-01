package com.jed.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;

public class UploadedCloudFileAdapter extends RecyclerView.Adapter<UploadedCloudFileAdapter.ViewHolder> {

    private static final String TAG = "UploadedCloudFileAdapter";
    private List<UploadedCloudFile> UploadedCloudFileList;
    private Context context;

    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(currentUser.getDisplayName());

    public UploadedCloudFileAdapter(List<UploadedCloudFile> UploadedCloudFileList, Context context) {
        this.UploadedCloudFileList = UploadedCloudFileList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UploadedCloudFile UploadedCloudFile = UploadedCloudFileList.get(position);

        // WE STRIP OFF THE DATETIME STAMP FROM THE FILENAME FOR BETTER PRESENTATION
        String truncatedFileName = UploadedCloudFile.getFileName().substring(0,
                UploadedCloudFile.getFileName().length() - 28);
        Date localDate = UploadedCloudFile.getCreated();
        String truncatedDate = localDate.toString().substring(0,
                localDate.toString().length() - 18);
        holder.fileDate.setText(truncatedDate);
        holder.fileName.setText(truncatedFileName);
    }

    @Override
    public int getItemCount() {
        return UploadedCloudFileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView fileDate;
        TextView fileName;
        Button selectButton;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            fileDate = itemView.findViewById(R.id.fileDate);
            fileName = itemView.findViewById(R.id.fileName);
            cv = itemView.findViewById(R.id.uploadHistoryCardView);
            selectButton = itemView.findViewById(R.id.selectButton);
            selectButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            /*
            TODO
            1. IMPLEMENT ChatHistoryActivity.finish() UPON CLICK,
            2. GREY OUT (VIA CODE) THE UPLOAD BUTTON
            3. CHANGE ICON (TO TICK) OF UPLOAD BUTTON
             */

            // SET THE NEWLY CHOSEN FILE FOR ANALYSIS
            FileProcessing.reset();
            ReplyTiming.reset();

            // DETERMINE SELECTED FILE AND PULL FROM CLOUD
            int chosenPosition = this.getPosition();
            UploadedCloudFile chosenFile = UploadedCloudFileList.get(chosenPosition);
            String chosenFileCloudDir = chosenFile.getFileName();
            String chosenFileName = chosenFileCloudDir.substring(0, chosenFileCloudDir.length() - 28);
            Log.d(TAG, "chosenFileCloudDir : " + chosenFile.getFileName());
            StorageReference chosenFileRef = mStorageRef.child(chosenFileCloudDir);
            chosenFileRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        // ADD TO FILE PROCESSING
                        FileProcessing.setUploadedFileURI(uri);
                        FileProcessing.setInitialized(true);
                        FileProcessing.setFileName(chosenFileName);

                        // CLOSE THE HISTORY SELECTOR ACTIVITY
                        ((Activity) context).finish();
                    })
                    .addOnFailureListener(e -> Log.d(TAG, "DOWNLOAD FAILURE + " + chosenFileCloudDir));

            // CLOSE THE CHAT HISTORY SELECTOR ACTIVITY
        }
    }
}
