package com.prasad.nithin.playtech.server.controller;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.prasad.nithin.playtech.server.dto.WalletUpdateInput;
import com.prasad.nithin.playtech.server.dto.WalletUpdateResponse;
import com.prasad.nithin.playtech.server.service.WalletUpdateService;

@Controller
public class WalletController {

	@Autowired
	WalletUpdateService service;
	
    @MessageMapping("/update")
    @SendTo("/topic/public")
    public WalletUpdateResponse register(@Payload WalletUpdateInput input, SimpMessageHeaderAccessor headerAccessor) {
    	MDC.put("username",input.getUserName());
    	WalletUpdateResponse updateResponse= service.updateWallet(input);
		MDC.clear();
		return  updateResponse;
    }

}