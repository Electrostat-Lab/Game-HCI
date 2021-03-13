package com.myGame.JmEGamePadExample;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Toast;

import com.scrappers.superiorExtendedEngine.bluetoothHelper.BluetoothActivity;
import com.scrappers.superiorExtendedEngine.bluetoothHelper.DataTransform;

import java.util.ArrayList;
import java.util.UUID;

import androidx.annotation.Nullable;

public class BluetoothLogic extends BluetoothActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDiscoveryTime(300);
        setOnDiscoveryActivation(() -> {

        });
        setOnConnectionEstablished(new com.scrappers.superiorExtendedEngine.bluetoothHelper.BluetoothActivity.OnConnectionEstablished() {
            @Override
            public void connectionSuccess(BluetoothSocket bluetoothSocket,boolean connectivity, UUID uuid, String SPDname) {
                Toast.makeText(BluetoothLogic.this.getApplicationContext(), connectivity +" done "+uuid,Toast.LENGTH_LONG).show();
                final DataTransform dataTransform=new DataTransform(bluetoothSocket);
                dataTransform.setOnDataReceivedListener(new DataTransform.OnDataReceivedListener() {
                    @Override
                    public <T> void onDataReceived(T data) {
                        Toast.makeText(getApplicationContext(),new String((byte[]) data),Toast.LENGTH_LONG).show();
                        dataTransform.write(2);
                    }
                });
            }

            @Override
            public void connectionFailed(BluetoothSocket bluetoothSocket,boolean connectivity, UUID uuid, String SPDname) {
                Toast.makeText(BluetoothLogic.this.getApplicationContext(), connectivity +" "+uuid,Toast.LENGTH_LONG).show();
            }
        });
        setOnDeviceFound((discoveredDevice, discoveredDevices) -> {
            System.out.println(discoveredDevice+" "+discoveredDevices);
//                BluetoothLogic.this.stopDiscovery();
            BluetoothLogic.this.registerAsClientSide(discoveredDevice,100);

        });
        setOnDiscoveryTerminated(new OnDiscoveryTerminated() {
            @Override
            public void onDiscoveryTermination(ArrayList<BluetoothDevice> discoveredDevices) {
                Toast.makeText(getApplicationContext(),discoveredDevices.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


}
