/**
 * 
 */
package com.prasad.nithin.playtech.server.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nithinprasad
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

	@Id
	@Column(name="USERNAME", nullable=false)
	private String userName;
	
	@Column(name="BALANCE_VERSION")
	private String balanceVersion;
	
	@Column(name="BALANCE")
	@DecimalMin(value = "0")
	private BigDecimal balance;
	
	
	
}
