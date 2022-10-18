package com.ihi.hcs.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "username"),
		@UniqueConstraint(columnNames = "email") })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;

	@NotBlank
	private String username;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String password;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "mobile_num")
	private String mobileNum;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private UserStatus status=UserStatus.ACTIVE;
	
	@ManyToMany
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	@Builder.Default
	private Set<Role> roles = new HashSet<>();

	public User(String mobileNum, String email, String password) {
		this.username = String.valueOf(mobileNum);
		this.email = email;
		this.password = password;
		this.mobileNum = mobileNum;
	}

}
