package com.wharfofwisdom.focusmediaplayer.demo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.ReceiveMessageClient;
import com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks.ReceiveMessageServer;
import com.wharfofwisdom.focusmediaplayer.presentation.AdvertisementActivity;


public class MessageService extends Service {
    private static final String TAG = "MessageService";
    public interface IOwnerService {
        void changeOwner(boolean isOwner);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (AdvertisementActivity.isMaster) {
            Log.v(TAG, "Start the AsyncTask for the server to receive messages");
            new ReceiveMessageServer(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        } else {
            Log.v(TAG, "Start the AsyncTask for the client to receive messages");
            new ReceiveMessageClient(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        }
        return START_STICKY;
    }
}
