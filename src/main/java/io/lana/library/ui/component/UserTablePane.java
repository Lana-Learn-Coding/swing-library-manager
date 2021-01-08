package io.lana.library.ui.component;

import io.lana.library.core.model.user.User;
import io.lana.library.ui.component.app.table.AbstractListBasedTablePane;
import io.lana.library.ui.component.app.table.TableColumnMapping;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

public class UserTablePane extends AbstractListBasedTablePane<User> {
    @Override
    protected TableColumnMapping<User> getTableColumnMapping() {
        TableColumnMapping<User> mapping = new TableColumnMapping<>();
        mapping.setDefaultColumnType(String.class);
        mapping.put("ID", User::getId, Integer.class);
        mapping.put("Name", User::getName);
        mapping.put("Username", User::getUsername);
        mapping.put("Phone", User::getPhoneNumber);
        mapping.put("Email", User::getEmail);
        mapping.put("Permissions", (user) -> StringUtils.join(user.getPermissions(), ", "));
        mapping.put("Permissions Count", (user) -> user.getPermissions().size());
        mapping.put("Created At", User::getCreatedAt, LocalDateTime.class);
        return mapping;
    }
}
