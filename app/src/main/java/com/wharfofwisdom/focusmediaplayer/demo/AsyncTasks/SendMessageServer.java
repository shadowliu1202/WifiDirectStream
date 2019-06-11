package com.wharfofwisdom.focusmediaplayer.demo.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.wharfofwisdom.focusmediaplayer.demo.Entities.Message;
import com.wharfofwisdom.focusmediaplayer.demo.ServerInit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SendMessageServer extends AsyncTask<Message, Message, Message> {
    private static final int SERVER_PORT = 4446;

    public SendMessageServer() {
    }

    @Override
    protected Message doInBackground(Message... msg) {
        publishProgress(msg);
        try {
            ArrayList<InetAddress> listClients = ServerInit.clients;
            for (InetAddress addr : listClients) {
                msg[0].setUser_record("From Owner");
                if (msg[0].getSenderAddress() != null && addr.getHostAddress().equals(msg[0].getSenderAddress().getHostAddress())) {
                    return msg[0];
                }
                Socket socket = new Socket();
                socket.setReuseAddress(true);
                socket.bind(null);
                socket.connect(new InetSocketAddress(addr, SERVER_PORT));
                OutputStream outputStream = socket.getOutputStream();
                new ObjectOutputStream(outputStream).writeObject(msg[0]);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg[0];
    }

    @Override
    protected void onProgressUpdate(Message... values) {
        super.onProgressUpdate(values);
        Log.e("Test", "onProgressUpdate Server Send" + values[0].getmText());
        Log.e("Test", "onProgressUpdate Server Send" + values[0].getChatName());
        Log.e("Test", "onProgressUpdate Server Send" + values[0].getFileName());
    }

    @Override
    protected void onPostExecute(Message result) {
        super.onPostExecute(result);
    }
}