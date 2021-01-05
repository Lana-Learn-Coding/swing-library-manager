package io.lana.library.ui;

import io.lana.library.core.model.user.User;
import io.lana.library.ui.view.app.LoginPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    private User user;

    private final MainFrame mainFrame;

    @Autowired
    public UserContext(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if (isLoggedIn()) {
            throw new RuntimeException("Cannot set user! User already logged in: " + user.getUsername());
        }
        this.user = user;
    }

    public void logout() {
        user = null;
        mainFrame.setContentPane(LoginPanel.class);
    }

    public boolean isLoggedIn() {
        return user != null;
    }
}
