package de.bsi.webflux;

import java.time.Instant;

import lombok.Data;

@Data
public class Employee {
	
    private String id;
 
    private String empNo;
 
    private String fullName;
 
    private Instant hireDate;
    
}
