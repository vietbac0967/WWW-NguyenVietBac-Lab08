package com.example.lab08.dto;

public record UserInfo(String userName, String password, boolean enable, String [] authorities){
}
