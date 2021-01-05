package io.lana.library.ui;

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
    @Qualifier("startupPanel")
    public void setContentPane(Container container) {
        super.setContentPane(container);
        setSize(container.getSize());
        pack();
        setLocationRelativeTo(null);
    }

    public void setContentPane(Class<? extends Container> containerClass) {
        setContentPane(applicationContext.getBean(containerClass));
    }

    public MainFrame() {
        setTitle("Library Manager App");
        setResizable(true);
    }
}
