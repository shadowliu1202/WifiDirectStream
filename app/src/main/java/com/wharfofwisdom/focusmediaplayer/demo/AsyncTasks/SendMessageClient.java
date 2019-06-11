package com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendMessageClient extends AsyncTask<Message, Message, Message> {
    private static final int SERVER_PORT = 4445;
    private InetAddress mServerAddr;

    public SendMessageClient(InetAddress serverAddr) {
        mServerAddr = serverAddr;
    }

    @Override
    protected Message doInBackground(Message... msg) {
        Socket socket = new Socket();
        try {
            socket.setReuseAddress(true);
            socket.bind(null);
            socket.connect(new InetSocketAddress(mServerAddr, SERVER_PORT));
            OutputStream outputStream = socket.getOutputStream();
            new ObjectOutputStream(outputStream).writeObject(msg[0]);
            Log.e("Test", "onProgressUpdate Client Receive" + msg[0].getmText());
            Log.e("Test", "onProgressUpdate Client Receive" + msg[0].getChatName());
            Log.e("Test", "onProgressUpdate Client Receive" + msg[0].getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return msg[0];
    }
}
