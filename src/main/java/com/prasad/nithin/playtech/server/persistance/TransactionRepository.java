/**
 * 
 */
package com.prasad.nithin.playtech.server.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.nithin.playtech.server.entity.Transaction;

/**
 * @author nithinprasad
 *
 */
public interface TransactionRepository extends JpaRepository<Transaction,String> {
	
	
}
