package com.example.receiptstorageapp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private Context context;
    private List<File> fileList;

    public FileAdapter(Context context, List<File> fileList){
        this.context = context;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.name.setText(fileList.get(position).getName());
        holder.name.setSelected(true);
        holder.imageFile.setImageBitmap(BitmapFactory.decodeFile(fileList.get(position).getPath()));
        holder.imageFile.setRotation(90);
        holder.delete.setOnClickListener(v ->
        {
            fileList.get(position).delete();
            fileList.remove(position);
            notifyItemRemoved(position);
        });
        holder.view.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.popup_layout, null);
            ImageView popupView = layout.findViewById(R.id.popupImage);
            popupView.setImageBitmap(BitmapFactory.decodeFile(fileList.get(position).getAbsolutePath()));
            popupView.setRotation(90);
            builder.setView(layout);
            AlertDialog popup = builder.create();
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }
}
