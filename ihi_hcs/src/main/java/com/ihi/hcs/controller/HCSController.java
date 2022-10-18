package com.ihi.hcs.controller;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*import com.hedera.hashgraph.sdk.HederaStatusException;
import com.hedera.hashgraph.sdk.consensus.ConsensusTopicInfo;*/
import com.ihi.hcs.payload.request.HcsMessageSubmit;
import com.ihi.hcs.payload.request.PublishHcsMessage;
import com.ihi.hcs.payload.request.testRequest;
import com.ihi.hcs.service.HCSService;
import com.ihi.hcs.service.HcsIntegrations;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin
@RestController
@Api("Handles management of Hedera Consensus Services")
@RequestMapping(path = "/api/hcs")
public class HCSController {

	@Autowired
	HCSService hcsService;

	@Autowired
	HcsIntegrations hcsIntegrations;

	public HCSController() {
		hcsService = new HCSService();
	}

	/*
	 * @PostMapping("/topic/create")
	 * 
	 * @ApiOperation("Create a HCS Topic")
	 * 
	 * @ApiResponses(value = {@ApiResponse(code = 200, message = "Topic ID")})
	 * public String createTopic() throws HederaStatusException { return
	 * hcsService.createTopic(); }
	 * 
	 * @DeleteMapping("/topic/delete/{topicId}")
	 * 
	 * @ApiOperation("Delete a topic")
	 * 
	 * @ApiImplicitParam(name = "topicId", required = true, type = "String", example
	 * = "0.0.2117")
	 * 
	 * @ApiResponses(value = {@ApiResponse(code = 200, message =
	 * "success or failure")}) public boolean deleteTopic(@PathVariable String
	 * topicId) throws HederaStatusException { return
	 * hcsService.deleteTopic(topicId); }
	 * 
	 * @GetMapping("/topic/info/{topicId}")
	 * 
	 * @ApiOperation("Get info on a topic")
	 * 
	 * @ApiImplicitParam(name = "topicId", required = true, type = "String", example
	 * = "0.0.2117") public ConsensusTopicInfo getTopicInfo(@PathVariable String
	 * topicId) throws HederaStatusException { return
	 * hcsService.getTopicInfo(topicId); }
	 * 
	 * @GetMapping("/topic/subscribe/message/{topicId}")
	 * 
	 * @ApiOperation("Subscribe Mirror Node to a topicId. Updates to topics will be printed to the console."
	 * )
	 * 
	 * @ApiImplicitParam(name = "topicId", required = true, type = "String", example
	 * = "0.0.2117")
	 * 
	 * @ApiResponses(value = {@ApiResponse(code = 200, message =
	 * "success or failure")}) public String submitMessage(@PathVariable String
	 * topicId) { boolean res = hcsService.subscribeToTopic(topicId); return (res ?
	 * "New messages in this topics will be printed to the console." :
	 * "Subscription failed"); }
	 */
	@PostMapping("/topic/submit/message/{topicId}")
	@ApiOperation("submit a message to a topic")
	@ApiImplicitParams({ @ApiImplicitParam(name = "topicId", required = true, type = "String", example = "0.0.2117"), })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success or failure") })
	public ResponseEntity<?> submitMessage(@PathVariable String topicId, @RequestBody HcsMessageSubmit hcsMessageSubmit)
			throws JMSException {
		return hcsService.submitMessage(topicId, hcsMessageSubmit);
	}

	@GetMapping("/fetch/ledger/message/{clientId}/{page}/{size}")
	public ResponseEntity<?> getLedgerMessage(@PathVariable(value = "clientId") String clientId,
			@PathVariable(value = "page") int page, @PathVariable(value = "size") int size) {
		return hcsService.getLedgerMessage(clientId, PageRequest.of(page, size));
	}

	@PostMapping("/v2/topic/submit/message/{topicId}")
	@ApiOperation("submit a message to a topic")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "topicId", required = true, type = "String", example = "0.0.29693910"), })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "success or failure") })
	public ResponseEntity<?> publishMessage(@PathVariable(name = "topicId", required = true) String topicId,
			@RequestBody PublishHcsMessage publishHcsMessage) throws JMSException {
		return hcsService.publishMessage(topicId, publishHcsMessage);
	}

	@GetMapping("/v2/fetch/ledger/message/{clientId}/{page}/{size}")
	public ResponseEntity<?> getHcsMessage(@PathVariable(value = "clientId") String clientId,
			@PathVariable(value = "page") int page, @PathVariable(value = "size") int size) {
		return hcsService.getHcsMessage(clientId, PageRequest.of(page, size));
	}

	@PostMapping("/test/hedera/subscribe")
	public ResponseEntity<?> testSubscribe(@RequestBody testRequest hederaSubscribeInfo) {
		return hcsIntegrations.testSubscribeHedera(hederaSubscribeInfo);
	}

}
