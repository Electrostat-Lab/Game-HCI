package com.myGame.JmEGamePadExample;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.widget.Toast;

import com.myGame.R;
import com.scrappers.superiorExtendedEngine.LANHelper.LanActivity;
import com.scrappers.superiorExtendedEngine.LANHelper.OnAPScanFinished;
import com.scrappers.superiorExtendedEngine.LANHelper.OnConnectionEstablished;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class LanLogic extends LanActivity implements OnAPScanFinished, OnConnectionEstablished {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_logic);
        setOnConnectionEstablished(this);
//        startWifiScan();
        try {
            registerAsClientSide(88,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void success(List<ScanResult> newScanResults) {
        System.out.println(newScanResults);
    }

    @Override
    public void failure(List<ScanResult> oldScanResults) {
        System.out.println(oldScanResults);
    }

    @Override
    public void connectionSuccess(Socket dataSocket) {
        Toast.makeText(getApplicationContext(),dataSocket.getRemoteSocketAddress().toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void connectionFailed(Socket dataSocket) {
        Toast.makeText(getApplicationContext(),dataSocket.getRemoteSocketAddress().toString(),Toast.LENGTH_LONG).show();

    }

}