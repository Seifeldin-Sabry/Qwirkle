package appName.model.game;

import java.sql.*;

public class DBListener {
    private String db_username = "postgres";
    private String db_password = "Student_1234";
    private String db_URL = "jdbc:postgresql://localhost:5432/qwirkle_group14";

    //Constructor
    public DBListener(){
        createDatabase();
        createTables();
    }


    //Setters and Getters
    public void setDb_username(String db_username) {
        this.db_username = db_username;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public void setDb_URL(String db_URL) {
        this.db_URL = db_URL;
    }

    public String getDb_username() {
        return db_username;
    }

    public String getDb_password() {
        return db_password;
    }

    public String getDb_URL() {
        return db_URL;
    }

    //Methods

    //Execute update queries
    public void updateQuery(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(getDb_URL(), getDb_username(), getDb_password());
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
        closeConnection(statement, connection);
    }

    //Execute select queries
    public void selectQuery(String query) throws SQLException {
        Connection connection = DriverManager.getConnection(getDb_URL(), getDb_username(), getDb_password());
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        closeConnection(resultSet, statement, connection);
    }

    //Close connections for table updates
    public void closeConnection(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    //Close connections for select queries
    public void closeConnection(ResultSet resultSet, Statement statement, Connection connection) throws SQLException {
        resultSet.close();
        statement.close();
        connection.close();
    }

    //SQL Database creation
    private void createDatabase() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/",
                    getDb_username(),
                    getDb_password());
            Statement statement = connection.createStatement();
            ResultSet selectResult = statement.executeQuery("SELECT FROM pg_database WHERE LOWER(datname) = 'qwirkle_group14';");
            if (!selectResult.next()) {
                statement.executeUpdate("CREATE DATABASE qwirkle_group14;");
                closeConnection(selectResult, statement, connection);
            }
//        } catch (PSQLException var3) {
//            System.exit(0);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }
    }

    //SQL Tables creation
    private void createTables() {
        try {
            Connection connection = DriverManager.getConnection(getDb_URL(), getDb_username(), getDb_password());
            Statement statement = connection.createStatement();
            statement.executeUpdate("");
            closeConnection(statement, connection);
        } catch (SQLException var4) {
            var4.printStackTrace();
        }
    }
}
