package de.bsi.webflux.database;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Employee")
public class EmployeeDAO {
	
	@Id
    private String id;
 
	private String empNo;
 
    private String fullName;
 
    private LocalDate hireDate;
    
}
