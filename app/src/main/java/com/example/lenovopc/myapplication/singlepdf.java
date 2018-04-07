package com.example.lenovopc.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class singlepdf extends AppCompatActivity implements View.OnClickListener{

    public static final int GALLERY_PICTURE = 1;
    Button btn_select, btn_convert;
    ImageView iv_image;
    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    String filename;
    public static final int REQUEST_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlepdf);
        init();
        listener();
        fn_permission();
    }

    private void init()
    {
        btn_select = (Button) findViewById(R.id.btn_select);
        btn_convert = (Button) findViewById(R.id.btn_convert);
        iv_image = (ImageView) findViewById(R.id.iv_image);
    }

    private void listener()
    {
        btn_select.setOnClickListener(this);
        btn_convert.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_PICTURE);
                break;

            case R.id.btn_convert:
                if (boolean_save){

//                    Intent intent1=new Intent(getApplicationContext(),PDFViewActivity.class);
//                    startActivity(intent1);
                    Intent k = new Intent(getApplicationContext(),MainActivity.class);
                    k.putExtra("filename",filename);   // key pair value
                    startActivity(k);

                }else {
                    createPdf();
                }
                break;


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf()
    {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();


        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffff"));
        canvas.drawPaint(paint);



        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // Obtain image Name
        ImageView iv_image = (ImageView) findViewById(R.id.iv_image);
        String backgroundImageName = String.valueOf(iv_image.getTag());

        // write the document content
        String targetPdf = "/sdcard/eMAx/"+filename+".pdf";

        File folder = new File(Environment.getExternalStorageDirectory() + "/eMAx");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

//        File filePath = new File(String.valueOf(targetPdf));
        File filePath = new File(targetPdf);
        try
        {
            document.writeTo(new FileOutputStream(filePath));
            btn_convert.setText("Success");
            btn_convert.setEnabled(false);
            boolean_save=true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICTURE && resultCode == RESULT_OK) {

            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                filename = new File(filePath).getName();
                cursor.close();


                bitmap = BitmapFactory.decodeFile(filePath);
                iv_image.setImageBitmap(bitmap);


                btn_convert.setClickable(true);
            }
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(singlepdf.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(singlepdf.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(singlepdf.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(singlepdf.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }
}
