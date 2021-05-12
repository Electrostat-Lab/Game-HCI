
package com.scrappers.superiorExtendedEngine.jmeSurfaceView;

import com.jme3.app.LegacyApplication;
import com.jme3.system.AppSettings;

/**
 * Embedded interface class(abstract) to listen for the moment when when the GL thread holding the #{@link JmeSurfaceView}
 * joins the UI thread , after asynchronous rendering.
 * @see JmeSurfaceView#setOnRendererCompleted(OnRendererCompleted).
 */
public interface OnRendererCompleted {
    /**
     * Listens for the the moment when when the GL thread holding the #{@link JmeSurfaceView}
     * joins the UI thread , after asynchronous rendering.
     * @param application the current jme game instance.
     * @param appSettings the current window settings of the running jme game.
     */
    void onRenderCompletion(LegacyApplication application , AppSettings appSettings);
}
