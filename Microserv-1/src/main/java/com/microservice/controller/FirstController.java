package com.microservice.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.json.simple.JSONObject;
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

	@GetMapping("/health")
	@ApiOperation(value="This is going to check the services are up or not.")
	public ResponseEntity<String> health() {
		String serverStatus = "Up";
		return new ResponseEntity<String>(serverStatus,HttpStatus.OK);
	}
	
		
	@SuppressWarnings("unchecked")
	@PostMapping("/ConcatResponse")
	@ApiOperation(value="This will concatenate the response from 2nd Microservice and 3rd Microservice.")
	public String concatResponse(@Validated @RequestBody CustomerDTO customerDTO)
	{
		 //String url="http://localhost:8201/SecondMicroservApp/getString";
		String url="http://two-env.eba-dwijrjek.us-east-2.elasticbeanstalk.com/getString";
		String responseEntity =   restTemplate.getForObject(url, String.class);
		//String url1="http://localhost:8202/ThirdMicroservApp/Print";
		String url1="http://microservice3-env.eba-qncm2yhd.us-east-2.elasticbeanstalk.com/Print";

		ResponseEntity<String> response=restTemplate.postForEntity(url1, customerDTO, String.class);
		 String body = response.getBody();
		String finalStr=responseEntity+body;
		JSONObject json = new JSONObject();
		json.put("Concatenated Response",finalStr);
		return json.toString();
		
	}
	
	@PostMapping("/createTable")
	@ApiOperation(value="This will create a table in datbase.")
	public Customer createCustomer(@RequestBody CustTabledto custTabledto) {
		
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
	
	boolean notfound=true;
	
	@GetMapping(value= "/byId/{Id}")
	@ApiOperation(value="This will search the data by Id",response=Customer.class)
//	@ApiResponses(value= {@ApiResponse (code=200,message="Fetched data successfully."),
//			@ApiResponse (code=404,message="Data is not found.")})
	public Customer readCustomer( @PathVariable ("Id") int Id) throws NoSuchCustomerException {
		
		Optional<Customer> optional= custRepo.findById(Id);
		Customer cust=optional.get();
		return cust;
	}
	
	
	@GetMapping("/allCustomers")
	@ApiOperation(value="This will give complete list of objects in table in nested JSON format.")
	public List<Customers> readAllCustomer() {		
		List<Customer> custList1= custRepo.findAll();
		List<CustTabledto> custList= setCustomerDTO(custList1);
		
		List<Customers> cs= new ArrayList<>();
		for(int i=0;i<custList.size();i++) {
			Customers c= new Customers();
			if(custList.get(i).getSelect().equals("N")) {
				
			c.setName(custList.get(i).getName());
			
		if(isNextRound(custList, custList.get(i).getId()))
			c.setSub_Class(addSubClass(custList, i));
			cs.add(c);
		}
		}
		return cs;
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
	
	public List<Customers> addSubClass(List<CustTabledto> custList, int i) {
		List<Customers> sub_Class = new ArrayList<>();
		for(int j=i+1;j<custList.size();j++) {
			if(custList.get(i).getId() == custList.get(j).getParentId()) {
				custList.get(j).setSelect("Y");;
				Customers c1= new Customers();
				c1.setName(custList.get(j).getName());
				if(isNextRound(custList, custList.get(j).getId()))
					c1.setSub_Class(addSubClass(custList, j));
				
				sub_Class.add(c1);
			}
			
		}
		return sub_Class;
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
		String response = "Customer of id: "+id+"deleted Successfully";
		if(!custRepo.existsById(id))
			throw new NoSuchCustomerException("Customer does not exist :"+id);
		//DeleteById already handles exception of not record found
		 custRepo.deleteById(id);
		
		return response;
	}
}


