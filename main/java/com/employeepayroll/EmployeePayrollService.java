package com.employeepayroll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollService
{
    public enum IOService{CONSOLE_IO, FILE_IO, DB_IO, REST_IO}
    public List<EmployeePayrollData> employeePayrollList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService()
    {
        employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollList)
    {
        this();
        this.employeePayrollList = employeePayrollList;
    }

    public static void main(String[] args)
    {
        ArrayList<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(employeePayrollList);
        employeePayrollService.writeEmployeePayrollData(IOService.CONSOLE_IO);
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws SQLException {
        if (ioService.equals(IOService.DB_IO))
        {
            this.employeePayrollList = employeePayrollDBService.readData();
        }
        return this.employeePayrollList;
    }


    public boolean checkEmployeePayrollInSyncWithDB(String name)
    {
        List<EmployeePayrollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
    }

    public void updateEmployeeData(String name, double salary)
    {
        int result = employeePayrollDBService.updateEmployeeData(name, salary);
        if (result == 0) return;
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if(employeePayrollData != null) employeePayrollData.salary = salary;
    }

    private EmployeePayrollData getEmployeePayrollData(String name)
    {
        EmployeePayrollData employeePayrollData;
        employeePayrollData = this.employeePayrollList.stream()
                .filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name))
                .findFirst()
                .orElse(null);
        return employeePayrollData;
    }

    public void writeEmployeePayrollData(IOService ioService)
    {
        if(ioService.equals(IOService.CONSOLE_IO))
        {
            System.out.println("\nWriting Employee Payroll Details to Console\n" + employeePayrollList);
        }
        else if(ioService.equals(IOService.FILE_IO))
        {
            new EmployeePayrollFileIOService().writeData(employeePayrollList);
        }
    }

    public void printData(IOService ioService)
    {
        if(ioService.equals(IOService.FILE_IO))
        {
            new EmployeePayrollFileIOService().printData();
        }
    }

    public long countEntries(IOService ioService)
    {
        if(ioService.equals(IOService.FILE_IO))
        {
            return new EmployeePayrollFileIOService().countEntries();
        }
        return 0;
    }

    public List<String> readData(IOService ioService)
    {
        if(ioService.equals(IOService.FILE_IO))
        {
            return new EmployeePayrollFileIOService().readData();
        }
        return null;
    }

}
