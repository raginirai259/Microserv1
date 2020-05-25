package com.microservice2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecondController {
	
	@GetMapping("/getString")
	public ResponseEntity<String> getString()
	{

		return  ResponseEntity.ok("Hello!");
	}

}
