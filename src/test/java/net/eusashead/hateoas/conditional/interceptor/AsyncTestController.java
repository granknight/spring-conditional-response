package net.eusashead.hateoas.conditional.interceptor;

import java.util.concurrent.Callable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/async/{id}")
public class AsyncTestController {

	@RequestMapping(method=RequestMethod.GET) 
	public Callable<ResponseEntity<String>> get() {
		return new Callable<ResponseEntity<String>>() {

			@Override
			public ResponseEntity<String> call() throws Exception {
				HttpHeaders headers = new HttpHeaders();
				headers.setETag("\"123456\"");
				return new ResponseEntity<String>("hello", headers, HttpStatus.OK);
			}
		};

	}

	@RequestMapping(method=RequestMethod.HEAD) 
	public Callable<ResponseEntity<Void>> head() {
		return new Callable<ResponseEntity<Void>>() {

			@Override
			public ResponseEntity<Void> call() throws Exception {
				HttpHeaders headers = new HttpHeaders();
				headers.setETag("\"123456\"");
				return new ResponseEntity<Void>(headers, HttpStatus.OK);
			}
		};

	}

	@RequestMapping(method=RequestMethod.PUT) 
	public Callable<ResponseEntity<Void>> put() {
		return new Callable<ResponseEntity<Void>>() {

			@Override
			public ResponseEntity<Void> call() throws Exception {
				HttpHeaders headers = new HttpHeaders();
				headers.setETag("\"123456\"");
				return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
			}
		};
	}

	@RequestMapping(method=RequestMethod.DELETE) 
	public Callable<ResponseEntity<Void>> delete() {
		return new Callable<ResponseEntity<Void>>() {

			@Override
			public ResponseEntity<Void> call() throws Exception {
				return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
			}
		};
	}

	@RequestMapping(method=RequestMethod.POST) 
	public Callable<ResponseEntity<Void>> post() {
		return new Callable<ResponseEntity<Void>>() {

			@Override
			public ResponseEntity<Void> call() throws Exception {
				return new ResponseEntity<Void>(HttpStatus.CREATED);
			}
		};
	}
}
