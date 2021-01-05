package io.lana.library.ui;

import io.lana.library.utils.WorkerUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class MainFrame extends JFrame implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Autowired
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    @Qualifier("startupPane")
    @Override
    public void setContentPane(Container contentPane) {
        if (!MainFrameContainer.class.isAssignableFrom(contentPane.getClass())) {
            throw new RuntimeException("Container class: " + contentPane.getClass() + "is not of type MainFrameContainer");
        }
        super.setContentPane(contentPane);
        setSize(contentPane.getSize());
        pack();
        setLocationRelativeTo(null);
    }

    public <T extends Container & MainFrameContainer> void switchContentPane(T container) {
        setContentPane(container);
        WorkerUtils.runAsync(() -> ((Container & MainFrameContainer) getContentPane()).onPaneUnMounted(container));
        WorkerUtils.runAsync(() -> container.onPaneMounted((Container & MainFrameContainer) getContentPane()));
    }

    public <T extends Container & MainFrameContainer> void switchContentPane(Class<T> containerClass) {
        switchContentPane(applicationContext.getBean(containerClass));
    }

    public MainFrame() {
        setTitle("Library Manager App");
        setResizable(true);
    }
}
