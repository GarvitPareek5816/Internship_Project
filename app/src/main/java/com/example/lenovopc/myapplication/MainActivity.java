package com.example.lenovopc.myapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.os.Parcelable;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static Button openCustomGallery;
    private static GridView selectedImageGridView;

    private static final int CustomGallerySelectId = 1;//Set Intent Id66
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value

    public static final int GALLERY_PICTURE = 1;

    Button btn_select, btn_convert;


    boolean boolean_permission;
    boolean boolean_save;
    Bitmap bitmap;
    String filename;
    String val;
    List<String> selectedImages;
    public static final int REQUEST_PERMISSIONS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file);
        initViews();
        setListeners();
        getSharedImages();

        init();
        listener();
        fn_permission();
    }


    private void init()
    {
        btn_select = (Button) findViewById(R.id.openCustomGallery);
//        openCustomGallery =(Button) findViewById(R.id.openCustomGallery) ;
        btn_convert = (Button) findViewById(R.id.btn_convert);
//       ImageView  iv_image = (ImageView) findViewById(R.id.selectedImagesGridView);
//        ImageView selectedImagesGridView = (ImageView) findViewById(R.id.selectedImagesGridView);
    }

    private void listener()
    {
        btn_select.setOnClickListener(this);
        btn_convert.setOnClickListener(this);
    }


    //Init all views
    private void initViews() {
        openCustomGallery = (Button) findViewById(R.id.openCustomGallery);
        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
    }

    //set Listeners
    private void setListeners() {
        openCustomGallery.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.openCustomGallery:
                startActivityForResult(new Intent(MainActivity.this, CustomGallery_Activity.class), CustomGallerySelectId);
                break;

            case R.id.btn_convert:
                if (boolean_save){


                    Intent k = new Intent(getApplicationContext(),PDFViewActivity.class);
                    k.putExtra("filename",filename);
                    k.putExtra("filename",val);
                    startActivity(k);

                }else {
                    createPdf();
                }
                break;
        }

    }

   protected void onActivityResult(int requestcode, int resultcode, Intent imagereturnintent)
    {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode)
        {
            case CustomGallerySelectId:
                if (resultcode == RESULT_OK)
                {
                    String imagesArray = imagereturnintent.getStringExtra(CustomGalleryIntentKey);//get Intent data
                    //Convert string array into List by splitting by ',' and substring after '[' and before ']'
                    selectedImages = Arrays.asList(imagesArray.substring(1, imagesArray.length() - 1).split(", "));
                    loadGridView(new ArrayList<String>(selectedImages));//call load gridview method by passing converted list into arrayList

                    btn_convert.setClickable(true);
                }
                break;

        }
    }

    //Load GridView
    private void loadGridView(ArrayList<String> imagesArray)
    {
        GridView_Adapter adapter = new GridView_Adapter(MainActivity.this, imagesArray, false);
        selectedImageGridView.setAdapter(adapter);
    }

    //Read Shared Images
    private void getSharedImages()
    {

        //If Intent Action equals then proceed
        if (Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && getIntent().hasExtra(Intent.EXTRA_STREAM))
        {
            ArrayList<Parcelable> list =
                    getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);//get Parcelabe list
            ArrayList<String> selectedImages = new ArrayList<>();

            //Loop to all parcelable list
            for (Parcelable parcel : list)
            {
                Uri uri = (Uri) parcel;//get URI
                String sourcepath = getPath(uri);//Get Path of URI
                selectedImages.add(sourcepath);//add images to arraylist
            }
            loadGridView(selectedImages);//call load gridview
        }
    }


    //get actual path of uri
    public String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        PdfDocument NewPDFdocument = new PdfDocument();
        int convertHighet = (int) hight, convertWidth = (int) width;
        //number of images in the grid
        int iNumberofpages = selectedImages.size();
        ArrayList<Bitmap> bitmap = new ArrayList<Bitmap>(iNumberofpages);



        String targetPdf = "/sdcard/LabSolutions/test.pdf";

        //for loop for which for the number of images
        for(int i=0;i<iNumberofpages;i++)
        {

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) width, (int) hight,i+1).create();
            PdfDocument.Page page = NewPDFdocument.startPage(pageInfo);
            String CurrentFilePath;
            Bitmap LocalBitmap;

            Canvas canvas = page.getCanvas();

            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);

            Resources mResources = getResources();
            CurrentFilePath = selectedImages.get(i).toString();
            bitmap.add(BitmapFactory.decodeFile(CurrentFilePath));
            LocalBitmap = bitmap.get(i);
             LocalBitmap = LocalBitmap.createScaledBitmap(LocalBitmap, LocalBitmap.getWidth(), LocalBitmap.getHeight(), true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(LocalBitmap, 0, 0 , null);
            NewPDFdocument.finishPage(page);


        }

        selectedImageGridView = (GridView) findViewById(R.id.selectedImagesGridView);
        String backgroundImageName = String.valueOf(selectedImageGridView.getTag());


        File folder = new File(Environment.getExternalStorageDirectory() + "/LabSolutions");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }


        File filePath = new File(targetPdf);
        try
        {
            NewPDFdocument.writeTo(new FileOutputStream(filePath));
            btn_convert.setText("Check PDF");
            boolean_save=true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        NewPDFdocument.close();
    }


    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
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
