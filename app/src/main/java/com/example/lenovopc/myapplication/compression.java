package com.example.lenovopc.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Random;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class compression extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView actualImageView;
    private ImageView compressedImageView;
    private TextView actualSizeTextView;
    private TextView compressedSizeTextView;
    private File actualImage;
    private File compressedImage;
    File eMaxDirectory;

    Bitmap bitmap;
    OutputStream output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compress);
        actualImageView = (ImageView) findViewById(R.id.actual_image);
        compressedImageView = (ImageView) findViewById(R.id.compressed_image);
        actualSizeTextView = (TextView) findViewById(R.id.actual_size);
        compressedSizeTextView = (TextView) findViewById(R.id.compressed_size);

        actualImageView.setBackgroundColor(getRandomColor());
        clearImage();
        eMaxDirectory = Environment.getExternalStoragePublicDirectory("/eMax_Compression");
        assert eMaxDirectory != null;
        if (!eMaxDirectory.exists())
            eMaxDirectory.mkdir();
    }

    public void chooseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void compressImage(View view)
    {
        if (actualImage == null)
        {
            showError("Please choose an image!");
        }
        else
        {

            // Compress image using RxJava in background thread
            new Compressor(this)
                    .compressToFileAsFlowable(actualImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<File>()
                    {
                        @Override
                        public void accept(File file)
                        {
                            compressedImage = file;
                            String fileName = compressedImage.getName();
                            File newPath = new File(eMaxDirectory, fileName);
                            FileUtil.moveFile(compressedImage, newPath);
                            setCompressedImage(newPath);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            throwable.printStackTrace();
                            showError(throwable.getMessage());
                        }
                    });
        }



    }


    private void setCompressedImage(File newPath) {
        compressedImageView.setImageBitmap(BitmapFactory.decodeFile(newPath.getAbsolutePath()));
        compressedSizeTextView.setText(String.format("Size : %s", getReadableFileSize(newPath.length())));

        Toast.makeText(this, "Compressed image save in " + newPath.getPath(), Toast.LENGTH_LONG).show();
        Log.d("Compressor", "Compressed image save in " + newPath.getPath());

//        Intent k = new Intent(getApplicationContext(),MainActivity.class);
//        k.putExtra("compressedImage",compressedImage);   // key pair value
//        startActivity(k);

     /*   File file = getFile();
        Uri photoURI = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                file);*/


    }



  /*  private static File getFile()
    {
        File folder = new File(Environment.getExternalStorageDirectory() + "/eMax_Compression/");
        if(!folder.exists())
            folder.mkdir();
     //   File image_file = new File(folder, String.format("%s.jpg", fileName1));
      //  return image_file;
         return null;
    }
*/



    // camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
    // String[] filePathColumn = {MediaStore.Images.Media.DATA};


    private void clearImage() {
        actualImageView.setBackgroundColor(getRandomColor());
        compressedImageView.setImageDrawable(null);
        compressedImageView.setBackgroundColor(getRandomColor());
        compressedSizeTextView.setText("Size : -");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            //                String realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
            String path =  FileUtil.getRealPathFromURI_API19(this, data.getData());
            actualImageView.setImageBitmap(BitmapFactory.decodeFile(path));
            actualImage = new File(path);
            actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
            clearImage();
        }
    }

    public void showError(String errorMessage)
    {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private int getRandomColor()
    {
        Random rand = new Random();
        return Color.argb(100, rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    public String getReadableFileSize(long size)
    {
        if (size <= 0)
        {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }






}