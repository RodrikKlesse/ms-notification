package com.klesse.notification.domain;

import lombok.Data;

@Data
public class Users {
    private Long id;
    private String name;
    private String lastName;
    private String ssn;
    private String phoneNumber;
    private Double wage;
}
