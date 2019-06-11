package com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiveMessageServer extends AbstractReceiver {
    private static final int SERVER_PORT = 4445;
    private Context mContext;
    private ServerSocket serverSocket;

    public ReceiveMessageServer(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream objectIS = new ObjectInputStream(inputStream);
                Message message = (Message) objectIS.readObject();
                InetAddress senderAddr = clientSocket.getInetAddress();
                message.setSenderAddress(senderAddr);
                clientSocket.close();
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
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Message... values) {
        super.onProgressUpdate(values);
        playNotification(mContext, values[0]);
        Log.e("Test", "onProgressUpdate Server Receive" + values[0].getmText());
        Log.e("Test", "onProgressUpdate Server Receive" + values[0].getChatName());
        Log.e("Test", "onProgressUpdate Server Receive" + values[0].getFileName());

        //If the message contains a video or an audio, we saved this file to the external storage
        int type = values[0].getmType();
        if (type == Message.AUDIO_MESSAGE || type == Message.VIDEO_MESSAGE || type == Message.FILE_MESSAGE || type == Message.DRAWING_MESSAGE) {
            values[0].saveByteArrayToFile(mContext);
        }
        Intent in = new Intent();
        in.putExtra("message", values[0].getmText());
        in.putExtra("mission", values[0].getChatName());
        in.setAction("NOW");
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(in);
        new SendMessageServer().executeOnExecutor(THREAD_POOL_EXECUTOR, values);
    }

}
