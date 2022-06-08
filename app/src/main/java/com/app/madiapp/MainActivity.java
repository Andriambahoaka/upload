package com.app.madiapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    protected static String LOG_NAME = "HelloWorld";
    static String ROOT_URL = "http://10.0.75.1/upload/";
    static int IMAGE_PICKING = 12;
    static int IMAGE_CAPTURE = 11;
    ArrayList<Bitmap> pictures;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pictures = new ArrayList<Bitmap>();
    }


    public String convertToString(Bitmap bitmap){
        ByteArrayOutputStream fileOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100 ,fileOutputStream);
        byte[] array = fileOutputStream.toByteArray();
        return Base64.encodeToString(array,Base64.DEFAULT);
    }

    public void uploadPhoto (View view){
        Intent s = new Intent(this,FileService.class);
      /*  for(int i=0;i<pictures.size();i++){
            s.putParcelableArrayListExtra("PIC", pictures.get(0));
        }*/
      //  s.putExtra("PIC",pictures.get(0));
        //Log.v("SIZEEEEEEEEEEEEEEEEEEEE", String.valueOf(pictures.size()));
       // startService(s);


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ROOT_URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            //JSONObject obj = new JSONObject(new String(response.data));
                            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                            Toast.makeText(getApplicationContext(),json, Toast.LENGTH_SHORT).show();
                            Log.e("FILEMAN", String.valueOf(response.data));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
             /*   for(Bitmap bite: pictures){
                    String string = convertToString(bite);
                    params.put("name",string);
                }*/

                for(int i=0;i<pictures.size();i++){
                    String string = convertToString(pictures.get(i));
                    params.put("image"+i,string);
                    Log.v("loghiujhijijij", String.valueOf(i));
                }





   /*
                params.put("name", convertToString(bitmap));
                Log.v("PARAMS",params+"");
                */
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);






    }

  public void loadPhoto(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, ""), IMAGE_PICKING);
    }
    //camera
   /*public void loadPhoto(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(Intent.createChooser(intent, ""), IMAGE_CAPTURE);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_PICKING && data != null){
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            int n=0;
            if(filePathColumn!=null){
                n=filePathColumn.length;
            }
            if(data.getData()!=null){
                putPicture(data.getData(),1);
            }else{

                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        putPicture(uri,i+1);

                    }
                }
            }
        }
        if(requestCode == IMAGE_CAPTURE && data != null){
            Bundle extras = data.getExtras();
            Bitmap img = (Bitmap) extras.get("data");
            ImageView p = findViewById(R.id.photo);
            p.setImageBitmap(img);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());;
            String fileName = "IMG_"+ timeStamp + ".jpg";
            File tmpFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            pictures.add(img);
            Log.v(LOG_NAME,"file:"+pictures.get(0));
        }
    }

    private void putPicture(Uri uri,int indice){
        Bitmap btm = getBitmap(this,uri);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        timeStamp+=""+indice;
        String fileName = "IMG_"+ timeStamp + ".jpg";

        try {
            File tmpFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            btm = rotateBitmap(btm,uri);
            pictures.add(btm);
            Log.v(LOG_NAME,"FILEEEEEEEEEEEEEEEEEEEEEEEEEE:"+pictures.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertBitmaptoFile(File destinationFile , Bitmap bitmap) {
        try {
            //create a file to write bitmap data
            destinationFile.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapData = bos.toByteArray();
            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(destinationFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return destinationFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getBitmap(Context context, Uri imageUri){
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, Uri uri) throws IOException {
        int []orientation = getOrientation(uri);
        Matrix matrix = new Matrix();
        if(orientation[0] == 0)
            return bitmap;
        matrix.setRotate(orientation[0]);
        if(orientation[1]<0)
            matrix.setScale(-1,1);
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    private int [] getOrientation(Uri uri){
        int []orientation = getOrientationFromExif(uri);
        if(orientation[0] < 0){
            int rotation = getOrientationFromMediaStore(this,uri);
            orientation[0] = rotation;
        }
        return orientation;
    }

    private int [] getOrientationFromExif(Uri uri) {
        int []result = new int[2];
        result[0] = -1;
        result[1] = 1;
        try {
            InputStream in;
            in = this.getContentResolver().openInputStream(uri);

            ExifInterface exif;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                exif = new ExifInterface(in);
            }else{
                FileUtils fileUtils = new FileUtils(this);
                String path = fileUtils.getPath(uri);
                exif = new ExifInterface(path);
            }
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    result[0] = 0;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    result[0] = 0;
                    result[1] = -1;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    result[0] = 180;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    result[0] = 180;
                    result[1] = -1;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    result[0] = 90;
                    result[1] = -1;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    result[0] = 90;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    result[0] = -90;
                    result[1] = -1;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    result[0] = -90;
                    break;
                default:
                    return result;
            }
        } catch (IOException e) {
            Log.e("LOG_TAG", "Unable to get image exif orientation", e);
        }

        return result;
    }

    private int getOrientationFromMediaStore(Context context, Uri imageUri) {
        if(imageUri == null) {
            return -1;
        }

        String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, projection, null, null, null);

        int orientation = -1;
        if (cursor != null && cursor.moveToFirst()) {
            orientation = cursor.getInt(0);
            cursor.close();
        }
        return orientation;
    }

}