package com.example.receiptstorageapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FileViewHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public Button delete;
    public Button view;
    public CardView container;
    public ImageView imageFile;
    public FileViewHolder(@NonNull View itemView) {
        super(itemView);
        delete = itemView.findViewById(R.id.deleteButton);
        view = itemView.findViewById(R.id.viewButton);
        name = itemView.findViewById(R.id.fileName);
        container = itemView.findViewById(R.id.container);
        imageFile = itemView.findViewById(R.id.fileImage);
    }
}
