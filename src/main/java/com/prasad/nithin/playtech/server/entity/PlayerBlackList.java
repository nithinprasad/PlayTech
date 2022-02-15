/**
 * 
 */
package com.prasad.nithin.playtech.server.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author nithinprasad
 *
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerBlackList {

	@Id
	String userName;
	boolean isBlocked;
	
	
}
