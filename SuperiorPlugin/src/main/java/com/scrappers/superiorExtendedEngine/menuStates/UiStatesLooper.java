package com.scrappers.superiorExtendedEngine.menuStates;

import android.view.View;

public class UiStatesLooper {
    /**
     * A modifiable annotated looper interface.
     */
    public @interface NonModifiable {
        interface Looper {
            /**
             * Apply the user code for this currentView.
             * @param currentView the current loopedOver view UI-State from the UI-State stack.
             * @param position the current Ui-State position.
             */
            void applyUpdate(View currentView,int position);
        }
    }

    /**
     * A non-modifiable annotated looper interface.
     */
    public @interface Modifiable {
        interface Looper {
            /**
             * Apply the user code for this currentView.
             * @param currentView the current loopedOver view UI-State from the UI-State stack.
             * @param position the current Ui-State position.
             */
            void applyUpdate(View currentView,int position);
        }
    }
}