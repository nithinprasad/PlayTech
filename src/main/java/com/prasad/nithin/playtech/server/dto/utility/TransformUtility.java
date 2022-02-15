/**
 * 
 */
package com.prasad.nithin.playtech.server.dto.utility;

import org.springframework.beans.BeanUtils;

import com.prasad.nithin.playtech.server.dto.WalletUpdateInput;
import com.prasad.nithin.playtech.server.entity.Player;

/**
 * @author nithinprasad
 *
 */
public class TransformUtility {

	
	public static Player convertDtoToEntity(WalletUpdateInput input) {
		Player player=new Player();
		BeanUtils.copyProperties(input, player);
		return player;
	}
	
}
