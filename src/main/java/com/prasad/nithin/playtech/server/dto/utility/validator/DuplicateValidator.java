/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility.validator;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.prasad.nithin.playtech.server.dto.utility.exception.DomainErrorCode;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainException;
import com.prasad.nithin.playtech.server.persistance.TransactionPersistance;

/**
 * @author nithinprasad
 *
 */
@Component
public class DuplicateValidator {

	@Autowired
	TransactionPersistance persistance;

	public boolean checkIfDuplicateTransaction(String userName, String id, BigDecimal currentBalance,
			BigDecimal updatedBalance, String version) throws DomainException {

		var txn = persistance.checkIfTransactionExists(id);

		if (txn.isPresent()) {
			throw new DomainException(DomainErrorCode.DUPLICATE_TRANSACTION,txn.get());
		}
		
		return true;
	}
}
