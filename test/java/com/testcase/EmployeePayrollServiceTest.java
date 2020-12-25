package com.testcase;

import com.employeepayroll.EmployeePayrollData;
import com.employeepayroll.EmployeePayrollService;
import org.junit.Assert;
import org.junit.Test;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmployeePayrollServiceTest
{
    @Test
    public void given3Employees_WhenWrittenToFile_ShouldMatchEmployeeEntries()
    {
        EmployeePayrollData[] arrayOfEmps = {
             new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };
        EmployeePayrollService employeePayrollService;
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenEmployeePayrollDB_ShouldMatchDataCount() throws SQLException
    {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() throws SQLException
    {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.updateEmployeeData("Terisa", 3000000.0);
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
        Assert.assertTrue(result);
    }

    @Test
    public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        LocalDate startDate = LocalDate.of(2018, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollForDateRange(EmployeePayrollService.IOService.DB_IO, startDate, endDate);
        Assert.assertEquals(3, employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(EmployeePayrollService.IOService.DB_IO);
        Assert.assertTrue(averageSalaryByGender.get("M").equals(2000000.0) &&
                            averageSalaryByGender.get("F").equals(3000000.0));
    }

    @Test
    public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeTOPayroll("Mark", 5000000.0, LocalDate.now(), "M");
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
        Assert.assertTrue(result);
    }

    @Test
    public void givenEmployee_WhenAdded_ShouldAddedToAllTables() throws SQLException
    {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.addEmployeeToDatabase("CapG", "Rohit", "M", 1000000.0, LocalDate.now(),"IT");
        boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Rohit");
        Assert.assertTrue(result);
    }

    @Test
    public void givenEmployee_WhenRemoved_ShouldReturnUpdatedCount() throws SQLException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        employeePayrollService.removeEmployeeFromDB("Mark");
        int result = employeePayrollService.countActiveEmployees();
        Assert.assertEquals(1, result);
    }

    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeEntries() throws SQLException
    {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mark Zuckerberg", "M", 300000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Sundar", "M", 600000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Mukesh", "M", 100000.0, LocalDate.now()),
                new EmployeePayrollData(0, "Anil", "M", 200000.0, LocalDate.now()),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeeTOPayroll(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without thread" + Duration.between(start, end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeeTOPayrollWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with thread" + Duration.between(threadStart, threadEnd));
        employeePayrollService.printData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(13, employeePayrollService.countEntries(EmployeePayrollService.IOService.DB_IO));
    }


    @Test
    public void given6Employees_WhenAddedToDB_ShouldMatchEmployeeCount() throws SQLException
    {
        EmployeePayrollData[] arrayOfEmps = {
                new EmployeePayrollData(0, "Jeff Bezos", "M", 100000.0, LocalDate.now(), "Amazon", "IT"),
                new EmployeePayrollData(0, "Bill Gates", "M", 200000.0, LocalDate.now(), "Microsoft", "HR"),
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
        Instant start = Instant.now();
        employeePayrollService.addEmployeeToDB(Arrays.asList(arrayOfEmps));
        Instant end = Instant.now();
        System.out.println("Duration without thread" + Duration.between(start, end));
        Instant threadStart = Instant.now();
        employeePayrollService.addEmployeeToDBWithThreads(Arrays.asList(arrayOfEmps));
        Instant threadEnd = Instant.now();
        System.out.println("Duration with thread" + Duration.between(threadStart, threadEnd));
        employeePayrollService.printData(EmployeePayrollService.IOService.DB_IO);
        Assert.assertEquals(3, employeePayrollService.countActiveEmployees());
    }
}
