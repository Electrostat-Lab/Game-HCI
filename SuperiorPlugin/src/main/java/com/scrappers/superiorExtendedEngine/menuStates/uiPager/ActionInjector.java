package com.scrappers.superiorExtendedEngine.menuStates.uiPager;

import android.view.View;

public interface ActionInjector {
    void execute(View uiState, int position, String currentItem);
}
