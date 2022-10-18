package com.ihi.hcs.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Entity
@Table(name = "refresh_token")
@Data
public class RefreshToken {

	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private Instant expiryDate;

}
