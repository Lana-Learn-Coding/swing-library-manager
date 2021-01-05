package io.lana.library.ui.view.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class AppPanel extends JPanel implements ApplicationContextAware {
    protected JFrame frame;

    protected ApplicationContext applicationContext;

    @Autowired
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    @Autowired
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void gotoPanel(JPanel panel) {
        frame.setContentPane(panel);
        frame.setSize(panel.getSize());
        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    public void gotoPanel(Class<? extends JPanel> panelClass) {
        JPanel panel = applicationContext.getBean(panelClass);
        gotoPanel(panel);
    }
}
