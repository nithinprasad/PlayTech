/**
 * 
 */
package com.prasad.nithin.playtech.server.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

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
public class Transaction {

	@Id @GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	private String userName;
	private String transactionId;
	private String balanceVersion;
	private BigDecimal previousBalance;
	private BigDecimal updatedBalance;

	
}
