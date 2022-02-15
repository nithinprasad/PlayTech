/**
 * 
 */
package com.prasad.nithin.playtech.server.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prasad.nithin.playtech.server.entity.Player;

/**
 * @author nithinprasad
 *
 */
public interface PlayerRepository extends JpaRepository<Player, String>{

}
