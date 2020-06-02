package com.microservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservice.dto.CustTabledto;
import com.microservice.dto.CustomerDTO;
import com.microservice.dto.Customers;
import com.microservice.entity.Customer;
import com.microservice.exceptions.NoSuchCustomerException;
import com.microservice.repository.CustomerRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(value="FirstController,Rest API which will deal the task 1 and 2.")
public class FirstController {
	
	@Autowired
	CustomerRepository custRepo;
	
	@Autowired
	RestTemplate restTemplate;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@GetMapping("/health")
	@ApiOperation(value="This is going to check the services are up or not.")
	public String gethealth() {
		logger.info("Health request for Application");
		return "All OK";
	}
	@GetMapping(value= "/byId/{Id}")
	@ApiOperation(value="This will search the data by Id",response=Customer.class)
	//	@ApiResponses(value= {@ApiResponse (code=200,message="Fetched data successfully."),
//			@ApiResponse (code=404,message="Data is not found.")})
	public Customer readCustomer( @PathVariable ("Id") int Id) {
		
		logger.info("Read request for customer by Id{}",Id);
		
		Optional<Customer> optional= custRepo.findById(Id);
		Customer cust=optional.get();
		return cust;
	}
	
@PostMapping("/createCust")
@ApiOperation(value="This will create a table in database.")
public Customer createCustomer(@RequestBody CustTabledto custTabledto) {
	logger.info("Creation request for customer {}", custTabledto);
		Customer cust=new Customer();
		cust.setId(custTabledto.getId());
		cust.setColor(custTabledto.getColor());
		cust.setName(custTabledto.getName());
		cust.setParentId(custTabledto.getParentId());
		System.out.println("custtableDto: "+custTabledto);
		System.out.println("cust: "+cust);
		Customer custupdated=custRepo.save(cust);
		return custupdated;
	}



@PostMapping("/ConcatResponse")
@ApiOperation(value="This will concatenate the response from 2nd Microservice and 3rd Microservice.")
public String concatResponse(@RequestBody CustomerDTO customerDTO)
{
	long overAllStart=System.currentTimeMillis();
	logger.info("Concatnated Response response for service 1 and 2 {}",customerDTO);
		String url="http://two-env.eba-dwijrjek.us-east-2.elasticbeanstalk.com/getString";
	
	long service1Start=System.currentTimeMillis();
	String responseEntity =   restTemplate.getForObject(url, String.class);
	long service1End=System.currentTimeMillis();
	
	String url1="http://microservice3-env.eba-qncm2yhd.us-east-2.elasticbeanstalk.com/Print";
	
	long secondStart=System.currentTimeMillis();
	ResponseEntity<String> response=restTemplate.postForEntity(url1, customerDTO, String.class);
	String body = response.getBody();
	long secondEnd=System.currentTimeMillis();
	 
	String finalStr=responseEntity+body;
	JSONObject json = new JSONObject();
	json.put("Concatenated Response",finalStr);
	long overAllStop=System.currentTimeMillis();
	logger.info("Total time for Service 1 {}",(service1End-service1Start));
	logger.info("Total time for Service 2 {}",(secondEnd-secondStart));
	logger.info("Total Overall time for request {}",(overAllStop-overAllStart));
	return json.toString();
	
}

@SuppressWarnings("unchecked")
@GetMapping("/allCustomers")
@ApiOperation(value="This will give complete list of objects in table in nested JSON format.")
public JSONArray readAllCustomer() {
	logger.info("Complete list of objects in table in nested JSON format");
	
	List<Customer> custList1= custRepo.findAll();
	List<CustTabledto> custList= setCustomerDTO(custList1);

	JSONArray jArray = new JSONArray();
	
	List<Customers> cs= new ArrayList<>();
	for(int i=0;i<custList.size();i++) {
		JSONObject json = new JSONObject();
		if(custList.get(i).getSelect().equals("N")) {
			
	json.put("Name", custList.get(i).getName());
	if(isNextRound(custList, custList.get(i).getId())) {

	json.put("Sub Class", addSubClass(custList, i).get(1));
	}
		jArray.add(json);
	}
	}
	
	return jArray;
}


public List<CustTabledto> setCustomerDTO(List<Customer> custList) {
	List<CustTabledto> cdto = new ArrayList<CustTabledto>();
	for(int j=0;j<custList.size();j++) {
		CustTabledto cd=new CustTabledto();
		cd.setId(custList.get(j).getId());
		cd.setColor(custList.get(j).getColor());
		cd.setName(custList.get(j).getName());
		cd.setParentId(custList.get(j).getParentId());
		cd.setSelect("N");
		cdto.add(cd);
	}
	
	return cdto;
}


@SuppressWarnings("unchecked")
public List addSubClass(List<CustTabledto> custList, int i) {
	List<Object> total = new ArrayList<>();
	List<Customers> sub_Class = new ArrayList<>();
	
	JSONArray jArray = new JSONArray();
	
	for(int j=i+1;j<custList.size();j++) {
		if(custList.get(i).getId() == custList.get(j).getParentId()) {
			custList.get(j).setSelect("Y");;
			JSONObject json = new JSONObject();
			if(isNextRound(custList, custList.get(j).getId())) {
				json.put("Sub Class", addSubClass(custList, j).get(1));
			}
			json.put("Name", custList.get(j).getName());
			jArray.add(json);
		}
		total.add(0, sub_Class);
		total.add(1, jArray);
	}
	return total;
}

public Boolean isNextRound(List<CustTabledto> custList, int id) {
	for(int i=0;i<custList.size();i++) {
		if(id==custList.get(i).getParentId())
			return true;
	}
	return false;
}


@DeleteMapping("/deleteById/{Id}")
@ApiOperation(value="This will delete the data by Id",response=Customer.class)
public String deleteCustomer(@PathVariable ("Id") int  id) throws NoSuchCustomerException
{   
	logger.info("Delete request for customer by Id{}", id);
	String response = "Customer of id: "+id+" deleted Successfully";
	if(!custRepo.existsById(id))
		throw new NoSuchCustomerException("Customer does not exist with Id :"+id);
	 custRepo.deleteById(id);
	
	return response;
}

}


