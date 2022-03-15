package com.maxtrain.bootcamp.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestController {

	@Autowired
	private RequestRepository reqRepo;
	
	@GetMapping
	public ResponseEntity<Iterable<Request>> getRequests() {
		var req = reqRepo.findAll();
		return new ResponseEntity<Iterable<Request>>(req, HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Request> getRequests(@PathVariable int id){
		var req = reqRepo.findById(id);
		if(req.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Request>(req.get(), HttpStatus.OK);
	}
	
	//GET ALL Requests WHERE STATUS IS "REVIEW"
		@GetMapping("reviews/{userId}")
		public ResponseEntity<Iterable<Request>>getRequestsInReview(@PathVariable int userId){
			var req = reqRepo.findByStatusAndNotUserId("REVIEW", userId);
			return new ResponseEntity<Iterable<Request>>(req, HttpStatus.OK);
		}
	
	@PostMapping
	public ResponseEntity<Request> postRequest(@RequestBody Request request){
		if(request == null || request.getId() != 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var inv = reqRepo.save(request);
		return new ResponseEntity<Request>(HttpStatus.CREATED);	
	}
	
	// Sets status to approved if total is under 50 or review if it is over 50
		@SuppressWarnings("rawtypes")
		@PutMapping("review/{id}")
		public ResponseEntity reviewRequest(@PathVariable int id, @RequestBody Request request) {
			var statusValue= (request.getTotal() <= 50) ? "APPROVED" : "REVIEW";
			request.setStatus(statusValue);
			return putRequest(id, request);
		}
		
		// APPROVING AN INVOICE
		@SuppressWarnings("rawtypes")
		@PutMapping("approve/{id}")
		public ResponseEntity approveRequest(@PathVariable int id, @RequestBody Request request) {
			request.setStatus("APPROVED");
			return putRequest(id, request);
		}
		//REJECTING AN INVOICE
		@SuppressWarnings("rawtypes")
		@PutMapping("reject/{id}")
		public ResponseEntity rejectRequest(@PathVariable int id, @RequestBody Request request) {
			request.setStatus("REJECTED");
			return putRequest(id, request);
		}
	
	@SuppressWarnings("rawtypes")
	@PutMapping("{id}")
	public ResponseEntity putRequest(@PathVariable int id, @RequestBody Request request) {
		if(request == null || request.getId()== 0) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		var req = reqRepo.findById(request.getId());
		if(req.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		reqRepo.save(request);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@SuppressWarnings("rawtypes")
	@DeleteMapping("{id}")
	public ResponseEntity deleteRequest(@PathVariable int id) {
		var req = reqRepo.findById(id);
		if(req.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);			
		}
		reqRepo.delete(req.get());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);		
	}
	
}
