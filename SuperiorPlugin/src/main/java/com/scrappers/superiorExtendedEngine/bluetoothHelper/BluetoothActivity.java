package com.scrappers.superiorExtendedEngine.bluetoothHelper;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BluetoothActivity extends AppCompatActivity{
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothDevice> discoveredBluetoothDevices=new ArrayList<>();
    private int discoveryTime=200;

    public static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int DISCOVERABLE_INTENT = 2;
    private static final int ACCESS_FINE_LOCATION = 4;
    private final String SEE_RFCOMM_CHANNEL="SuperiorExtendedEngine bluetoothHelper";
    /*UUID is Universally Unique Identifier , a 128-bit key for authentication in
    a bluetooth socket server between a server side system & a client side system*/
    private final String CHANNEL_UUID="2b97b0e6-f7ca-4295-81be-cd4e934b020d";

    public OnConnectionEstablished onConnectionEstablished;
    public OnDeviceFound onDeviceFound;
    public OnDiscoveryActivation onDiscoveryActivation;
    public OnDiscoveryTerminated onDiscoveryTerminated;

    private final Logger logger=Logger.getLogger("Superior Extended Engine bluetoothHelper Library");
    public final ScheduledExecutorService executorService= Executors.newScheduledThreadPool(2);
    private final BluetoothBroadCast bluetoothBroadCast=new BluetoothBroadCast();
    private final BluetoothServerSideSocket bluetoothServerSideSocket=new BluetoothServerSideSocket();
    private final BluetoothClientSideSocket bluetoothClientSideSocket=new BluetoothClientSideSocket();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initializeBluetooth();
    }

    /**
     * Initialize Default BluetoothAdapter that can access most of the devices
     * @apiNote internal use only.
     */
    private void initializeBluetooth(){
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            throw new IllegalStateException("Device doesn't support BluetoothProfiling !");
        }
        /*enable bluetooth intent*/
        if(!bluetoothAdapter.isEnabled()){
            this.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),REQUEST_ENABLE_BLUETOOTH);
        }
        /*request ACCESS_FINE_LOCATION permission for over L devices*/
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION);
        }else {
            setDiscoverable();
        }

        logger.log(Level.INFO,"SEE(Superior Extended Engine) Bluetooth Service Started ## By Scrappers");

    }

    /**
     * Start Bluetooth service Device Discovery
     * @apiNote Internal use only.
     */
    private void setDiscoverable(){
        Intent discoverableIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,getDiscoveryTime());
        this.startActivityForResult(discoverableIntent,DISCOVERABLE_INTENT);
        /*start the discovery*/
        bluetoothAdapter.startDiscovery();
        /*Listen for discovery events change*/
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(bluetoothBroadCast,intentFilter);

        logger.log(Level.INFO,"Device is discoverable !");

    }

    /**
     * set discovery time in seconds
     * @param discoveryTime discovery time in seconds
     */
    public void setDiscoveryTime(int discoveryTime) {
        this.discoveryTime = discoveryTime;
    }

    /**
     * get the discovery time
     * @return discovery time in seconds.
     * @apiNote default value is 200 seconds.
     */
    public int getDiscoveryTime() {
        return discoveryTime;
    }

    /**
     * get all discovered Devices
     * @return array list of discovered devices
     */
    public ArrayList<BluetoothDevice> getDiscoveredBluetoothDevices() {
        return discoveredBluetoothDevices;
    }

    /**
     * get paired devices
     * @return previously paired devices
     * @apiNote Warning : paired devices differ from discovered devices & connected ones
     */
    public Set<BluetoothDevice> getPairedDevices(){
        if(bluetoothAdapter==null){
            throw new IllegalArgumentException("Default BluetoothAdapter hasn't been initialized !");
        }
        return bluetoothAdapter.getBondedDevices();
    }

    /**
     * register the device as a server side bluetooth socket (receives any incoming request from other nearby devices).
     * @param delaySeconds delay of registration of the futureTask in seconds.
     */
    public void registerAsServerSide(int delaySeconds){
        executorService.schedule(bluetoothServerSideSocket,delaySeconds, TimeUnit.SECONDS);
        logger.log(Level.INFO,"Device is registered as a server side system for BluetoothServerSocket !");
    }

    /**
     * register the device as a client side bluetooth socket (starts/initiates the communication w/ the server side device through MAC address)
     * @param bluetoothDevice the device you want to initiate the communication with(server side device).
     * @param delaySeconds delay of registration of the futureTask in seconds.
     * @apiNote UUID (Universally Unique Identifier) must be the same throughout both the server & client side which is a 128-bit value.
     */
    public void registerAsClientSide(BluetoothDevice bluetoothDevice,int delaySeconds){
        executorService.schedule(bluetoothClientSideSocket.registerBluetoothServerDevice(bluetoothDevice),delaySeconds,TimeUnit.SECONDS);
        logger.log(Level.INFO,"Device is registered as a client side system for BluetoothSocket !");
    }

    /**
     * stop device discovery explicitly(directly).
     * @apiNote device discovery usually stops implicitly when the period specified is up & onDestruction() of the BluetoothActivity activity.
     */
    public void stopDiscovery(){
        if( bluetoothBroadCast.isOrderedBroadcast()){
            this.unregisterReceiver(bluetoothBroadCast);
            bluetoothAdapter.cancelDiscovery();
            logger.log(Level.INFO,"Device Discovery terminated by the user !");
        }
    }

    private class BluetoothBroadCast extends  BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case BluetoothDevice.ACTION_FOUND:
                    logger.log(Level.INFO,"Device Found through "+intent.getAction());
                    BluetoothDevice discoveredDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    discoveredBluetoothDevices.add(discoveredDevice);
                    if(onDeviceFound!=null){
                        onDeviceFound.deviceFound(discoveredDevice, discoveredBluetoothDevices);
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    logger.log(Level.INFO,"Device discovery service through "+intent.getAction());
                    if(onDiscoveryActivation!=null){
                        onDiscoveryActivation.onDiscoverable();
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    logger.log(Level.INFO,"Device discovery terminated through "+intent.getAction());
                    if(onDiscoveryTerminated!=null){
                        onDiscoveryTerminated.onDiscoveryTermination(discoveredBluetoothDevices);
                    }
                    break;
            }
        }
    }

    /**
     * ServerSide System Authentication
     */
    public class BluetoothServerSideSocket implements Callable<BluetoothServerSocket> {
        private BluetoothServerSocket bluetoothServerSocket;
        private BluetoothSocket bluetoothSocket;
        @Override
        public BluetoothServerSocket call() {
            try{
                bluetoothServerSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(SEE_RFCOMM_CHANNEL, UUID.fromString(CHANNEL_UUID));
                connectWhenRequested();
            }catch (IOException e){
                e.printStackTrace();
            }
            return bluetoothServerSocket;
        }
        public void connectWhenRequested(){
            /*keep trying to accept as long as there's no external exception*/
            while (true){
                try {
                    bluetoothSocket=bluetoothServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(onConnectionEstablished!=null){
                        /*connection failed listener*/
                        onConnectionEstablished.connectionFailed(bluetoothSocket,false, UUID.fromString(CHANNEL_UUID), SEE_RFCOMM_CHANNEL);
                    }
                }
                if(bluetoothSocket !=null){
                    if(onConnectionEstablished!=null){
                        onConnectionEstablished.connectionSuccess(bluetoothSocket,true, UUID.fromString(CHANNEL_UUID), SEE_RFCOMM_CHANNEL);
                    }
                    /*close the bluetoothServerSocket that was waiting for a connection unless you want multiple devices to connect you
                    * it makes sense to call close() on the BluetoothServerSocket immediately after accepting a connected socket.
                    * because it accepts only one connection unlike TCP/IPs */
                    cancelOperation();
                    break;
                }
            }
        }

        public void cancelOperation(){
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * ClientSide System Authentication
     */
    public class BluetoothClientSideSocket implements Callable<BluetoothSocket>{
        private BluetoothSocket clientSideBluetoothSocket;

        public Callable<BluetoothSocket> registerBluetoothServerDevice(BluetoothDevice bluetoothDevice){
            try {
                clientSideBluetoothSocket=bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(CHANNEL_UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }
        @Override
        public BluetoothSocket call() {
            if(bluetoothAdapter.isDiscovering()){
                BluetoothActivity.this.unregisterReceiver(bluetoothBroadCast);
                bluetoothAdapter.cancelDiscovery();
            }
            try {
                clientSideBluetoothSocket.connect();
                if(onConnectionEstablished!=null){
                    onConnectionEstablished.connectionSuccess(clientSideBluetoothSocket,true, UUID.fromString(CHANNEL_UUID), SEE_RFCOMM_CHANNEL);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(onConnectionEstablished!=null){
                    onConnectionEstablished.connectionFailed(clientSideBluetoothSocket,false, UUID.fromString(CHANNEL_UUID), SEE_RFCOMM_CHANNEL);
                }
            }

            return clientSideBluetoothSocket;
        }
        /*this operation closes the whole connection*/
        public void cancelOperation(){
            try {
                clientSideBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void setDiscoveredBluetoothDevices(ArrayList<BluetoothDevice> discoveredBluetoothDevices) {
        this.discoveredBluetoothDevices = discoveredBluetoothDevices;
    }

    public BluetoothServerSideSocket getBluetoothServerSideSocket() {
        return bluetoothServerSideSocket;
    }

    public BluetoothClientSideSocket getBluetoothClientSideSocket() {
        return bluetoothClientSideSocket;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == ACCESS_FINE_LOCATION ){
            setDiscoverable();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case BluetoothActivity.REQUEST_ENABLE_BLUETOOTH:
                    Toast.makeText(getApplicationContext(),"Bluetooth Enabled",Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothActivity.DISCOVERABLE_INTENT:
                    Toast.makeText(getApplicationContext(),"Started Discovering Devices",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDiscovery();
    }
    public interface OnConnectionEstablished{
        void connectionSuccess(BluetoothSocket bluetoothSocket,boolean connectivity,UUID uuid,String SPDname);
        void connectionFailed(BluetoothSocket bluetoothSocket,boolean connectivity,UUID uuid,String SPDname);
    }

    public void setOnConnectionEstablished(OnConnectionEstablished onConnectionEstablished) {
        this.onConnectionEstablished = onConnectionEstablished;
    }
    public interface OnDeviceFound {
        void deviceFound(BluetoothDevice discoveredDevice, ArrayList<BluetoothDevice> discoveredDevices);
    }

    public void setOnDeviceFound(OnDeviceFound onDeviceFound) {
        this.onDeviceFound = onDeviceFound;
    }

    public interface OnDiscoveryActivation{
        void onDiscoverable();
    }

    public void setOnDiscoveryActivation(OnDiscoveryActivation onDiscoveryActivation) {
        this.onDiscoveryActivation = onDiscoveryActivation;
    }
    public interface OnDiscoveryTerminated{
        void onDiscoveryTermination(ArrayList<BluetoothDevice> discoveredDevices);
    }

    public void setOnDiscoveryTerminated(OnDiscoveryTerminated onDiscoveryTerminated) {
        this.onDiscoveryTerminated = onDiscoveryTerminated;
    }
}
