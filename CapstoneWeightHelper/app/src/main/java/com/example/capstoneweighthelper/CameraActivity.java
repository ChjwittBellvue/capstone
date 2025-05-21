/*
* References
*
* GitHub (2022, May 16). CameraXTrial. Retrieved April 12, 2025, from https://github.com/farazxsiddiqui/CameraX/blob/master/app/src/main/java/com/example/cameraxtrial/MainActivity.java
 */
package com.example.capstoneweighthelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.capstoneweighthelper.pojo.WeightHistoryRecord;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageButton bTakePicture;
    private ImageCapture imageCapture;
    private File picture;
    private WeightHistoryRecord historyRecord = new WeightHistoryRecord();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        copyIntent();

        bTakePicture = findViewById(R.id.take_picture);
        previewView = findViewById(R.id.previewView);

        bTakePicture.setOnClickListener(this);

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                startCameraX(cameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }, getExecutor());


    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

    /**
     * Starts the camera
     *
     * @param cameraProvider
     */
    @SuppressLint("RestrictedApi")
    private void startCameraX(ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        Preview preview = new Preview.Builder().build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    /**
     * Takes the picture
     */
    private void capturePhoto() {

        picture = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CapstoneWeightHelper");
        boolean isDirectoryCreated = picture.exists() || picture.mkdirs();

        if(isDirectoryCreated) {
            Date date = new Date();
            Long pictureName = date.getTime();
            picture = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/CapstoneWeightHelper",  "pic" +pictureName+ ".jpg"); //test3 doesn't work
            ImageCapture.OutputFileOptions.Builder outputFileOptionsBuilder =
                    new ImageCapture.OutputFileOptions.Builder(picture);

            imageCapture.takePicture(outputFileOptionsBuilder.build(), Runnable::run, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    historyRecord.setPictureLocation(picture.getPath());
                    moveToEntry();
                }

                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    exception.printStackTrace();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.take_picture) {
                capturePhoto();
        }
    }

    /**
     * Gets variables shared between pages
     */
    private void copyIntent() {
        if (getIntent().hasExtra("recordId")) {
            historyRecord.setId(getIntent().getExtras().getInt("recordId"));
        }
        if (getIntent().hasExtra("recordWeight")) {
            historyRecord.setEntryWeight(getIntent().getExtras().getDouble("recordWeight"));
        }
        if (getIntent().hasExtra("recordDate")) {
            historyRecord.setEntryDate(getIntent().getExtras().getLong("recordDate"));
        }
        if (getIntent().hasExtra("pictureUri")) {
            historyRecord.setPictureLocation(getIntent().getExtras().getString("pictureUri"));
        }
    }

    /**
     * Handles page movement
     */
    private void moveToEntry() {
        Intent intent = new Intent(this, WeightEntry.class);
        System.out.println("HISTORY RECORD: " + historyRecord);
        if (historyRecord.getId()!=null) {
            intent.putExtra("recordId", historyRecord.getId());
        }
        if (historyRecord.getEntryDate()!=null) {
            intent.putExtra("recordDate", historyRecord.getEntryDate());
        }
        if (historyRecord.getEntryWeight()!=null) {
            intent.putExtra("recordWeight", historyRecord.getEntryWeight());
        }
        intent.putExtra("pictureUri", historyRecord.getPictureLocation());
        startActivity(intent);
    }
}