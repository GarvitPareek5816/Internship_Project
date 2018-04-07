package com.example.lenovopc.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.File;
import java.io.IOException;

public class start extends AppCompatActivity {
    private static final int REQUEST_CODE = 99;
    private Button scanButton;
    private Button cameraButton;
    private Button mediaButton;
    private Button compress;
    private Button camera;
    private Button crop;
    private Button pdf;
    private Button OCR;
    private Button QR;
    private Button status;
    private Button onepdf, pdfbox;
    private Button PDFextract;
    private ImageView scannedImageView;
    static final int CAM_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        init();
    }

    private void init() {

        compress = (Button) findViewById(R.id.compress);
        camera = (Button) findViewById(R.id.camera);
        crop = (Button) findViewById(R.id.crop);
        scannedImageView = (ImageView) findViewById(R.id.scannedImage);
        pdf = (Button) findViewById(R.id.PDF);
        OCR = (Button) findViewById(R.id.OCR);
        QR = (Button) findViewById(R.id.QR);
        status = (Button) findViewById(R.id.status);
        onepdf = (Button) findViewById(R.id.onepdf);
//        PDFextract = (Button) findViewById(R.id.PDFextract);
//        pdfbox = (Button) findViewById(R.id.pdfbox);

        compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, compression.class);
                startActivity(i);
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, crop.class);
                startActivity(i);
            }
        });

        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, MainActivity.class);
                startActivity(i);
            }
        });


        OCR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, ocr.class);
                startActivity(i);
            }
        });

        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, QR.class);
                startActivity(i);
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, driveractivity.class);
                startActivity(i);
            }
        });

        onepdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, singlepdf.class);
                startActivity(i);
            }
        });

       /* PDFextract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, PDFextract.class);
                startActivity(i);
            }
        });
*/

    /*    splitone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, singlepdf.class);
                startActivity(i);
            }
        });


        splitall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start.this, singlepdf.class);
                startActivity(i);
            }
        });*/


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /* Intent i = new Intent(start.this, camera.class);
                startActivity(i);*/

                final Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(camera_intent, CAM_REQUEST);

                AlertDialog.Builder alert = new AlertDialog.Builder(start.this);

                alert.setTitle("Title");
                //   alert.setMessage("Message");

                // Set an EditText view to get user input
                final EditText input = new EditText(start.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String value = String.valueOf(input.getText());
                        // Do something with value!


                        File file = getFile(value);

                        Uri photoURI = FileProvider.getUriForFile(start.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file);
                        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(camera_intent, CAM_REQUEST);

                    }

                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
    }


    private File getFile(String value)
    {
        File folder = new File(Environment.getExternalStorageDirectory() + "/eMax_Cam/");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        File image_file = new File(folder, String.format("%s.jpg", value));
        return image_file;
    }


    @Override

    protected void onActivityResult(int requestcode, int resultcode, Intent data)
    {
        String path = data.getStringExtra(MediaStore.EXTRA_OUTPUT);
        scannedImageView.setImageDrawable(Drawable.createFromPath(path));
    }

}