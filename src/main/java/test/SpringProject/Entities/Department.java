package test.SpringProject.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "Departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "departmentId")
    private long departmentId;
    private String department;

    public Department(int departmentId, String department) {
        this.departmentId = departmentId;
        this.department = department;
    }

    public Department() {
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
