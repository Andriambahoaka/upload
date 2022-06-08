package com.app.madiapp;

import static java.lang.Thread.sleep;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

public class FileService extends Service {
    static String TAG = "FileService";
    //static String ROOT_URL = "10.13.104.39/~Adriela/upload_madiaapp/";
    //static String ROOT_URL = "10.0.2.2/upload/";
    static String ROOT_URL = "http://10.0.75.1/upload/";
    private NotificationService ns;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        NotificationManager ntm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        ns = new NotificationService(this,getApplicationContext(),ntm);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null){
            String f_name = intent.getStringExtra("PIC");
            ns.setFile_name("f_name");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.i(TAG, "onStart");
        try {
            ns.notifyMadiaapp();
            //String f_name = intent.getStringExtra("PIC");
            List<Bitmap> receive=(List<Bitmap>)intent.getParcelableExtra("PIC");
            Log.v("HUHUHUHUHUHUHUHUHUHHUHU",receive.toString());
       /*     List<Bitmap> receive = new ArrayList<Bitmap>();
            int intValue = intent.getIntExtra("size", 0);
            for(int i=0;i<intValue;i++){
                Bitmap b= (Bitmap) intent.getParcelableExtra("PIC"+i);
                receive.add(b);
            }*/

            Bitmap b= (Bitmap) intent.getParcelableExtra("PIC");

            sleep(3000);
            this.uploadFile(b);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //private void uploadFile(final List<Bitmap> bitmap) {
        private void uploadFile(final Bitmap bitmap) {


    }
}
