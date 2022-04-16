package qwirkle.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import qwirkle.model.GameSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private String password = "Student_1234";
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





    //function to make a directory in root directory
    public void makeDirectory(){
        try {
            Path path = Paths.get("/home/Documents/DB_AUTH/");
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Path path2 = Paths.get("/home/Documents/DB_AUTH/credentials.txt");
            if (!Files.exists(path2)) {
                Files.createFile(path2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     Creates the database in case it doesn't exist
     * @return if database successfully created
     */
    public boolean createDatabase()  {
        ResultSet rs = null;
        Statement statement = null;
        try {
            this.connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", username, password);
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT FROM pg_database WHERE LOWER(datname) = 'qwirkledb';");
            if (!rs.next()) {
                statement.executeUpdate("CREATE DATABASE qwirkledb;");
            }
        }catch (SQLException e) {
            return false;
        }
        finally {
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
     Sets up postgres server connection
     @return <code>Connection</code>
     */
    public Connection setConnection() {
        try  {
            connection = DriverManager.getConnection(jdbc, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
//        System.out.println("Connected");
        return connection;
    }


    public boolean logIn(){
        if (!createDatabase()) return false;
        createSaveGameTables();
//        makeDirectory();
//        saveCredentials();
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
                    """;

            stmt.executeUpdate(truncSql);

        } catch (SQLException e) {
            System.out.println("Error while trying to drop all tables and sequences.");
        }finally {
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
        }
    }



    public boolean dropTable(){
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

        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }finally {
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
        }finally {
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


    public ObservableList<PieChart.Data> getTileByShapeChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        this.connection = setConnection();
        try {
            stmt = connection.createStatement();
            String sql = """
                         SELECT COUNT(*) AS count, shape
                         FROM int_move
                         JOIN int_tile it on it.tile_id = int_move.tile_id
                         JOIN int_playersession p on p.playersession_id = int_move.playersession_id
                         JOIN int_player ph on ph.player_id = p.player_id
                         WHERE player_name not in ('Computer')
                         group by shape
                         """;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                pieChartData.add(new PieChart.Data(rs.getString("shape"), rs.getInt("count")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }

        }
        return pieChartData;
    }



    public ObservableList<PieChart.Data> getTileByColorChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Statement stmt = null;
        ResultSet rs = null;
        this.connection = setConnection();
        try {
            stmt = connection.createStatement();
            String sql = """
                         SELECT COUNT(*) AS count, color
                         FROM int_move
                         JOIN int_tile it on it.tile_id = int_move.tile_id
                         JOIN int_playersession p on p.playersession_id = int_move.playersession_id
                         JOIN int_player ph on ph.player_id = p.player_id
                         WHERE player_name not in ('Computer')
                         group by color
                         """;
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int count = rs.getRow() -1;
                pieChartData.add(new PieChart.Data(rs.getString("color"), rs.getInt("count")));
                String color = pieChartData.get(count).getName();
//                pieChartData.get(count).getNode().setStyle("-fx-pie-color: " + color + ";");
                //TODO handle exception
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (connection != null) {
                closeConnectionQuietly(connection);
            }
            if (stmt != null) {
                closeStatementQuietly(stmt);
            }
            if (rs != null) {
                closeResultSetQuietly(rs);
            }
        }
        return pieChartData;
    }


    public ObservableList<XYChart.Data> getDurationPerTurnLastGameSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
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
                int gameId = rs.getInt("game_id");
                sql = """
                          SELECT turn_no, time_spent, player_name
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name in ('Computer')
                          AND game_id = ?
                          ORDER BY turn_no
                          """;
                ptsmt = connection.prepareStatement(sql);
                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while(rs.next()){
                    data.add(new XYChart.Data<>(rs.getInt("turn_no"), rs.getInt("time_spent")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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



    public ObservableList<XYChart.Data> getDurationPerTurnLastGameSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
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
                int gameId = rs.getInt("game_id");
                sql = """
                          SELECT turn_no, time_spent, player_name
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name not in ('Computer')
                          AND game_id = ?
                          ORDER BY turn_no
                          """;
                ptsmt = connection.prepareStatement(sql);
                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while(rs.next()){
                    data.add(new XYChart.Data<>(rs.getInt("turn_no"), rs.getInt("time_spent")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getPointsPerTurnLastGameSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
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
                int gameId = rs.getInt("game_id");
                sql = """
                          SELECT turn_no, points, player_name
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name in ('Computer')
                          AND game_id = ?
                          ORDER BY turn_no
                          """;
                ptsmt = connection.prepareStatement(sql);
                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while(rs.next()){
                    data.add(new XYChart.Data<>(rs.getInt("turn_no"), rs.getInt("points")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getPointsPerTurnLastGameSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
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
                int gameId = rs.getInt("game_id");
                sql = """
                          SELECT turn_no, points, player_name
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name not in ('Computer')
                          AND game_id = ?
                          ORDER BY turn_no
                          """;
                ptsmt = connection.prepareStatement(sql);
                ptsmt.setInt(1, gameId);
                rs = ptsmt.executeQuery();
                while(rs.next()){
                    data.add(new XYChart.Data<>(rs.getInt("turn_no"), rs.getInt("points")));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getBestPointsPerSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                          SELECT max(points) as points, game_id
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name in ('Computer')
                          group by game_id
                          order by game_id
                          """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while(rs.next()){
                data.add(new XYChart.Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getBestPointsPerSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                          SELECT max(points) as points, game_id
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name not in ('Computer')
                          group by game_id
                          order by game_id
                          """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while(rs.next()){
                data.add(new XYChart.Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getAvgPointsPerSessionComputer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                          SELECT avg(points) as points, game_id
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name in ('Computer')
                          group by game_id
                          order by game_id
                          """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while(rs.next()){
                data.add(new XYChart.Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getAvgPointsPerSessionPlayer() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                          SELECT avg(points) as points, game_id
                          FROM int_turn
                          JOIN int_playersession ip on int_turn.playersession_id = ip.playersession_id
                          JOIN int_player i on i.player_id = ip.player_id
                          WHERE player_name not in ('Computer')
                          group by game_id
                          order by game_id
                          """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while(rs.next()){
                data.add(new XYChart.Data<>(rs.getInt("game_id"), rs.getInt("points")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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

    public ObservableList<XYChart.Data> getDurationPerSession() {
        this.connection = setConnection();
        PreparedStatement ptsmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
        try {
            String sql = """
                          SELECT game_duration as duration, game_id
                          FROM int_gamesession
                          order by game_id
                          """;
            ptsmt = connection.prepareStatement(sql);
            rs = ptsmt.executeQuery();
            while(rs.next()){
                data.add(new XYChart.Data<>(rs.getInt("game_id"), rs.getInt("duration")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
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
        }catch (SQLException ignored) {}
    }

    private void closePreparedStatementQuietly(PreparedStatement ptsmt) {
        try {
            ptsmt.close();
        } catch (SQLException ignored) {
        }
    }



}