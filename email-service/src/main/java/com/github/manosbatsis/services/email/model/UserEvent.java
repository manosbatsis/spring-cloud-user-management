package com.github.manosbatsis.services.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {

    private UserEventKey key;

    private String type;
    private String data;
}
