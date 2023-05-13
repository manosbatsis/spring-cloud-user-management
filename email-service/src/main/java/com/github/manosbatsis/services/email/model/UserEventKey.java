package com.github.manosbatsis.services.email.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEventKey {

  private Long userId;

  private Date datetime;
}
