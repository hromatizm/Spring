package com.edu.ulab.app.web.request;

import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String title;
    private String country;
    private int age;
}
