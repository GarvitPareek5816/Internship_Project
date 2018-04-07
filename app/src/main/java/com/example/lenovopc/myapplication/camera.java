package com.example.lenovopc.myapplication;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class camera extends Activity {


    Button button;
    ImageView imageView;
    static final int CAM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        button= (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.image_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, CAM_REQUEST);

/* ---- customized code ---------*/
                AlertDialog.Builder alert = new AlertDialog.Builder(camera.this);

                alert.setTitle("Title");
                //   alert.setMessage("Message");

                // Set an EditText view to get user input
                final EditText input = new EditText(camera.this);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String value = String.valueOf(input.getText());
                        // Do something with value!


                        File file = getFile(value);

                        Uri photoURI = FileProvider.getUriForFile(camera.this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file);
                        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
//                        startActivityForResult(camera_intent, CAM_REQUEST);

                    }

                });

               /* alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });*/

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
        imageView.setImageDrawable(Drawable.createFromPath(path));
    }


}
