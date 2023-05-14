package com.github.manosbatsis.services.email.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventKey {

    private Long userId;

    private Date datetime;
}
