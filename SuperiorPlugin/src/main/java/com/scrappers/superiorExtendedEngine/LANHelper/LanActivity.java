package com.scrappers.superiorExtendedEngine.LANHelper;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LanActivity extends AppCompatActivity {
    private static final int ENABLE_WIFI_REQUEST=1;
    private static final int SCAN_INTENT = 2;
    private WifiManager wifiManager;
    private OnAPScanFinished onAPScanFinished;
    private OnConnectionEstablished onConnectionEstablished;
    public final ScheduledExecutorService executorService= Executors.newScheduledThreadPool(2);
    private final WifiScanBroadCast wifiScanBroadCast= new WifiScanBroadCast();
    private final LanClientSide lanClientSide=new LanClientSide();
    private final LanServerSide lanServerSide= new LanServerSide();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    protected void initializeLAN(){
        wifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            /*request ACCESS_FINE_LOCATION permission for over L devices*/
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                this.requestPermissions(new String[]{Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.ACCESS_WIFI_STATE
                        ,Manifest.permission.ACCESS_FINE_LOCATION}, ENABLE_WIFI_REQUEST);
            }else {
                if(wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED || !wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(true);
                }
            }

    }
    protected void startWifiScan(){
        if(wifiManager==null){
            return;
        }
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            /*Listen for discovery events change*/
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            this.registerReceiver(wifiScanBroadCast, intentFilter);
            wifiManager.startScan();
        }else{
            Toast.makeText(getApplicationContext(),"Device doesn't support wifi scan through apps !",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initializeLAN();
    }

    private class WifiScanBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
                if(intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                && intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) ){
                    if(onAPScanFinished!=null){
                        onAPScanFinished.success(wifiManager.getScanResults());
                    }
                }else{
                    if(onAPScanFinished!=null){
                        onAPScanFinished.failure(wifiManager.getScanResults());
                    }
                }
            }
        }
    }
    public void registerAsClientSide(int port,long delay) throws IOException {
        executorService.schedule(lanClientSide.registerLanClientSide(port),delay, TimeUnit.MILLISECONDS);
    }
    public void registerAsServerSide(int port,int bucketCarries,long delay) throws IOException {
        executorService.schedule(lanServerSide.registerLanServerSide(port,bucketCarries),delay, TimeUnit.MILLISECONDS);
    }
    public WifiManager getWifiManager() {
        return wifiManager;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==ENABLE_WIFI_REQUEST){
                wifiManager.setWifiEnabled(true);
        }
    }

    private class LanClientSide implements Callable<Socket>{
        private Socket dataSocket;
        private InetSocketAddress socketAddress;
        private int port;
        public Callable<Socket> registerLanClientSide(int port) throws IOException {
//            /*define the Socket address of the current AP*/
//            InetAddress inetAddress=InetAddress.getLocalHost();
//            socketAddress=new InetSocketAddress(inetAddress, port);
            this.port=port;
            return this;
        }
        @Override
        public Socket call() {
            unregisterReceiver(wifiScanBroadCast);
            try {
                /*bind to the connected wifi(AP) address*/
//                dataSocket.bind(socketAddress);
                Toast.makeText(getApplicationContext(),getLocalIpAddress(),Toast.LENGTH_LONG).show();
                dataSocket =new Socket(getLocalIpAddress(),port);
                //connection-success
                    if ( onConnectionEstablished != null ){
                        onConnectionEstablished.connectionSuccess(dataSocket);
                    }

            } catch (Exception e) {
                e.printStackTrace();
                //connection-failure
                if(onConnectionEstablished!=null){
                    onConnectionEstablished.connectionFailed(dataSocket);
                }
            }
            return dataSocket;
        }
        private String getLocalIpAddress() throws UnknownHostException {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
        }
        public void cancelOperation(){
            try {
                dataSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public InetSocketAddress getSocketAddress() {
            return socketAddress;
        }
        public Socket getDataSocket() {
            return dataSocket;
        }
    }

    private class LanServerSide implements Callable<ServerSocket>{
        private ServerSocket serverSocket;
        private Socket dataSocket;
        private boolean cancelOperation=false;
        public Callable<ServerSocket> registerLanServerSide(int port,int bucketCarriers) throws IOException {
            serverSocket=new ServerSocket(port,bucketCarriers);
            return this;
        }
        @Override
        public ServerSocket call() {
            connectWhenRequested();
            return null;
        }
        private void connectWhenRequested(){
            do {
                try {
                    dataSocket = serverSocket.accept();
                    //connection-success
                    if ( onConnectionEstablished != null ){
                        onConnectionEstablished.connectionSuccess(dataSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //connection-failed
                    if ( onConnectionEstablished != null ){
                        onConnectionEstablished.connectionFailed(dataSocket);
                    }
                }
            } while (!isCancelOperation());
        }
        public void cancelOperation(){
            try {
                cancelOperation=true;
                dataSocket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public boolean isCancelOperation() {
            return cancelOperation;
        }
    }

    public LanClientSide getLanClientSide() {
        return lanClientSide;
    }

    public void setOnConnectionEstablished(OnConnectionEstablished onConnectionEstablished) {
        this.onConnectionEstablished = onConnectionEstablished;
    }

    public void setOnAPScanFinished(OnAPScanFinished onAPScanFinished) {
        this.onAPScanFinished = onAPScanFinished;
    }
}
