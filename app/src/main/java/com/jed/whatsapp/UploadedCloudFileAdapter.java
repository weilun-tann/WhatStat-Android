package com.jed.whatsapp;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.Distribution;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UploadedCloudFileAdapter extends RecyclerView.Adapter<UploadedCloudFileAdapter.ViewHolder> {

    List<UploadedCloudFile> UploadedCloudFileList;
    Context context;

    public UploadedCloudFileAdapter(List<UploadedCloudFile> UploadedCloudFileList) {
        this.UploadedCloudFileList = UploadedCloudFileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
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
        Calendar c = Calendar.getInstance();
        c.setTime(UploadedCloudFile.getLastModified());
        c.add(Calendar.HOUR_OF_DAY, 8);
        Date localDate = c.getTime();
        String truncatedDate = localDate.toString().substring(0, localDate.toString().length() - 9);
        holder.fileName.setText(truncatedFileName);
        holder.fileDate.setText(truncatedDate);

        // SET BUTTON LISTENER
        // TODO/BUG : FIX LINEARLAYOUT BEING NULL IN ViewHolder holder
        if (holder.linearLayout == null) {
            System.out.println("LINEARLAYOUT IS NULL");
        }
        holder.linearLayout.setOnClickListener(v -> System.out.println(truncatedFileName + " " + "CHOSEN"));
    }

    @Override
    public int getItemCount() {
        return UploadedCloudFileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileDate;
        TextView fileName;
        CardView cv;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fileDate = itemView.findViewById(R.id.fileDate);
            fileName = itemView.findViewById(R.id.fileName);
            cv = itemView.findViewById(R.id.cv);
            linearLayout = itemView.findViewById(R.id.uploadedFilesRV);
        }
    }
}
