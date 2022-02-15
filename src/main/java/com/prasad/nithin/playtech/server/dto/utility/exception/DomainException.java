/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility.exception;

import com.prasad.nithin.playtech.server.entity.Transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author nithinprasad
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DomainException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Transaction transaction;
	
	public DomainException(DomainErrorCode domainErrorCode){
		super(domainErrorCode.name());
	}
	
	public DomainException(DomainErrorCode domainErrorCode,Transaction transaction){
		super(domainErrorCode.name());
		this.transaction=transaction;
	}
	
}
