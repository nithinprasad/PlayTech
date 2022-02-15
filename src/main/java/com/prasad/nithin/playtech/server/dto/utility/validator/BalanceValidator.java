/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility.validator;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.prasad.nithin.playtech.server.dto.utility.exception.DomainErrorCode;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainException;
import com.prasad.nithin.playtech.server.entity.Player;
import com.prasad.nithin.playtech.server.persistance.PlayerPersistance;

/**
 * @author nithinprasad
 *
 */

@Component
public class BalanceValidator {

	@Autowired
	PlayerPersistance persistance;

	public boolean validateAccountBalance(Player player) throws DomainException {

		Player persistedBalance = persistance.findPlayer(player);

		synchronized (persistedBalance) {
			if (player.getBalance().compareTo(BigDecimal.ZERO) < 0) {
				throw new DomainException(DomainErrorCode.BALANCE_CANNOT_BE_LESS_THAN_ZERO);
			}
			return true;
		}

	}

}
