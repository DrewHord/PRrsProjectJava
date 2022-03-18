package com.maxtrain.bootcamp.requestline;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.maxtrain.bootcamp.request.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/requestlines")
public class RequestlineController {
	@Autowired 
	private RequestlineRepository reqlnRepo;
	
	@Autowired
	private RequestRepository reqRepo;
	
	@SuppressWarnings("rawtypes")
	private ResponseEntity recalcRequestTotal(int requestId) {
		var reqOpt = reqRepo.findById(requestId);
		if(reqOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		var request = reqOpt.get();
		var requestTotal = 0;
		for(var requestline : request.getRequestlines()) {
			requestTotal += requestline.getProduct().getPrice() * requestline.getQuantity();
		}
		request.setTotal(requestTotal);
		reqRepo.save(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}


	
	@GetMapping
	public ResponseEntity<Iterable<Requestline>> getRequestlines() {
		var reqln = reqlnRepo.findAll();
		return new ResponseEntity<Iterable<Requestline>>(reqln, HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Requestline> getRequestline(@PathVariable int id){
		var reqln = reqlnRepo.findById(id);
		if(reqln.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Requestline>(reqln.get(), HttpStatus.OK);
	}
		
	@PostMapping
	public ResponseEntity<Requestline> postRequestline(@RequestBody Requestline requestline) throws Exception{
		if(requestline == null || requestline.getId() != 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var reqln = reqlnRepo.save(requestline);
		var respEntity = this.recalcRequestTotal(reqln.getRequest().getId());
		if(respEntity.getStatusCode() != HttpStatus.OK) {
			throw new Exception("Recalculate request tot failed!");
		}
		return new ResponseEntity<Requestline>(HttpStatus.CREATED);	
	}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putRequestline(@PathVariable int id, @RequestBody Requestline requestline) throws Exception {
		if(requestline == null || requestline.getId()== 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var reqln = reqlnRepo.findById(requestline.getId());
		if(reqln.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
			var reql =reqln.get();
			reqlnRepo.save(reql);
			var respEntity = this.recalcRequestTotal(reql.getRequest().getId());
			if(respEntity.getStatusCode() != HttpStatus.OK) {
				throw new Exception("Recalculate request total failed!");
			}		
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
		@SuppressWarnings("rawtypes")
		@DeleteMapping("{id}")
		public ResponseEntity deleteRequestline(@PathVariable int id) throws Exception {
			var requestOpt = reqlnRepo.findById(id);
			if(requestOpt.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			var request = requestOpt.get();
			reqlnRepo.delete(request);
			var respEntity = this.recalcRequestTotal(request.getId());
			if(respEntity.getStatusCode() != HttpStatus.OK) {
				throw new Exception("Recalculate request total failed!");
			}
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

}
