package com.employeepayroll;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<EmployeePayrollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate, LocalDate endDate)
    {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getEmployeePayrollForDateRange(startDate, endDate);
        return null;
    }

    public Map<String, Double> readAverageSalaryByGender(IOService ioService)
    {
        if(ioService.equals(IOService.DB_IO))
            return employeePayrollDBService.getAverageSalaryByGender();
        return null;
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

    public void addEmployeeTOPayroll(List<EmployeePayrollData> employeePayrollDataList)
    {
        employeePayrollDataList.forEach(employeePayrollData -> {
            System.out.println("Employee being added: " + employeePayrollData.name);
            this.addEmployeeTOPayroll(employeePayrollData.name, employeePayrollData.salary,
                    employeePayrollData.startDate, employeePayrollData.gender);
            System.out.println("Employee Added: " + employeePayrollData.name);
        });
        //System.out.println(this.employeePayrollList);
    }

    public void addEmployeeTOPayrollWithThreads(List<EmployeePayrollData> employeePayrollDataList)
    {
        Map<Integer, Boolean> employeeAdditionStatus = new HashMap<Integer, Boolean>();
        employeePayrollDataList.forEach(employeePayrollData -> {
            Runnable task = () -> {
                employeeAdditionStatus.put(employeePayrollData.hashCode(), false);
                System.out.println("Employee being added: " + Thread.currentThread().getName());
                this.addEmployeeTOPayroll(employeePayrollData.name, employeePayrollData.salary,
                        employeePayrollData.startDate, employeePayrollData.gender);
                employeeAdditionStatus.put(employeePayrollData.hashCode(), true);
                System.out.println("Employee added: " + Thread.currentThread().getName());
            };
            Thread thread = new Thread(task, employeePayrollData.name);
            thread.start();
        });
        while (employeeAdditionStatus.containsValue(false))
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(employeePayrollDataList);
    }

    public void addEmployeeToDatabase(String company, String name, String gender, double salary, LocalDate startDate, String department)
    {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToDatabase(company, name, gender, salary, startDate, department));
    }

    public void addEmployeeTOPayroll(String name, double salary, LocalDate startDate, String gender)
    {
        employeePayrollList.add(employeePayrollDBService.addEmployeeToPayroll(name, salary, startDate, gender));
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
        else System.out.println(employeePayrollList);
    }

    public long countEntries(IOService ioService)
    {
        if(ioService.equals(IOService.FILE_IO))
        {
            return new EmployeePayrollFileIOService().countEntries();
        }
        return employeePayrollList.size();
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
