package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Utilities {
    public static BufferedReader getReader(Socket socket) {
        try {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception ex) {
            Log.d(Constants.TAG, "Error when gettign input stream");
        }
        return null;
    }

    public static PrintWriter getWriter(Socket socket) {
        try {
            return new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception ex) {
            Log.d(Constants.TAG, "Error when gettign input stream");
        }
        return null;
    }
}
