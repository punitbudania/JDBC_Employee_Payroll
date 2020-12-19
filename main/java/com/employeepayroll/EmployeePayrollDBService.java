package com.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePayrollDBService {
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService() {}

    public static EmployeePayrollDBService getInstance()
    {
        if(employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException
    {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service_db?useSSL=false";
        String userName = "root";
        String password = "110016";
        Connection con;
        System.out.println("Connecting to database" + jdbcURL);
        con = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Connection is successful!!!" + con);
        return  con;
    }

    public List<EmployeePayrollData> readData() throws SQLException
    {
        String sql = "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        Connection connection = this.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next())
        {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            double salary = resultSet.getDouble("salary");
            LocalDate startDate = resultSet.getDate("start").toLocalDate();
            employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
        }
        connection.close();
        return employeePayrollList;
    }

    public int updateEmployeeData(String name, double salary)
    {
        return this.updateEmployeeDataUsingStatement(name, salary);
    }

    private int updateEmployeeDataUsingStatement(String name, double salary)
    {
        String sql = String.format("update employee_payroll set salary = %.2f where name = '%s'", salary, name);
        try (Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
