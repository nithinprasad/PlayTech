/**
 * 
 */
package com.prasad.nithin.playtech.server.persistance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.prasad.nithin.playtech.server.entity.Player;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nithinprasad
 *
 */
@Component
@Slf4j
public class PlayerPersistance {

	private Set<Player> players = new HashSet<Player>();
	

	@Autowired
	PlayerRepository playerRepository;
	
	
	private static class SingletonHolder {
		public static final PlayerPersistance instance = new PlayerPersistance();
	}

	public static PlayerPersistance getInstance() {
		return SingletonHolder.instance;
	}
	
	private PlayerPersistance() {
		
	}

	public Player findPlayer(Player player) {
		return players.stream().filter(eachPlayer -> player.getUserName().equals(eachPlayer.getUserName())).findFirst()
				.orElseGet(createOrfetchFromDB(player));
	}

	private Supplier<? extends Player> createOrfetchFromDB(Player player) {
		return () -> playerRepository.findById(player.getUserName()).orElse(createUser(player));
	}

	private Player createUser(Player player) {
		player.setBalanceVersion("0");
		player.setBalance(new BigDecimal(0));
		Player updatedPlayer = playerRepository.save(player);
		players.add(updatedPlayer);
		return updatedPlayer;
	}

	public void save(Player currentDetails, Player player) {
		players.remove(currentDetails);
		player.setBalanceVersion(new BigDecimal(currentDetails.getBalanceVersion()).add(new BigDecimal(1)).toPlainString());
		players.add(player);
	}
	
	@Scheduled(fixedRateString = "${scheduler.interval}")
	public void backup() {
		log.info("Data backedup at {}",new Date());
		if(!players.isEmpty())
			playerRepository.saveAll(players);
	}

	
}
