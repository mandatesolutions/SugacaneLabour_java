package com.sugarcanelabour.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sugarcanelabour.helper.Enums.UserStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "common_login")
public class CommonLogin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Email
	@NotBlank(message = "Email cannot be blank")
	private String email;

	@Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
	private String mobileNo;

	@NotBlank(message = "Password cannot be blank")
	private String password;

//	    @OneToMany(mappedBy = "commonLogin", cascade = CascadeType.ALL, orphanRemoval = true)
//	    private List<Document> documents;

	// @JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime createdAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@JsonIgnore
	private UserStatus status= UserStatus.ACTIVE;
	

}
