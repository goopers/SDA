package com.pineone.icbms.sda.itf.cm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.pineone.icbms.sda.comm.dto.ResponseMessage;
import com.pineone.icbms.sda.comm.util.Utils;
import com.pineone.icbms.sda.itf.cm.dto.CmCiDTO;
import com.pineone.icbms.sda.itf.cm.dto.CmDTO;
import com.pineone.icbms.sda.itf.cm.service.CmService;

@RestController
@RequestMapping(value = "/itf")
public class CmController {
	private final Log log = LogFactory.getLog(this.getClass());

	@Resource(name = "cmService")
	private CmService cmService;
	
	
	// http://localhost:8080/sda/itf/cm/ALL
	@RequestMapping(value = "/cm/ALL", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> selectList(Map<String, Object> commandMap) {
		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		List<CmDTO> list = new ArrayList<CmDTO>();
		log.info("/cm/ALL GET start================>");
		try {
			list = cmService.selectList(commandMap);

			contents = gson.toJson(list);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContents(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/cm/ALL GET end================>");
		return entity;
	}
	
	// http://localhost:8080/sda/itf/cm/CM-1-1-011
	@RequestMapping(value = "/cm/{cmid}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> selectOne(@PathVariable String cmid) {
		Map<String, Object> commandMap = new HashMap<String, Object>();

		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		log.info("/cm/{cmid} GET start================>");
		try {
			CmDTO cmDTO = new CmDTO();
			commandMap.put("cmid", cmid);

			cmDTO = cmService.selectOne(commandMap);

			contents = gson.toJson(cmDTO);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContents(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/cm/{cmid} GET end================>");
		return entity;
	}
	
	

	
	// http://localhost:8080/sda/itf/cmcmici
	@RequestMapping(value = "/cmcmici", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> selectCmCmiCiList(Map<String, Object> commandMap) {
		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		List<CmCiDTO> list = new ArrayList<CmCiDTO>();
		log.info("/cmcmici GET start================>");
		try {
			list = cmService.selectCmCmiCiList(commandMap);

			contents = gson.toJson(list);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContents(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/cmcmici GET end================>");
		return entity;
	}

	// http://localhost:8080/sda/itf/cmcmici/CM-1-1-011
	@RequestMapping(value = "/cmcmici/{cmid}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> selectCmCmiCiOne(@PathVariable String cmid) {
		Map<String, Object> commandMap = new HashMap<String, Object>();

		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		log.info("/cmcmici/{cmid} GET start================>");
		try {
			CmCiDTO cmCiDTO = new CmCiDTO();
			commandMap.put("cmid", cmid);

			cmCiDTO = cmService.selectCmCmiCiOne(commandMap);

			contents = gson.toJson(cmCiDTO);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContents(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/cmcmici/{cmid} GET end================>");
		return entity;
	}

/*	
	// 여러개의 ci를 묶어서 test수행
	// http://localhost:8080/sda/itf/context
	// {"cmid":"","ciid":"CI-1-1-011","name":"임박강의실테스트","conditions":[],"execution_type":"test","schedule":"","domain":"", "remarks":""}
	@RequestMapping(value = "/context", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<ResponseMessage> test(@RequestBody RequestDTO requestDTO) {
		log.debug("requested parameter for test ==>" + requestDTO.toString());

		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		log.info("/context POST test start================>");
		try {

			List<Map<String, String>> returnMsg = cmService.test(requestDTO);

			contents = gson.toJson(returnMsg);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContent(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/context POST test end================>");
		return entity;
	}
	
	
	
	// cmid를 지정하여 test수행
	@RequestMapping(value = "/context/{cmid}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> test(@PathVariable String cmid) {
		log.debug("requested parameter(cmid) for test ==>" + cmid);

		Map<String, Object> commandMap = new HashMap<String, Object>();

		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		log.info("/context/{cmid} GET test start================>");
		try {

			commandMap.put("cmid",cmid);
			List<Map<String, String>> returnMsg = cmService.test(commandMap);

			contents = gson.toJson(returnMsg);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContent(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/context/{cmid} GET test end================>");
		return entity;
	}
	
	// cmid및 쿼리조건을 지정하여 test수행
	@RequestMapping(value = "/context/{cmid}/{args}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<ResponseMessage> test(@PathVariable String cmid, @PathVariable String args) {
		log.debug("requested parameter(cmid) for test ==>" + cmid);
		log.debug("requested parameter(args) for test ==>" + args);

		Map<String, Object> commandMap = new HashMap<String, Object>();

		ResponseMessage resultMsg = new ResponseMessage();
		ResponseEntity<ResponseMessage> entity = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		Gson gson = new Gson();
		String contents;

		log.info("/context/{cmid}/{args} GET test start================>");
		try {

			commandMap.put("cmid",cmid);
			List<Map<String, String>> returnMsg = cmService.test(commandMap, args);

			contents = gson.toJson(returnMsg);

			resultMsg.setCode(Utils.OK_CODE);
			resultMsg.setMessage(Utils.OK_MSG);
			resultMsg.setContent(contents);
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			resultMsg = Utils.makeResponseBody(e);

			responseHeaders.add("ExceptionCause", e.getMessage());
			responseHeaders.add("ExceptionClass", e.getClass().getName());
			entity = new ResponseEntity<ResponseMessage>(resultMsg, responseHeaders,
					HttpStatus.valueOf(resultMsg.getCode()));
		}
		log.info("/context/{cmid}/{args} GET test end================>");
		return entity;
	}
	*/
}
