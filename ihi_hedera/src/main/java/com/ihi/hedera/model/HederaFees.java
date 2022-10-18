package com.ihi.hedera.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hedera_fees")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HederaFees {
	
	@Id
	@GenericGenerator( name = "uuid-gen", strategy = "uuid2" ) 
	@GeneratedValue(generator = "uuid-gen")
	private String id;
	
	private String service;
	
	private String operations;
	
	private double priceInUsd;
	
	private DateTime updateDate;
	
}
