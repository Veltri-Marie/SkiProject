package be.veltri.pojo;

import java.time.LocalDate;
import java.util.Objects;

public class Person {
    // ATTRIBUTES
    private int id_person;
    private String firstName;
    private String lastName;
    private LocalDate birthdate; 

    // CONSTRUCTORS
    public Person() {}

    public Person(int id_person, String firstName, String lastName, LocalDate birthdate) {
    	this();
        this.id_person = id_person;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    // PROPERTIES
    public int getId() {
        return id_person;
    }

    public void setId(int id) {
        this.id_person = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public int calculateAge() {
    	int age = 0;
    	
    	if (birthdate != null) {
        	age = java.time.Period.between(this.birthdate, LocalDate.now()).getYears();
    	}
    	
    	return age;
    }
    
    @Override
    public String toString() {
        return firstName + " " + lastName; 
        }


    @Override
    public int hashCode() {
        return Objects.hash(id_person);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id_person == person.id_person;
    }
}
