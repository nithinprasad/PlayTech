/**
 * 
 */
package com.prasad.nithin.playtech.server.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nithinprasad
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletUpdateResponse {

	private String transactionId;
	private String balanceVersion;
	private String errorCode;
	private BigDecimal balanceChange;
	private BigDecimal balanceAfterChange;
	
}
