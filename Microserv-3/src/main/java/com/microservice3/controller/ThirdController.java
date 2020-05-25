package com.microservice3.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservice3.dto.CustomerDTO;

@RestController
public class ThirdController {

		
	@PostMapping("/Print")
	public String printDetails(@RequestBody CustomerDTO custdto)
	{
		
		return custdto.getFirstName()+" "+ custdto.getLastNname();
		
	}
}
