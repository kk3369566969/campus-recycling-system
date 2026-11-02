package com.campus.cycle.service;

import com.campus.cycle.domain.User;
import com.campus.cycle.events.AppEvent;
import com.campus.cycle.events.EventPublisher;
import com.campus.cycle.exception.AuthenticationException;
import com.campus.cycle.exception.ValidationException;
import com.campus.cycle.repository.DaoFactory;
import com.campus.cycle.repository.UserRepository;
import com.campus.cycle.util.PasswordUtil;

public final class AuthService {
    private final UserRepository users = DaoFactory.users();
    private final EventPublisher events;
    public AuthService(EventPublisher events) { this.events = events; }

    public User login(String username, String password) {
        validateCredentials(username, password);
        User user = users.findByCredentials(username.trim(), PasswordUtil.sha256(password));
        if (user == null) throw new AuthenticationException("账号或密码错误");
        events.publish(new AppEvent("LOGIN", username));
        return user;
    }

    public User register(String username, String password, String displayName) {
        validateCredentials(username, password);
        if (displayName == null || displayName.isBlank()) throw new ValidationException("昵称不能为空");
        User user = users.create(username.trim(), PasswordUtil.sha256(password), displayName.trim());
        events.publish(new AppEvent("REGISTER", username));
        return user;
    }

    private void validateCredentials(String username, String password) {
        if (username == null || !username.matches("[A-Za-z0-9_]{3,24}")) throw new ValidationException("账号需为 3-24 位字母、数字或下划线");
        if (password == null || password.length() < 6) throw new ValidationException("密码至少 6 位");
    }
}
