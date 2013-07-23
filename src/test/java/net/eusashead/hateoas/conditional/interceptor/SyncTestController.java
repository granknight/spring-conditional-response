package net.eusashead.hateoas.conditional.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/sync/{id}")
public class SyncTestController {
	
	@RequestMapping(method=RequestMethod.GET) 
	public ResponseEntity<String> get() {
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		return new ResponseEntity<String>("hello", headers, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.HEAD) 
	public ResponseEntity<String> head() {
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		return new ResponseEntity<String>(headers, HttpStatus.OK);
	}
	
	@RequestMapping(method=RequestMethod.PUT) 
	public ResponseEntity<Void> put() {
		HttpHeaders headers = new HttpHeaders();
		headers.setETag("\"123456\"");
		return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(method=RequestMethod.DELETE) 
	public ResponseEntity<Void> delete() {
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(method=RequestMethod.POST) 
	public ResponseEntity<Void> post() {
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

}
