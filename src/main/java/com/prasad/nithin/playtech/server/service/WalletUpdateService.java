/**
 * 
 */
package com.prasad.nithin.playtech.server.service;

import java.math.BigDecimal;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prasad.nithin.playtech.server.dto.WalletUpdateInput;
import com.prasad.nithin.playtech.server.dto.WalletUpdateResponse;
import com.prasad.nithin.playtech.server.dto.utility.TransformUtility;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainException;
import com.prasad.nithin.playtech.server.dto.utility.validator.BalanceValidator;
import com.prasad.nithin.playtech.server.dto.utility.validator.DuplicateValidator;
import com.prasad.nithin.playtech.server.dto.utility.validator.LimitValidator;
import com.prasad.nithin.playtech.server.dto.utility.validator.PlayerStatusValidator;
import com.prasad.nithin.playtech.server.entity.Player;
import com.prasad.nithin.playtech.server.persistance.PlayerPersistance;
import com.prasad.nithin.playtech.server.persistance.TransactionPersistance;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nithinprasad
 *
 */
@Service
@Slf4j
public class WalletUpdateService {

	@Autowired
	LimitValidator limitValidator;
	@Autowired
	PlayerStatusValidator playerStatusValidator;
	@Autowired
	BalanceValidator balanceValidator;
	
	@Autowired
	DuplicateValidator duplicateValidator;

	@Autowired
	PlayerPersistance persistance;
	
	@Autowired
	TransactionPersistance transactionPersistance;

	public WalletUpdateResponse updateWallet(WalletUpdateInput input) {

		log.info("Incoming Request {}",input);
		Player player = TransformUtility.convertDtoToEntity(input);
		Player currentDetails = persistance.findPlayer(player);

		WalletUpdateResponse updateResponse = new WalletUpdateResponse();

		synchronized (currentDetails) {
			BigDecimal updatedBalance = currentDetails.getBalance().add(input.getBalance());
			player.setBalance(updatedBalance);
			try {
				balanceValidator.validateAccountBalance(player);
				limitValidator.validateLimit(player);
				playerStatusValidator.valiatePlayerStatus(player);
				duplicateValidator.checkIfDuplicateTransaction(player.getUserName(), input.getTransactionId(), currentDetails.getBalance(), updatedBalance, currentDetails.getBalanceVersion());
				persistance.save(currentDetails, player);
				transactionPersistance.addTransactionId(player.getUserName(), input.getTransactionId(), currentDetails.getBalance(), updatedBalance, player.getBalanceVersion());
				updateResponse.setBalanceAfterChange(updatedBalance);
				updateResponse.setBalanceVersion(player.getBalanceVersion());
				
			} catch (DomainException e) {
				log.error("Exception while updating current State{} due to {}",currentDetails,e);
				if(e.getTransaction()!=null) {
					updateResponse.setBalanceAfterChange(e.getTransaction().getUpdatedBalance());
					updateResponse.setBalanceChange(e.getTransaction().getPreviousBalance());
					updateResponse.setBalanceVersion(e.getTransaction().getBalanceVersion());
					log.info("OutGoing Response {}",updateResponse);
					BeanUtils.copyProperties(input, updateResponse);
					return updateResponse;
				}else {
					updateResponse.setErrorCode(e.getMessage());
					updateResponse.setBalanceAfterChange(currentDetails.getBalance());
					updateResponse.setBalanceVersion(currentDetails.getBalanceVersion());
				}
				
			}
			updateResponse.setBalanceChange(currentDetails.getBalance());
			
		}
		
		log.info("OutGoing Response {}",updateResponse);
		BeanUtils.copyProperties(input, updateResponse);
		return updateResponse;

	}

}
