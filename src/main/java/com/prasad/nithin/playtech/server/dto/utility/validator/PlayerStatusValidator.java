/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.prasad.nithin.playtech.server.dto.utility.exception.DomainErrorCode;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainException;
import com.prasad.nithin.playtech.server.entity.Player;
import com.prasad.nithin.playtech.server.entity.PlayerBlackList;
import com.prasad.nithin.playtech.server.persistance.PlayerBlackListRepository;

/**
 * @author nithinprasad
 *
 */

@Component
public class PlayerStatusValidator {

	@Autowired
	PlayerBlackListRepository blackListRepository;
	
	public boolean valiatePlayerStatus(Player player) throws DomainException {
		
		if(blackListRepository.findById(player.getUserName()).map(PlayerBlackList::isBlocked).orElse(false)) {
			throw new DomainException(DomainErrorCode.USER_IS_CURRENTLY_BLOCKED);
		}
		return true;
		
	}

}
