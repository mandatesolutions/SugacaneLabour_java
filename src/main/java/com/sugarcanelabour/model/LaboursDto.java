package com.sugarcanelabour.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LaboursDto {

	private String uuid;
    private String profileImage;
    private String firstName;
    private String lastName;
    private String email;
}
