package com.example.lab08.controller;

import com.example.lab08.dto.AuthInfos;
import com.example.lab08.dto.UserInfo;
import com.example.lab08.service.JdbcUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class JdbcUserController {
    @Autowired
    private JdbcUserService jdbcUserService;
    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> add(@RequestBody UserInfo userInfo) {
        UserDetails userDetails = User
                .withUsername(userInfo.userName())
                .password(encoder.encode(userInfo.password()))
                .roles(userInfo.authorities())
                .build();
        return ResponseEntity.ok(jdbcUserService.addUser(userDetails));
    }

    @PutMapping("/change-psw")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
//    @PreAuthorize("#username == authentication.principal.username")
    public ResponseEntity<?> changePsw(Principal principal, @RequestBody String newPass) {
        return ResponseEntity.ok(jdbcUserService.changePassword(
                principal.getName(),
                encoder.encode(newPass))
        );
    }

    @DeleteMapping("/{username}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> del(@PathVariable("username") String username) {
        log.info("******deleting user {}", username);
        UserDetails s = jdbcUserService.getByName(username);
        if (s == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credential " + username + " not found");
        return ResponseEntity.ok(jdbcUserService.deleteUser(username));
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<?> getByName(@PathVariable("username") String username) {
        UserDetails s = jdbcUserService.getByName(username);
        if (s == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credential " + username + " not found");
        return ResponseEntity.ok(s);
    }

    @GetMapping("/principal")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<AuthInfos> retrievePrincipal(Principal principal, Authentication auth) {
        return ResponseEntity.ok(new AuthInfos(principal, auth));
    }
}
