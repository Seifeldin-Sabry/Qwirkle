package src.qwirkle.data;

import qwirkle.model.GameSession;
import qwirkle.model.PlayerSession;

import java.sql.*;

/**
 * This is a @Singleton implementation
 * Ensures only one instance of the Database is present
 *
 * @author Seifeldin Ismail
 */

public class Database {
    //Instance field
    private static Database instance = null;
    private static final String jdbc = "jdbc:postgresql://localhost:5432/";
    private String username = "postgres";
    private String password = "postgres";
    private boolean isLoggedIn = false;
    private Connection connection;




    private Database(){

    }

    public static Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }



    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     Asks for postgres password and saves it in class variable password
     <br>
     Creates the database in case it doesn't exist
     * @return
     */
    public boolean createDatabase() {
        try {
            Connection connection = setConnection();
            Statement statement = connection.createStatement();
            ResultSet selectResult = statement.executeQuery("SELECT FROM pg_database WHERE LOWER(datname) = 'qwirkle_group14';");
            if (!selectResult.next()) {
                statement.executeUpdate("CREATE DATABASE qwirkle_group14;");
            }
//        } catch (PSQLException var3) {
//            System.exit(0);
            return true;
        }catch (SQLException var4) {
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getJdbc() {
        return jdbc;
    }

    /**
     Sets up postgres server connection

     @return <code>Connection</code>
     */
    public Connection setConnection() {
        try {
            connection = DriverManager.getConnection(jdbc, username, password);
            return connection;
        } catch (SQLException e) {
            System.out.println("Error while creating the connection to the postgres");
        }
        return null;
    }





    public boolean logIn(){
        if (!createDatabase()) return false;
        isLoggedIn = true;
        createSaveGameTables();
        System.out.println("Logged in");
        return true;
    }


    /**
     * Truncates all data in all tables and restarts identity columns and sequences
     */
    public void deleteData() {
        Statement stmt;
        try {
            Connection connection = getConnection();
            stmt = connection.createStatement();

            String truncSql = """
                    TRUNCATE TABLE int_gamesession CASCADE ;
                    TRUNCATE TABLE int_player CASCADE ;
                    TRUNCATE TABLE int_playersession CASCADE;
                    TRUNCATE TABLE int_score CASCADE ;
                    TRUNCATE TABLE int_turn CASCADE;
                    TRUNCATE TABLE int_tile CASCADE;
                    TRUNCATE TABLE int_move CASCADE;
                    
                    ALTER SEQUENCE player_id_seq RESTART;
                    ALTER SEQUENCE playersession_id_seq RESTART;
                    ALTER SEQUENCE game_id_seq RESTART;
                    """;

            stmt.executeUpdate(truncSql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error while trying to drop all tables and sequences.");
        }
    }

    /**
     * Creating all tables
     */
    public boolean createSaveGameTables() {
        Statement stmt;
        String CreateSql;

        try {
            Connection connection = getConnection();
            stmt = connection.createStatement();

            CreateSql = """					                        
                    CREATE SEQUENCE IF NOT EXISTS game_id_seq
                    START WITH 1
                    INCREMENT BY 1
                    NO MAXVALUE;
                    
                    CREATE SEQUENCE IF NOT EXISTS playersession_id_seq
                    START WITH 1
                    INCREMENT BY 1
                    MAXVALUE 2
                    CYCLE;
                    
                    CREATE SEQUENCE IF NOT EXISTS player_id_seq
                    START WITH 1
                    INCREMENT BY 1
                    NO MAXVALUE;
                    
                    
                    create table if not exists int_gamesession
                    (
                        game_id       integer   not null primary key,
                        game_duration integer   not null,
                        date_played   date not null ,
                        start_time    timestamp not null,
                        end_time      timestamp not null
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_player(
                    	player_id INT PRIMARY KEY,
                    	player_name VARCHAR(32) NOT NULL
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_playersession(
                    	psession_id INT UNIQUE,
                    	player_id INT REFERENCES INT_PLAYER(player_id) ON DELETE CASCADE,
                    	game_id INT REFERENCES INT_GAMESESSION(game_id) ON DELETE CASCADE,
                    	CONSTRAINT playersession_pkey PRIMARY KEY (psession_id,player_id,game_id)
                    );
                    
                    
                    
                    CREATE TABLE IF NOT EXISTS int_tile(
                    	tile_id INT primary key ,
                    	game_id INT REFERENCES int_gamesession(game_id) ON DELETE CASCADE,
                    	shape VARCHAR(16),
                    	color VARCHAR(6)
                    );
                    
                    
                    CREATE TABLE IF NOT EXISTS int_score(
                    	playersession_id INT PRIMARY KEY REFERENCES int_playersession(psession_id) ON DELETE CASCADE,
                    	total_score INT,
                    	tot_time_spent_turns INT
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_turn(
                    	playersession_id INT REFERENCES int_playersession(psession_id) ON DELETE CASCADE,
                    	turn_no INT NOT NULL UNIQUE,
                    	time_spent INT NOT NULL,
                    	time_played TIMESTAMP NOT NULL,
                    	CONSTRAINT turn_pkey PRIMARY KEY (playersession_id,turn_no)
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_move(
                    	playersession_id INT REFERENCES int_playersession(psession_id) ON DELETE CASCADE,
                    	turn_no INT REFERENCES int_turn(turn_no),
                    	move_no INT NOT NULL,
                    	x INT,
                    	y INT,
                    	tile_id INT REFERENCES int_tile(tile_id),
                    	CONSTRAINT move_pk PRIMARY KEY (playersession_id,move_no, turn_no)
                    );
                    """;
            stmt.executeUpdate(CreateSql);
            stmt.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Error while trying to create all tables");
            return false;
        }
    }
    public void save(GameSession session) {
        setConnection();
        createSaveGameTables();
        session.save(getConnection());
        for (PlayerSession sess: session.getPlayerSessions() ) {
            sess.save(connection);
        }
        try {
            closeConnection();
        }catch (SQLException e) {
            System.out.println("NOOOOOO");
            e.printStackTrace();
        }

    }

    private void closeConnection() throws SQLException {
        connection.close();
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
}
