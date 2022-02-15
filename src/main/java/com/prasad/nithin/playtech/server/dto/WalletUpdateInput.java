/**
 * 
 */
package com.prasad.nithin.playtech.server.dto;

import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
public class WalletUpdateInput {
	
	@NotNull
	@NotBlank
	String userName;
	@NotNull
	@NotBlank
	String transactionId;
	BigDecimal balance;

}
