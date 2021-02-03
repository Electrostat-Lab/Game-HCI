package com.myGame.JmEGamePadExample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Toast;

import com.scrappers.jmeGamePad.BluetoothHelper.BluetoothActivity;
import com.scrappers.jmeGamePad.BluetoothHelper.DataTransform;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import androidx.annotation.Nullable;

public class BluetoothLogic extends BluetoothActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDiscoveryTime(300);
        setOnDiscoveryActivation(new OnDiscoveryActivation() {
            @Override
            public void onDiscoverable() {
                BluetoothLogic.this.registerAsServerSide(3);
            }
        });
        setOnConnectionEstablished(new com.scrappers.jmeGamePad.BluetoothHelper.BluetoothActivity.OnConnectionEstablished() {
            @Override
            public void connectionSuccess(BluetoothSocket bluetoothSocket,boolean connectivity, UUID uuid, String SPDname) {
                Toast.makeText(BluetoothLogic.this.getApplicationContext(), connectivity +" done "+uuid,Toast.LENGTH_LONG).show();
                final DataTransform dataTransform=new DataTransform(bluetoothSocket);
                dataTransform.setOnDataReceivedListener(new DataTransform.OnDataReceivedListener() {
                    @Override
                    public void onDataReceived(String data) {
                        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();
                        dataTransform.write("Done !");
                    }
                });
            }

            @Override
            public void connectionFailed(BluetoothSocket bluetoothSocket,boolean connectivity, UUID uuid, String SPDname) {
                Toast.makeText(BluetoothLogic.this.getApplicationContext(), connectivity +" "+uuid,Toast.LENGTH_LONG).show();
            }
        });
        setOnDeviceFound(new com.scrappers.jmeGamePad.BluetoothHelper.BluetoothActivity.OnDeviceFound() {
            @Override
            public void deviceFound(BluetoothDevice discoveredDevice, ArrayList<BluetoothDevice> discoveredDevices) {
                System.out.println(discoveredDevice+" "+discoveredDevices);
//                BluetoothLogic.this.stopDiscovery();
               }
        });
        setOnDiscoveryTerminated(new OnDiscoveryTerminated() {
            @Override
            public void onDiscoveryTermination(ArrayList<BluetoothDevice> discoveredDevices) {
                Toast.makeText(getApplicationContext(),discoveredDevices.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


}
