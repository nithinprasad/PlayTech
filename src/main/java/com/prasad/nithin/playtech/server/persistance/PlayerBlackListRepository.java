/**
 * 
 */
package com.prasad.nithin.playtech.server.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.nithin.playtech.server.entity.PlayerBlackList;

/**
 * @author nithinprasad
 *
 */
public interface PlayerBlackListRepository extends JpaRepository<PlayerBlackList, String>{

}
