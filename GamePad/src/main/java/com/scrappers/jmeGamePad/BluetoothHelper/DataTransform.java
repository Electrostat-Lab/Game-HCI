package com.scrappers.jmeGamePad.BluetoothHelper;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataTransform implements Runnable{
    private final BluetoothSocket bluetoothSocket;
    public OnDataReceivedListener onDataReceivedListener;
    public OnDataReceivedFailureListener onDataReceivedFailureListener;
    private final Logger logger=Logger.getLogger("Superior Extended Engine BluetoothHelper Library");

    public DataTransform(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket=bluetoothSocket;
        Executors.newFixedThreadPool(1).execute(this);
        logger.log(Level.INFO,"DataTransform thread starts..............");
    }
    public void write(String data){
        try(OutputStream outputStream=bluetoothSocket.getOutputStream()){
            outputStream.write(data.getBytes());
            logger.log(Level.INFO,"Data sent to the bounded device..............");
            logger.log(Level.INFO,"OutputStream closed.");
        }catch (IOException e){
            e.printStackTrace();
            logger.log(Level.INFO,"Data Failed to be sent.");
            logger.log(Level.INFO,"OutputStream closed.");
        }
    }
    public void write(int data){
        try(OutputStream outputStream=bluetoothSocket.getOutputStream()){
            outputStream.write(data);
            logger.log(Level.INFO,"Data sent to the bounded device..............");
            logger.log(Level.INFO,"OutputStream closed.");
        }catch (IOException e){
            e.printStackTrace();
            logger.log(Level.INFO,"Data Failed to be sent.");
            logger.log(Level.INFO,"OutputStream closed.");
        }
    }

    @Override
    public void run() {
        while (true) {
            if(bluetoothSocket.isConnected()){
                logger.log(Level.INFO,"Device is connected & InputStream listener starts.");
                try  {
                    InputStream inputStream = bluetoothSocket.getInputStream();
                    if ( inputStream != null ){
                        /*get byte data with the same size of the bytes from the inputStream*/
                        byte[] readBytes = new byte[inputStream.available()];
                        /*loop over the data & submit them one by one(each byte) along our byte array*/
                        for (int pos = 0; pos < readBytes.length; pos++) {
                            /*check if the data is readable(not blank data*/
                            if ( inputStream.read() != -1 ){
                                /*assign bytes(fill our byte array storage*/
                                readBytes[pos] = (byte) inputStream.read();
                            }
                        }
                        logger.log(Level.INFO,"Data read from the bounded device.");
                        /*get the data in the form of readable string to return from the callable future task*/
                        if(onDataReceivedListener!=null){
                            onDataReceivedListener.onDataReceived(new String(readBytes));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.log(Level.INFO,"Data read from the bounded device Failed.");
                    if(onDataReceivedFailureListener!=null){
                        onDataReceivedFailureListener.onDataFailure();
                    }
                }
            }else{
                logger.log(Level.INFO,"Device isn't connected.");
                break;
            }

        }
    }
    public interface OnDataReceivedListener{
        void onDataReceived(String data);
    }
    public interface OnDataReceivedFailureListener{
        void onDataFailure();
    }
    public void setOnDataReceivedListener(OnDataReceivedListener onDataReceivedListener) {
        this.onDataReceivedListener = onDataReceivedListener;
    }

}