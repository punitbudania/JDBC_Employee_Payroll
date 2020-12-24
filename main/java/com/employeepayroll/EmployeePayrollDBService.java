package com.employeepayroll;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {
    private int connectionCounter = 0;
    private PreparedStatement employeePayrollDataStatement;
    private static EmployeePayrollDBService employeePayrollDBService;
    private EmployeePayrollDBService() {}
    HashMap<String, Integer> departments = new HashMap<>();


    public static EmployeePayrollDBService getInstance()
    {
        if(employeePayrollDBService == null)
            employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    private Connection getConnection() throws SQLException
    {
        connectionCounter++;
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service_db?useSSL=false";
        String userName = "root";
        String password = "110016";
        Connection con;
        //System.out.println("Connecting to database" + jdbcURL);
        System.out.println("Processing thread: " + Thread.currentThread().getName() +
                            " Connecting to database with id:" + connectionCounter);
        con = DriverManager.getConnection(jdbcURL, userName, password);
        System.out.println("Processing thread: " + Thread.currentThread().getName() +
                            " Id: " + connectionCounter + " Connection is successful!!" + con);
        //System.out.println("Connection is successful!!!" + con);
        return  con;
    }

    public List<EmployeePayrollData> readData() throws SQLException
    {
        String sql = "SELECT * FROM employee_payroll;";
        List<EmployeePayrollData> employeePayrollList;
        Connection connection = this.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        employeePayrollList = this.getEmployeePayrollData(resultSet);
        connection.close();
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollForDateRange(LocalDate startDate, LocalDate endDate)
    {
        String sql = String.format("SELECT * FROM employee_payroll WHERE START BETWEEN '%s' AND '%s';",
                                    Date.valueOf(startDate), Date.valueOf(endDate));
        return this.getEmployeePayrollDataUsingDB(sql);
    }

    public Map<String, Double> getAverageSalaryByGender()
    {
        String sql = "SELECT gender, AVG(salary) as avg_salary FROM employee_payroll GROUP BY gender;";
        Map<String, Double> genderToAverageSalaryMap = new HashMap<>();
        try(Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next())
            {
                String gender = resultSet.getString("gender");
                double salary = resultSet.getDouble("avg_salary");
                genderToAverageSalaryMap.put(gender, salary);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return genderToAverageSalaryMap;
    }

    private List<EmployeePayrollData> getEmployeePayrollDataUsingDB(String sql)
    {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try(Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    public List<EmployeePayrollData> getEmployeePayrollData(String name)
    {
        List<EmployeePayrollData> employeePayrollList = null;
        if (this.employeePayrollDataStatement == null)
            this.prepareStatementForEmployeeData();
        try
        {
            employeePayrollDataStatement.setString(1, name);
            ResultSet resultSet = employeePayrollDataStatement.executeQuery();
            employeePayrollList = this.getEmployeePayrollData(resultSet);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet)
    {
        List<EmployeePayrollData> employeePayrollList = new ArrayList<>();
        try
        {
            while (resultSet.next())
            {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start").toLocalDate();
                employeePayrollList.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollList;
    }

    private void prepareStatementForEmployeeData()
    {
        try
        {
            Connection connection =this.getConnection();
            String sql = "SELECT * FROM employee_payroll WHERE name = ?";
            employeePayrollDataStatement = connection.prepareStatement(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
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

    public EmployeePayrollData addEmployeeToPayrollUC7(String name, double salary, LocalDate startDate, String gender)
    {
        int employeeId = -1;
        EmployeePayrollData employeePayrollData = null;
        String sql = String.format("INSERT INTO employee_payroll (name, gender, salary, start)" +
                            "VALUES ('%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(startDate));
        try(Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1)
            {
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return employeePayrollData;
    }


    public EmployeePayrollData addEmployeeToPayroll(String name, double salary, LocalDate startDate, String gender)
    {
        int employeeId = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try
        {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        try(Statement statement = connection.createStatement())
        {
            String sql = String.format("INSERT INTO employee_payroll (name, gender, salary, start)" +
                    "VALUES ('%s', '%s', '%s', '%s')", name, gender, salary, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1)
            {
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeId = resultSet.getInt(1);
            }
            employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement())
        {
            double deductions = salary*0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay*0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details" +
                                    "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES" +
                                    "(%s, %s, %s, %s, %s, %s)", employeeId, salary, deductions, taxablePay, tax, netPay);
            int rowAffected = statement.executeUpdate(sql);
            if(rowAffected == 1)
            {
                employeePayrollData = new EmployeePayrollData(employeeId, name, salary, startDate);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally
        {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }

    public EmployeePayrollData addEmployeeToDatabase(String company, String name, String gender, double salary, LocalDate startDate, String department)
    {
        departments.put("IT", 1);
        departments.put("HR", 2);
        int employeeID = -1;
        Connection connection = null;
        EmployeePayrollData employeePayrollData = null;
        try
        {
            connection = this.getConnection();
            connection.setAutoCommit(false);
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }

        try (Statement statement = connection.createStatement())
        {
            String sql = String.format("INSERT INTO employee_payroll (company, name, gender, salary, start)" +
                    "VALUES ('%s', '%s', '%s', '%s', '%s')", company, name, gender, salary, Date.valueOf(startDate));
            int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
            if(rowAffected == 1)
            {
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) employeeID = resultSet.getInt(1);
            }
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try(Statement statement = connection.createStatement())
        {
            double deductions = salary*0.2;
            double taxablePay = salary - deductions;
            double tax = taxablePay*0.1;
            double netPay = salary - tax;
            String sql = String.format("INSERT INTO payroll_details" +
                    "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) VALUES" +
                    "(%s, %s, %s, %s, %s, %s)", employeeID, salary, deductions, taxablePay, tax, netPay);
            statement.executeUpdate(sql);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try (Statement statement = connection.createStatement())
        {
            int dept_id = -1;
            for (String i : departments.keySet())
            {
                if(i == department)
                {
                    dept_id = departments.get(i);
                }
            }
            String sql = String.format("INSERT INTO employee_department (employee_id, department_id) VALUES (%s, %s)", employeeID, dept_id);
            int rowAffected = statement.executeUpdate(sql);
            if(rowAffected == 1)
            {
                employeePayrollData = new EmployeePayrollData(employeeID, name, salary, startDate);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            try {
                connection.rollback();
                return employeePayrollData;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        finally
        {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return employeePayrollData;
    }

    public void removeEmployeeFromDB(String name)
    {
        String sql = String.format("UPDATE employee_payroll SET is_active = false WHERE name = '%s'", name);
        try(Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
    }

    public int countActiveEmployees()
    {
        int count = -1;
        String sql = "SELECT SUM(is_active) FROM employee_payroll";
        try(Connection connection = this.getConnection())
        {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) count = resultSet.getInt(1);
        }
        catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return count;
    }
}
