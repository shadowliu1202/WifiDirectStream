package com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.domain.interactor.AdvertisementRepository;
import com.wharfofwisdom.focusmediaplayer.domain.repository.db.RoomRepository;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Scheduler;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class ReceiveMessageClient extends AbstractReceiver {
    private static final int SERVER_PORT = 4446;
    private Context mContext;
    private ServerSocket socket;
    private AdvertisementRepository repository;

    public ReceiveMessageClient(Context context) {
        mContext = context;
        repository = new RoomRepository(context);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            socket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket destinationSocket = socket.accept();

                InputStream inputStream = destinationSocket.getInputStream();
                BufferedInputStream buffer = new BufferedInputStream(inputStream);
                ObjectInputStream objectIS = new ObjectInputStream(buffer);
                Message message = (Message) objectIS.readObject();
                destinationSocket.close();
                publishProgress(message);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onCancelled() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Message... values) {
        super.onProgressUpdate(values);
        //If the message contains a video or an audio, we saved this file to the external storage
        int type = values[0].getmType();
        if (type == Message.AUDIO_MESSAGE || type == Message.VIDEO_MESSAGE || type == Message.FILE_MESSAGE || type == Message.DRAWING_MESSAGE) {
            File file = values[0].saveByteArrayToFile(mContext);
            repository.addVideoCache(file, values[0].getChatName(), values[0].getFileName())
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
        Toast.makeText(mContext, values[0].getmText(), Toast.LENGTH_SHORT).show();
        Log.e("Test", "onProgressUpdate Client Receive" + values[0].getmText());
        Log.e("Test", "onProgressUpdate Client Receive" + values[0].getChatName());
        Log.e("Test", "onProgressUpdate Client Receive" + values[0].getFileName());
    }

    @SuppressWarnings("rawtypes")
    public Boolean isActivityRunning(Class activityClass) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }
}
