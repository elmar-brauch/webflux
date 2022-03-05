package de.bsi.webflux.database;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Employee")
public class EmployeeDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    private String id;
 
	private String empNo;
 
    private String fullName;
 
    private LocalDate hireDate;
    
}
