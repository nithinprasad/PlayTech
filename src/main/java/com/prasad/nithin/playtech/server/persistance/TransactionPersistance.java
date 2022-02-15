/**
 * 
 */
package com.prasad.nithin.playtech.server.persistance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.prasad.nithin.playtech.server.entity.Transaction;

/**
 * @author nithinprasad
 *
 */
@Component
public class TransactionPersistance {

	@Autowired
	TransactionRepository repository;
	
	
	
	private static class SingletonHolder {
		public static final TransactionPersistance instance = new TransactionPersistance();
	}

	public static TransactionPersistance getInstance() {
		return SingletonHolder.instance;
	}
	
	private TransactionPersistance() {
		
	}

	@CacheEvict(cacheNames =  "transactionids",allEntries = true)
	public Transaction  addTransactionId(String userName,String id,BigDecimal currentBalance,BigDecimal updatedBalance,String version) {
		Transaction transaction=new Transaction();
		transaction.setTransactionId(id);
		transaction.setUserName(userName);
		transaction.setPreviousBalance(currentBalance);
		transaction.setUpdatedBalance(updatedBalance);
		transaction.setBalanceVersion(version);
		return repository.save(transaction);
	}
	
	public Optional<Transaction> checkIfTransactionExists(String id) {
		
		return fetchAllTransactions(1000)
			.stream()
			.filter(each->id.equals(each.getTransactionId()))
			.findFirst();
			//.orElseGet(()->this.addTransactionId(userName,id,currentBalance,updatedBalance,version));
		
	}
	
	@Cacheable(cacheNames = ("transactionids"))
	private List<Transaction> fetchAllTransactions(int limit){
		Pageable pageable = PageRequest.ofSize(limit);
		return repository.findAll(pageable).getContent();
	}

	
}
