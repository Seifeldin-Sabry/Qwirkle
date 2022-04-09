package qwirkle.data;

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
    private static final String jdbc = "jdbc:postgresql://localhost:5432/qwirkledb";
    private String username = "postgres";
    private String password = "ss";
    private boolean isLoggedIn = false;
    private Connection connection;


    private Database(){}

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
     * @return if database successfully created
     */
    public boolean createDatabase() {
        try {
            Connection connection = setConnection();
            Statement statement = connection.createStatement();
            ResultSet selectResult = statement.executeQuery("SELECT FROM pg_database WHERE LOWER(datname) = 'qwirkledb';");
            if (!selectResult.next()) {
                statement.executeUpdate("CREATE DATABASE qwirkledb;");
            }
        }catch (SQLException e) {
            return false;
        }
        return true;
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
        } catch (SQLException ignored) {
//            System.out.println("Error while creating the connection to the postgres");
            return null;
        }
//        System.out.println("Connected");
        return connection;
    }





    public boolean logIn(){
        if (!createDatabase()) return false;
        isLoggedIn = true;
        createSaveGameTables();
//        System.out.println("Logged in");
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
                    ALTER SEQUENCE game_id_seq RESTART;
                    """;

            stmt.executeUpdate(truncSql);
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error while trying to drop all tables and sequences.");
        }
    }

    public boolean dropTable(){
        Statement stmt;
        String CreateSql;

        try {
            Connection connection = getConnection();
            stmt = connection.createStatement();

            CreateSql = """
                    drop table if exists int_score cascade;
                                    
                    drop table if exists int_move cascade;
                                    
                    drop table if exists int_tile cascade;
                                    
                    drop table if exists int_turn cascade;
                                    
                    drop table if exists int_playersession cascade;
                                    
                    drop table if exists int_gamesession cascade;
                                    
                    drop table if exists int_player cascade;
                                    
                    drop sequence if exists game_id_seq;
                    
                    drop sequence if exists player_id_seq;
                    drop sequence if exists playersession_id_seq;
                    """;
            stmt.executeUpdate(CreateSql);
            stmt.close();
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     * Creating all tables
     */
    public boolean createSaveGameTables() {
        boolean result = true;
        Statement stmt;
        String CreateSql;

        try {
            stmt = connection.createStatement();

            CreateSql = """					                        
                    CREATE SEQUENCE IF NOT EXISTS game_id_seq
                        START WITH 1
                        INCREMENT BY 1
                        NO MAXVALUE;
                    
                    
                    CREATE SEQUENCE IF NOT EXISTS player_id_seq
                        START WITH 1
                        INCREMENT BY 1
                        NO MAXVALUE;
                        
                    CREATE SEQUENCE IF NOT EXISTS playersession_id_seq
                        START WITH 1
                        INCREMENT BY 1
                        NO MAXVALUE;
                    
                    create table if not exists int_gamesession
                    (
                        game_id       integer   not null primary key,
                        game_duration integer   not null,
                        date_played   date not null default CURRENT_DATE,
                        start_time    timestamp not null,
                        end_time      timestamp not null default now()
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_player(
                                                             player_id INT PRIMARY KEY,
                                                             player_name VARCHAR(32) NOT NULL
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_playersession(
                        playersession_id INT NOT NULL UNIQUE ,
                        player_id INT REFERENCES INT_PLAYER ON DELETE CASCADE,
                        game_id INT REFERENCES INT_GAMESESSION ON DELETE CASCADE,
                        CONSTRAINT playersession_pkey PRIMARY KEY (playersession_id,player_id,game_id)
                    );
                    
                    
                    
                    CREATE TABLE IF NOT EXISTS int_tile(
                                                           tile_id INT PRIMARY KEY ,
                                                           shape VARCHAR(16),
                                                           color VARCHAR(16)
                    );
                    
                    
                    CREATE TABLE IF NOT EXISTS int_score(
                                                            playersession_id INT NOT NULL 
                                                                            REFERENCES int_playersession(playersession_id)
                                                                            ON DELETE CASCADE,
                                                            total_score INT,
                                                            tot_time_spent_turns numeric(3),
                                                            CONSTRAINT score_pkey PRIMARY KEY (playersession_id)
                    );
                    
                    CREATE TABLE IF NOT EXISTS int_turn(
                                                           playersession_id INT NOT NULL
                                                                            REFERENCES int_playersession(playersession_id)
                                                                            ON DELETE CASCADE,
                                                           turn_no INT NOT NULL ,
                                                           points INT NOT NULL,
                                                           time_spent float4 NOT NULL,
                                                           time_of_play TIMESTAMP NOT NULL,
                                                           CONSTRAINT turn_pkey PRIMARY KEY (playersession_id,turn_no)
                    );
                    
                    create table if not exists int_move
                      (
                          playersession_id integer not null
                                            REFERENCES int_playersession(playersession_id)
                                                ON DELETE CASCADE
                              ,
                          turn_no          integer not null,
                          move_no          integer not null,
                          row              integer ,
                          "column"           integer ,
                          tile_id          integer ,
                          constraint move_pk
                              primary key (playersession_id, move_no, turn_no),
                          constraint valid_player_turn
                              foreign key (playersession_id, turn_no) references int_turn
                                  on delete cascade,
                          constraint tile_fk
                              foreign key (tile_id) references int_tile
                      );
                    """;
            stmt.executeUpdate(CreateSql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while trying to create all tables");
            result = false;
        }
//        System.out.println("Created Tables");
        return result;
    }
    public boolean save(GameSession session) {
        session.save();
        for (PlayerSession sess: session.getPlayerSessions()) {
            //id is defined here for playerSession
            sess.save();
        }
        try {
            closeConnection();
        }catch (SQLException e) {
            System.out.println("NOOOOOO");
            e.printStackTrace();
            return false;
        }
        System.out.println("Saved Everything");
        return true;
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
