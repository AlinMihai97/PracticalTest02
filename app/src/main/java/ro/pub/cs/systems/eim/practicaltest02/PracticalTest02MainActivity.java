package ro.pub.cs.systems.eim.practicaltest02;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class PracticalTest02MainActivity extends AppCompatActivity {

    ServerThread serverThread;
    EditText serverPortEditText;
    Button connectButton;
    TextView weatherForecastTextView;
    EditText clientAddressEditText;
    EditText clientPortEditText;
    EditText cityEditText;
    Spinner informationTypeSpinner;
    Button getWeatherButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical_test02_main_activity);


         serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);
         connectButton = (Button) findViewById(R.id.connect_button);
         weatherForecastTextView = (TextView) findViewById(R.id.weather_forecast_text_view);
         clientAddressEditText = (EditText) findViewById(R.id.client_address_edit_text);
         clientPortEditText = (EditText) findViewById(R.id.client_port_edit_text);
         cityEditText = (EditText) findViewById(R.id.city_edit_text);
         informationTypeSpinner = (Spinner) findViewById(R.id.information_type_spinner);
         getWeatherButton = (Button) findViewById(R.id.get_weather_forecast_button);

        connectButton.setOnClickListener(new ConnectButtonClickListener());
        getWeatherButton.setOnClickListener(new GetWeatherForecastButtonClickListener());
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }

    // subclass for click listener
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }

    }
    private class GetWeatherForecastButtonClickListener implements Button.OnClickListener {

        ClientThread clientThread;

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String city = cityEditText.getText().toString();
            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (city == null || city.isEmpty()
                    || informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            weatherForecastTextView.setText("");

            clientThread = new ClientThread(
                    clientAddress, Integer.parseInt(clientPort), city, informationType, weatherForecastTextView
            );
            clientThread.start();
        }

    }


    private class ClientThread extends Thread {


        String address;
        int port;
        Socket socket;
        String city;
        String informationType;
        TextView weatherForecastTextView;

        ClientThread(String address, int port, String city, String informationType,  TextView weatherForecastTextView ) {
            this.address = address;
            this.port = port;
            this.city = city;
            this.informationType = informationType;
            this.weatherForecastTextView = weatherForecastTextView;
        }
        public void run() {
            try {
                socket = new Socket(address, port);
                if (socket == null) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
                    return;
                }
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader == null || printWriter == null) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
                    return;
                }
                printWriter.println(city);
                printWriter.flush();
                printWriter.println(informationType);
                printWriter.flush();
                String weatherInformation;
                while ((weatherInformation = bufferedReader.readLine()) != null) {
                    final String finalizedWeateherInformation = weatherInformation;
                    weatherForecastTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            weatherForecastTextView.setText(finalizedWeateherInformation);
                        }
                    });
                }
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                        if (Constants.DEBUG) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

