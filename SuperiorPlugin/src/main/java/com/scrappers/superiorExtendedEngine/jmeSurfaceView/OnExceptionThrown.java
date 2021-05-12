
package com.scrappers.superiorExtendedEngine.jmeSurfaceView;

/**
 * Embedded interface designed to listen to exceptions & fire when an exception is thrown.
 * @see JmeSurfaceView#setOnExceptionThrown(OnExceptionThrown).
 */
public interface OnExceptionThrown {
    /**
     * Listens for a thrown exception or a thrown error.
     * @param e the exception or the error that is throwable.
     */
    void onExceptionThrown(Throwable e);
}
