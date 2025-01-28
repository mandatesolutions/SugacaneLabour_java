//package com.sugarcanelabour.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.sugarcanelabour.entity.CommonLogin;
//import com.sugarcanelabour.model.RegistrationDto;
//import com.sugarcanelabour.service.CommonLoginService;
//
//import io.swagger.v3.oas.annotations.Operation;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@Slf4j
//@RequestMapping("/sclm/coworker")
//public class CoworkerController {
//	
//	private CommonLoginService commonLoginService;
//	
//	  @Operation(summary = "Register Coworker (Laborer)", description = "This API is used by Supervisor to register a Coworker.")
//	    @PostMapping("/register-laborer")
//	    ResponseEntity<Object> registerLaborer(@Valid @RequestBody RegistrationDto registrationDto) {
//	        log.info("***** Inside CoworkerController - registerLaborer *****");
//	        CommonLogin commonLogin = commonLoginService.registerLaborer(registrationDto);
//	        return ResponseEntity.status(HttpStatus.CREATED).body(commonLogin);
//	    }
//}
