/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility.validator;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class LimitValidator {

	@Value("${limit.maxlimit}")
	private BigDecimal maxLimit=new BigDecimal(1000);
	
	@Autowired
	PlayerPersistance persistance;
	
	public boolean validateLimit(Player player) throws DomainException {


		Player persistedBalance = persistance.findPlayer(player);

		synchronized (persistedBalance) {
			if (player.getBalance().compareTo(maxLimit) > 0) {
				throw new DomainException(DomainErrorCode.BALANCE_CAANOT_BE_MORE_THAN_CONFIGURED_LIMIT);
			}
			return true;
		}

	
	}
	
}
