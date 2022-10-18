package com.ihi.hts.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "token_info")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenInfo {
	
	@Id
	@GenericGenerator( name = "uuid-gen", strategy = "uuid2" ) 
	@GeneratedValue(generator = "uuid-gen")
	private String id;
	
	@ManyToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;
	
	@Column(name = "created_time")
	@Builder.Default
	private DateTime createdTime= new DateTime();

	@Column(name = "token_id")
	private String tokenId;
}
