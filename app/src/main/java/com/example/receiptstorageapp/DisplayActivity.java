package com.example.receiptstorageapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;


public class DisplayActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<File> fileList;
    private FileAdapter fileAdapter;
    private File receiptStorage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        if (OpenCVLoader.initLocal())
            Log.i("OPENCV", "OPENCV LOADED SUCCESSFULLY");
        receiptStorage = getFilesDir();
        displayFiles();
        try {
            analyzeFiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void analyzeFiles() throws IOException {
        // file to bitmap
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(receiptStorage));
        String imageFilePath = fileList.get(fileList.size() - 1).getAbsolutePath();
        Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
        Mat imageMat = new Mat();
        assert imageBitmap != null;
        Utils.bitmapToMat(imageBitmap, imageMat);

        //preprocessing
        Mat greyImage = new Mat();
        Mat thresholdImage = new Mat();

        //greyscaling
        Imgproc.cvtColor(imageMat, greyImage, Imgproc.COLOR_BGR2GRAY);

        //thresholding
        Imgproc.threshold(greyImage, thresholdImage, 128, 255, Imgproc.THRESH_BINARY);

        Utils.matToBitmap(thresholdImage, imageBitmap);
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
        //text recognition
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(inputImage)
                .addOnSuccessListener(
                        text -> {
                            if(text != null){
                                Log.i("EXTRACTEDTEXT", text.getText());
                            } else Toast.makeText(this, "text found but no result", Toast.LENGTH_SHORT).show();
                        }
                ).addOnFailureListener(
                        e -> {
                            Toast.makeText(this, "text not found", Toast.LENGTH_SHORT).show();
                        }
                );
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
