package com.github.manosbatsis.services.email.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmailRecepient {

    private String email;

    private String fullName;

    private String address;

    public EmailRecepient(String email, String fullName, String address) {
        this.email = email;
        this.fullName = fullName;
        this.address = address;
    }
}
