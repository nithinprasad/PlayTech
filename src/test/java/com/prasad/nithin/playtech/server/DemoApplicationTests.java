package com.prasad.nithin.playtech.server;

import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.util.UriComponentsBuilder;

import com.prasad.nithin.playtech.server.dto.WalletUpdateInput;
import com.prasad.nithin.playtech.server.dto.WalletUpdateResponse;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainErrorCode;
import com.prasad.nithin.playtech.server.dto.utility.exception.DomainException;
import com.prasad.nithin.playtech.server.dto.utility.validator.PlayerStatusValidator;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

	private final TestRestTemplate restTemplate = new TestRestTemplate();
	@LocalServerPort
	int randomServerPort;

	private String url;

	@Value("${limit.maxlimit}")
	private BigDecimal maxLimit = new BigDecimal(1000);
	private WebSocketStompClient webSocketStompClient;

	private CompletableFuture<WalletUpdateResponse> completableFuture;

	@BeforeEach
	public void setUp() {
		completableFuture = new CompletableFuture<>();
		url = "ws://localhost:" + randomServerPort + "/websocket";
	}

	@MockBean
	PlayerStatusValidator statusValidator;

	WalletUpdateInput input = new WalletUpdateInput("nithin", "123", new BigDecimal(112));

	private List<Transport> createTransportClient() {
		List<Transport> transports = new ArrayList<>(1);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		return transports;
	}

	@Test
	@Order(1)
	void contextLoads() {
	}

	@Test
	@Order(2)
	void checkCreatePlayer() throws InterruptedException, ExecutionException, TimeoutException {

		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
		}).get();
		
		stompSession.subscribe("/topic/public", new WallerTpdateResponseHandler());
		
		stompSession.send("/app/update", input);
		
		WalletUpdateResponse response = completableFuture.get();
		Assertions.assertNotNull(response);
		Assertions.assertNotNull(response.getTransactionId());
		Assertions.assertEquals(response.getBalanceAfterChange(), input.getBalance());
		Assertions.assertEquals(response.getBalanceVersion(), "1");

	}

	@Test
	@Order(3)
	void checkZeroUpdate() throws InterruptedException, ExecutionException, TimeoutException {

		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
		}).get();
		
		stompSession.subscribe("/topic/public", new WallerTpdateResponseHandler());
		input.setTransactionId("random");
		input.setBalance(new BigDecimal(-2000));
		stompSession.send("/app/update", input);
		WalletUpdateResponse response = completableFuture.get();
		Assertions.assertEquals(DomainErrorCode.BALANCE_CANNOT_BE_LESS_THAN_ZERO.name(), response.getErrorCode());
	}

	@Test
	@Order(4)
	void updateBalanceAboveLimit() throws InterruptedException, ExecutionException, TimeoutException {

		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
		}).get();
		
		stompSession.subscribe("/topic/public", new WallerTpdateResponseHandler());
		input.setBalance(new BigDecimal(maxLimit.intValue()+10));
		stompSession.send("/app/update", input);
		WalletUpdateResponse response = completableFuture.get();
		
		Assertions.assertEquals(DomainErrorCode.BALANCE_CAANOT_BE_MORE_THAN_CONFIGURED_LIMIT.name(),
				response.getErrorCode());
	}

	@Test
	@Order(5)
	void checkBlockedUser() throws DomainException, InterruptedException, ExecutionException, TimeoutException {

		when(statusValidator.valiatePlayerStatus(Mockito.any()))
				.thenThrow(new DomainException(DomainErrorCode.USER_IS_CURRENTLY_BLOCKED));
		WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		StompSession stompSession = stompClient.connect(url, new StompSessionHandlerAdapter() {
		}).get();
		
		stompSession.subscribe("/topic/public", new WallerTpdateResponseHandler());
		stompSession.send("/app/update", input);
		WalletUpdateResponse response = completableFuture.get();

		Assertions.assertEquals(DomainErrorCode.USER_IS_CURRENTLY_BLOCKED.name(), response.getErrorCode());
	}

	private <T> ResponseEntity<T> invokeEntity(String path, HttpMethod method, Object entity, Class<T> reposeClass) {
		return invokeEntity(path, method, entity, reposeClass, Collections.emptyMap());
	}

	private <T> ResponseEntity<T> invokeEntity(String path, HttpMethod method, Object entity, Class<T> reposeClass,
			Map<String, Object> args) {
		return invokeEntity(path, method, entity, reposeClass, args, Collections.emptyMap());
	}

	private <T> ResponseEntity<T> invokeEntity(String path, HttpMethod method, Object entity, Class<T> reposeClass,
			Map<String, Object> pathTemplate, Map<String, String> queryParam) {
		HttpHeaders headers = new HttpHeaders();
		String fullURI = url + path;
		LinkedMultiValueMap<String, String> multiValuesMap = new LinkedMultiValueMap<String, String>();
		queryParam.forEach(multiValuesMap::add);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(fullURI).queryParams(multiValuesMap)
				.uriVariables(pathTemplate);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<?> httpEntity = new HttpEntity<Object>(entity, headers);
		log.info("Rest api request {}", httpEntity);
		ResponseEntity<T> responseEntity = restTemplate.exchange(builder.toUriString(), method, httpEntity, reposeClass,
				pathTemplate);
		log.info("Rest api response {}", responseEntity.toString());
		return responseEntity;
	}
	
	
	private class WallerTpdateResponseHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return WalletUpdateResponse.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((WalletUpdateResponse) o);
        }
    }

}
