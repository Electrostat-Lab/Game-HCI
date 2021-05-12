package com.scrappers.superiorExtendedEngine.LANHelper;

import java.net.Socket;

public interface OnConnectionEstablished {
    void connectionSuccess(Socket dataSocket);
    void connectionFailed(Socket dataSocket);
}
