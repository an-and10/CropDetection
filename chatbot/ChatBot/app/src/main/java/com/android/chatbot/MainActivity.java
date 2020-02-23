package com.android.chatbot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private  Button uploadBtn;
    private  ImageView Bgallery;
    private Uri postImageUri =null;

    Toolbar toolbar;
    Bitmap bitmap = null;
    Module module = null;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadBtn = findViewById(R.id.uploadBtn);
        Bgallery = findViewById(R.id.bgallery);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,"Please Wait","Processing...",true);
                 pDialog = new ProgressDialog(MainActivity.this); //Your Activity.this
                pDialog.setMessage("Loading...");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);
                pDialog.show();
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // creating bitmap from packaged into app android asset 'image.jpg',
                            // app/src/main/assets/image.jpg
                            InputStream input = getContentResolver().openInputStream(postImageUri);
                            bitmap = BitmapFactory.decodeStream(input);

                            // loading serialized torchscript module from packaged into app android asset model.pt,
                            // app/src/model/assets/model.pt
                            module = Module.load(assetFilePath(MainActivity.this, "final_model.pt"));
                        } catch (IOException e) {
                            Log.e("Crop Classifier ", "Error reading assets", e);
                            finish();
                        }

                        // preparing input tensor
                        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
                                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

                        // running the model
                        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

                        // getting tensor content as java array of floats
                        final float[] scores = outputTensor.getDataAsFloatArray();

                        // searching for the index with maximum score
                        float maxScore = -Float.MAX_VALUE;
                        int maxScoreIdx = -1;
                        for (int i = 0; i < scores.length; i++) {
                            if (scores[i] > maxScore) {
                                maxScore = scores[i];
                                maxScoreIdx = i;
                            }
                        }
                        pDialog.dismiss();

                        String className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];
                       // Toast.makeText(MainActivity.this, "ClassName : "+className, Toast.LENGTH_SHORT).show();


                        Intent nextIntent = new Intent(MainActivity.this, ResultActivity.class);
                        nextIntent.putExtra("ClassName",className);
                        startActivity(nextIntent);

                      //  Toast.makeText(MainActivity.this, "Next Activity", Toast.LENGTH_SHORT).show();
                    }
                };
                thread.start();



            }

        });

        Bgallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Opening Gallery", Toast.LENGTH_SHORT).show();
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(MainActivity.this);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater = new MenuInflater(MainActivity.this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case  R.id.about:
                Toast.makeText(this, "About Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "Settings Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(this, "Contact Selected", Toast.LENGTH_SHORT).show();
                break;
            case R.id.help:

                Toast.makeText(this, "Help Selected", Toast.LENGTH_SHORT).show();

                break;

            default:
                return  false;

        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Enter this", Toast.LENGTH_SHORT).show();
                postImageUri=  result.getUri();
                Bgallery.setImageURI(postImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, "Error: "+ error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pDialog.dismiss();
    }
}
