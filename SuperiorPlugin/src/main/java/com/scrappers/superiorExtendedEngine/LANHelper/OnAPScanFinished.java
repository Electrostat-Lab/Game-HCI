package com.scrappers.superiorExtendedEngine.LANHelper;

import android.net.wifi.ScanResult;

import java.util.List;

public interface OnAPScanFinished {
    void success(List<ScanResult> newScanResults);
    void failure(List<ScanResult> oldScanResults);
}
