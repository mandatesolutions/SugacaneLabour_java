package com.sugarcanelabour.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class SupervisorDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long age;
	private Long familyMembers;
	private String firstName;
	private String lastName;
	private String gender;
	private String bloodGroup;
	private String address;
	private String medicalHistory;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime createdAt;

	@JsonIgnore
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@JsonIgnore
	@Column(name = "profile_image_url")
	private String profileImage;

	@ManyToOne
	@JoinColumn(name = "registered_by_id")
	private CommonLogin registeredBy; // Stores who registered this supervisor

	@OneToOne
	@JoinColumn(name = "common_login_id")
	private CommonLogin commonLogin; // Link to CommonLogin for supervisor login details

	@Column(name = "unique_labor_id") // Ensure this matches the column name in your database
	private String uniqueLaborId; // or appropriate data type

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "talukaId")
	private Taluka taluka;

	@PrePersist
	@PreUpdate
	private void formatFields() {
		if (gender != null) {
			gender = gender.toUpperCase();
		}
		if (bloodGroup != null) {
			bloodGroup = bloodGroup.toUpperCase();
		}
	}
}
