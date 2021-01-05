package io.lana.library.ui;

import java.awt.*;

public interface MainFrameContainer {
    default <T extends Container & MainFrameContainer> void onPaneMounted(T previousPane) {
    }

    default <T extends Container & MainFrameContainer> void onPaneUnMounted(T nextPane) {
    }
}
