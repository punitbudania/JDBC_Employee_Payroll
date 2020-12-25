package com.employeepayroll;

import java.time.LocalDate;
import java.util.Objects;

public class EmployeePayrollData
{
    public int id;
    public String name;
    public String gender;
    public double salary;
    public LocalDate startDate;
    public String company;
    public String department;

    @Override
    public int hashCode()
    {
        return Objects.hash(name, gender, salary, startDate);
    }

    public EmployeePayrollData(Integer id, String name, Double salary)
    {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public EmployeePayrollData(Integer id, String name, Double salary, LocalDate startDate)
    {
        this(id, name, salary);
        this.startDate = startDate;
    }

    public EmployeePayrollData(int id, String name, String gender, double salary, LocalDate startDate)
    {
        this(id, name, salary, startDate);
        this.gender = gender;
    }

    public EmployeePayrollData(int id, String name, String gender, double salary, LocalDate startDate, String company, String department)
    {
        this(id, name, gender, salary, startDate);
        this.company = company;
        this.department = department;
    }

    @Override
    public String toString()
    {
        return "id=" + id +", name=" + name + ", salary=" + salary;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeePayrollData that = (EmployeePayrollData) o;
        return id == that.id &&
                     Double.compare(that.salary, salary) == 0 &&
                     Objects.equals(name, that.name);
    }

}
