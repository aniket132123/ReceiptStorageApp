package com.example.receiptstorageapp;


import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.tesseract.android.TessBaseAPI;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<File> fileList;
    private FileAdapter fileAdapter;
    private String extractedText;
    File receiptStorage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        receiptStorage = getFilesDir();

        setContentView(R.layout.activity_display);

        displayFiles();
//        try {
//            analyzeFiles(receiptStorage);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        Toast.makeText(this, extractedText, Toast.LENGTH_LONG).show();
    }

    public ArrayList<File> findFiles(File folder){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = folder.listFiles();

        assert files != null;
        for (File singleFile : files){
            if (singleFile.getName().toLowerCase().endsWith(".jpeg"))
                arrayList.add(singleFile);
        }

        return arrayList;
    }

    public void analyzeFiles(File folder) throws IOException {
        File[] files = folder.listFiles();
        TessBaseAPI ocrEngine = new TessBaseAPI();

        try {
            copyTessDataFiles();
        } catch (IOException e){
            e.printStackTrace();
        }

        ocrEngine.init(getFilesDir().toString(), "eng");
        assert files != null;
        ocrEngine.setImage(files[files.length-1]);
        extractedText = ocrEngine.getUTF8Text();
        ocrEngine.end();

    }

    private void copyTessDataFiles() throws IOException {
        AssetManager assetManager = getAssets();
        String[] files = assetManager.list("tessdata");
        assert files != null;
        // starts input stream from assets folder
        InputStream in = assetManager.open("tessdata/" + files[0]);
        // creates output directory and output file
        File bufferFolder = new File(getFilesDir() + "/tessdata/");
        File dataFile = new File(getFilesDir() + "/tessdata/" + files[0]);
        if (!bufferFolder.exists()){
            bufferFolder.mkdirs();
            dataFile.createNewFile();
        }
        // writes asset data to internal storage
        OutputStream out = new FileOutputStream(dataFile);
        copyFile(in, out);
        in.close();
        out.flush();
        out.close();

    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void displayFiles(){
        recyclerView = findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(DisplayActivity.this));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(receiptStorage));
        fileAdapter = new FileAdapter(DisplayActivity.this, fileList);
        recyclerView.setAdapter(fileAdapter);
    }

}
