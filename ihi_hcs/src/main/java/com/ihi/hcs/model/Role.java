package com.ihi.hcs.model;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
	@Id
	@GenericGenerator(name = "uuid-gen", strategy = "uuid2")
	@GeneratedValue(generator = "uuid-gen")
	private String id;

	@Enumerated(EnumType.STRING)
	private ERole name;

	public Role(ERole name) {
		this.name = name;
	}

}