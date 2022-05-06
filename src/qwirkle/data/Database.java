package qwirkle.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import qwirkle.model.GameSession;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

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
    private String username;
    private String password;
    private Connection connection;


    private Database() {
    }

    public static Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }


    public void setUsername(String text) {
        if (getSavedCredentials() != null && getSavedCredentials().size() > 0) {
            username = getSavedCredentials().getFirst();
        } else {
            username = text;
        }
    }


    public void setPassword(String text) {
        if (getSavedCredentials() != null && getSavedCredentials().size() > 0) {
            password = getSavedCredentials().getLast();
        } else {
            password = text;
        }
    }


    public String getUserName() {
        if (getSavedCredentials() != null && getSavedCredentials().size() > 0) {
            username = getSavedCredentials().getFirst();
        }
        return username;
    }

    public String getPassword() {
        if (getSavedCredentials() != null && getSavedCredentials().size() > 0) {
            password = getSavedCredentials().getLast();
        }
        return password;
    }

    //function to make a read credentials from a file

    public LinkedList<String> getSavedCredentials() {
        LinkedList<String> credentials = new LinkedList<>();
        try {
            Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/user-data/info.txt"))));
            String savedFilePath = scanner.nextLine();
            Scanner scanner1 = new Scanner(new File(savedFilePath));
            for (int i = 0; i < 2; i ++) {
                String text = scanner1.nextLine().substring(10);
                credentials.add(text);
            }
            scanner.close();
            scanner1.close();
        } catch (FileNotFoundException ignored) {
        }
        if (credentials.size() > 1) {
            credentials.subList(0, 2);
            return credentials;
        }
        return null;
    }


    /**
     * Creates the database in case it doesn't exist
     *
     * @return if database successfully created
     */
    public boolean createDatabase() {
        ResultSet rs = null;
        Statement statement = null;
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", getUserName(), getPassword());
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT FROM pg_database WHERE LOWER(datname) = 'qwirkledb';");
            if (!rs.next()) {
                statement.executeUpdate("CREATE DATABASE qwirkledb;");
            }
        } catch (SQLException e) {
            return false;
        } finally {
            if (statement != null) {
                closeStatementQuietly(statement);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
        }
        return true;
    }

    public Connection getConnection() {
        return connection;
    }


    /**
     * Sets up postgres server connection
     *
     * @return <code>Connection</code>
     */
    public Connection setConnection() {
        try {
            connection = DriverManager.getConnection(jdbc, getUserName(), getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return connection;
    }


    public boolean logIn() {
        if (!createDatabase()) return false;
        createSaveGameTables();
        return true;
    }


    /**
     * Truncates all data in all tables and restarts identity columns and sequences
     */
    public void deleteData() {
        Statement stmt = null;
        this.connection = setConnection();
        try {
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
                    ALTER SEQUENCE playersession_id_seq RESTART;
                    """;

            stmt.executeUpdate(truncSql);

        } catch (SQLException e) {
            System.out.println("Error while trying to drop all tables and sequences.");
        } finally {
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
        }
    }


    public boolean dropTable() {
        Statement stmt = null;
        String CreateSql;
        this.connection = setConnection();

        try {
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

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
        }
        return true;
    }

    /**
     * Creating all tables
     */
    public boolean createSaveGameTables() {
        Statement stmt = null;
        String CreateSql;
        this.connection = setConnection();
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
                                                             player_name VARCHAR(32) NOT NULL,
                                                             difficulty VARCHAR DEFAULT NULL
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
                                                            tot_time_spent_turns INT,
                                                            CONSTRAINT score_pkey PRIMARY KEY (playersession_id)
                    );
                                        
                    CREATE TABLE IF NOT EXISTS int_turn(
                                                           playersession_id INT NOT NULL
                                                                            REFERENCES int_playersession(playersession_id)
                                                                            ON DELETE CASCADE,
                                                           turn_no INT NOT NULL ,
                                                           points INT NOT NULL,
                                                           time_spent INT NOT NULL,
                                                           time_of_play TIMESTAMP DEFAULT now(),
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
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while trying to create all tables");
            return false;
        } finally {
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
        }
        return true;
    }


    public boolean save(GameSession session) {
        this.connection = setConnection();
        session.save();
        session.getPlayerSession().save();
        session.getComputerSession().save();
        return true;
    }


    public ObservableList<Data> getDurationPerTurnLastGameSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT game_id
                    FROM int_gamesession
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                sql = """
                        SELECT turn_no, time_spent, player_name
                        FROM int_turn
                        JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                        JOIN int_player i on i.player_id = ip.player_id
                        WHERE player_name in ('Computer')
                        AND game_id = (SELECT max(game_id) from int_gamesession)
                        ORDER BY turn_no
                        """;
                ptsmt = connection.prepareStatement(sql);
//                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while (rs.next()) {
                    data.add(new Data<>(rs.getInt("turn_no"), rs.getInt("time_spent")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }


    public ObservableList<Data> getDurationPerTurnLastGameSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT game_id
                    FROM int_gamesession
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                sql = """
                        SELECT turn_no, time_spent, player_name
                        FROM int_turn
                        JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                        JOIN int_player i on i.player_id = ip.player_id
                        WHERE player_name not in ('Computer')
                        AND game_id = (SELECT max(game_id) from int_gamesession)
                        ORDER BY turn_no
                        """;
                ptsmt = connection.prepareStatement(sql);
//                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while (rs.next()) {
                    data.add(new Data<>(rs.getInt("turn_no"), rs.getInt("time_spent")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }

    public ObservableList<Data> getPointsPerTurnLastGameSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT game_id
                    FROM int_gamesession
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
//                int gameId = rs.getInt("game_id");
                sql = """
                        SELECT turn_no, points, player_name
                        FROM int_turn
                        JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                        JOIN int_player i on i.player_id = ip.player_id
                        WHERE player_name in ('Computer')
                        AND game_id = (SELECT max(game_id) from int_gamesession)
                        ORDER BY turn_no
                        """;
                ptsmt = connection.prepareStatement(sql);
//                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while (rs.next()) {
                    data.add(new Data<>(rs.getInt("turn_no"), rs.getInt("points")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }

    public ObservableList<Data> getPointsPerTurnLastGameSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT game_id
                    FROM int_gamesession
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
//                int gameId = rs.getInt("game_id");
                sql = """
                        SELECT turn_no, points, player_name
                        FROM int_turn
                        JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                        JOIN int_player i on i.player_id = ip.player_id
                        WHERE player_name not in ('Computer')
                        AND game_id = (SELECT max(game_id) from int_gamesession)
                        ORDER BY turn_no
                        """;
                ptsmt = connection.prepareStatement(sql);
//                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while (rs.next()) {
                    data.add(new Data<>(rs.getInt("turn_no"), rs.getInt("points")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }

    public ObservableList<Data> getBestPointsPerSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT max(points) as points, game_id
                    FROM int_turn
                    JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                    JOIN int_player i on i.player_id = ip.player_id
                    WHERE player_name in ('Computer')
                    group by game_id
                    order by game_id DESC
                    LIMIT 50
                    """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while (rs.next()) {
                data.add(new Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        Collections.reverse(data);
        return data;
    }

    public ObservableList<Data> getBestPointsPerSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT max(points) as points, game_id
                    FROM int_turn
                    JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                    JOIN int_player i on i.player_id = ip.player_id
                    WHERE player_name not in ('Computer')
                    group by game_id
                    order by game_id DESC
                    LIMIT 50
                    """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while (rs.next()) {
                data.add(new Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        Collections.reverse(data);
        return data;
    }

    public ObservableList<Data> getAvgPointsPerSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT avg(points) as points, game_id
                    FROM int_turn
                    JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                    JOIN int_player i on i.player_id = ip.player_id
                    WHERE player_name in ('Computer')
                    group by game_id
                    order by game_id DESC
                    LIMIT 50
                    """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while (rs.next()) {
                data.add(new Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        Collections.reverse(data);
        return data;
    }

    public ObservableList<Data> getAvgPointsPerSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT avg(points) as points, game_id
                    FROM int_turn
                    JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                    JOIN int_player i on i.player_id = ip.player_id
                    WHERE player_name not in ('Computer')
                    group by game_id
                    order by game_id DESC
                    LIMIT 50
                    """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while (rs.next()) {
                data.add(new Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        Collections.reverse(data);
        return data;
    }

    public ObservableList<Data> getDurationPerSession() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                    SELECT game_duration as duration, game_id
                    FROM int_gamesession
                    order by game_id DESC
                    LIMIT 50
                    """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while (rs.next()) {
                data.add(new Data<>(rs.getInt("game_id"), rs.getInt("duration")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        Collections.reverse(data);
        return data;
    }


    private void closeResultSetQuietly(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException ignored) {
        }
    }

    private void closeConnectionQuietly(Connection connection) {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    private void closeStatementQuietly(Statement stmt) {
        try {
            stmt.close();
        } catch (SQLException ignored) {
        }
    }

    private void closePreparedStatementQuietly(PreparedStatement ptsmt) {
        try {
            ptsmt.close();
        } catch (SQLException ignored) {
        }
    }


    public String getLastPlayerName() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        String data = null;
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT player_name
                    FROM int_gamesession
                    JOIN int_playersession USING (game_id)
                    JOIN int_player USING (player_id)
                    WHERE player_name not in ('Computer')
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("player_name");
            }
            return "Player";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }

    public String getLastComputerMode() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        String data = null;
        try {
            stmt = connection.createStatement();
            String sql = """
                    SELECT difficulty
                    FROM int_gamesession
                    JOIN int_playersession USING (game_id)
                    JOIN int_player USING (player_id)
                    WHERE player_name in ('Computer')
                    ORDER BY game_id DESC
                    LIMIT 1
                    """;
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("difficulty");
            }
            return "";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
            if (ptsmt != null) {
                closePreparedStatementQuietly(ptsmt);
            }
        }
        return data;
    }
}
