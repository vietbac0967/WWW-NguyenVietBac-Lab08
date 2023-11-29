package com.example.lab08.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class JdbcUserService {
    private DataSource dataSource;
    private JdbcUserDetailsManager manager;

    @Autowired
    public JdbcUserService(DataSource dataSource) {
        this.dataSource = dataSource;
        manager = new JdbcUserDetailsManager(dataSource);
    }

    public UserDetails addUser(UserDetails userDetails) {
        manager.createUser(userDetails);
        return userDetails;
    }

    public UserDetails changePassword(String username, String newPass) {
        UserDetails us = manager.loadUserByUsername(username);
        manager.changePassword("noop", newPass);
        return (us);
    }

    public UserDetails deleteUser(String username) {
        UserDetails us = manager.loadUserByUsername(username);
        manager.deleteUser(username);
        return us;
    }

    public UserDetails getByName(String username) {
        return manager.loadUserByUsername(username);
    }
}
