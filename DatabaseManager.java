package src;

import java.sql.*;
import java.util.*;
import java.util.Vector;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:user_credentials.db";

    // Helper: always enable foreign keys for each connection
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static class BookingRecord {
        public final int bookingId;
        public final String username;
        public final String movieTitle;
        public final String showTime;
        public final String seatId;

        public BookingRecord(int bookingId, String username, String movieTitle, String showTime, String seatId) {
            this.bookingId = bookingId;
            this.username = username;
            this.movieTitle = movieTitle;
            this.showTime = showTime;
            this.seatId = seatId;
        }

        // legacy constructor omitted (we always set username now)
    }

    public static class Seat {
        final String id;
        final String category;
        final int price;
        boolean isSelected = false;

        public Seat(String id, String category, int price) {
            this.id = id;
            this.category = category;
            this.price = price;
        }
    }

    private static void createPredefinedAdmins() {
        Map<String, String> adminAccounts = new HashMap<>();
        adminAccounts.put("shriram", "0981234");
        adminAccounts.put("admin", "admin123");
        adminAccounts.put("manager", "pass567");
        adminAccounts.put("root", "rootpass");
        adminAccounts.put("sysadmin", "sys123");
        adminAccounts.put("supervisor", "super");
        adminAccounts.put("director", "directorpass");
        adminAccounts.put("support", "support1");
        adminAccounts.put("network", "netpass");
        adminAccounts.put("master", "masterkey");

        String checkSql = "SELECT COUNT(*) AS count FROM admins WHERE admin_username = ?";
        String insertSql = "INSERT INTO admins(admin_username, admin_password) VALUES(?, ?)";

        try (Connection conn = getConnection()) {
            for (Map.Entry<String, String> entry : adminAccounts.entrySet()) {
                String username = entry.getKey();
                String password = entry.getValue();

                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, username);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next() && rs.getInt("count") == 0) {
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setString(1, username);
                            insertStmt.setString(2, password);
                            insertStmt.executeUpdate();
                            System.out.println("Created predefined admin account: " + username);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking/creating predefined admins: " + e.getMessage());
        }
    }

    public static void initializeDatabase() {
        String usersTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "username TEXT PRIMARY KEY NOT NULL, "
                + "password TEXT NOT NULL"
                + ");";

        String adminsTableSql = "CREATE TABLE IF NOT EXISTS admins ("
                + "admin_username TEXT PRIMARY KEY NOT NULL, "
                + "admin_password TEXT NOT NULL"
                + ");";

        String moviesTableSql = "CREATE TABLE IF NOT EXISTS movies ("
                + "movie_title TEXT PRIMARY KEY NOT NULL"
                + ");";

        String timesTableSql = "CREATE TABLE IF NOT EXISTS showtimes ("
                + "show_time TEXT PRIMARY KEY NOT NULL"
                + ");";

        String bookingsTableSql = "CREATE TABLE IF NOT EXISTS bookings ("
                + "booking_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "movie_title TEXT NOT NULL,"
                + "show_time TEXT NOT NULL,"
                + "seat_id TEXT NOT NULL,"
                + "price INTEGER NOT NULL,"
                + "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (username) REFERENCES users(username),"
                + "FOREIGN KEY (movie_title) REFERENCES movies(movie_title) ON DELETE CASCADE,"
                + "FOREIGN KEY (show_time) REFERENCES showtimes(show_time) ON DELETE CASCADE"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTableSql);
            stmt.execute(adminsTableSql);
            stmt.execute(moviesTableSql);
            stmt.execute(timesTableSql);
            stmt.execute(bookingsTableSql);

            System.out.println("Database and tables are ready.");

            createPredefinedAdmins();

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static Vector<String> loadMovies() {
        Vector<String> movies = new Vector<>();
        String sql = "SELECT movie_title FROM movies ORDER BY movie_title";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(rs.getString("movie_title"));
            }
            if (movies.isEmpty()) {
                System.out.println("No movies found, adding defaults.");
                addMovie("Avengers: Endgame");
                addMovie("Leo");
                addMovie("Jawan");
                addMovie("Oppenheimer");
                movies.addAll(List.of("Avengers: Endgame", "Leo", "Jawan", "Oppenheimer"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading movies: " + e.getMessage());
        }
        return movies;
    }

    public static Vector<String> loadShowtimes() {
        Vector<String> times = new Vector<>();
        String sql = "SELECT show_time FROM showtimes ORDER BY show_time";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                times.add(rs.getString("show_time"));
            }
            if (times.isEmpty()) {
                System.out.println("No showtimes found, adding defaults.");
                addShowtime("10:00 AM");
                addShowtime("1:00 PM");
                addShowtime("4:00 PM");
                addShowtime("7:00 PM");
                times.addAll(List.of("10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading showtimes: " + e.getMessage());
        }
        return times;
    }

    public static boolean addMovie(String movieTitle) {
        if (movieTitle == null) return false;
        String sql = "INSERT INTO movies(movie_title) VALUES(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle.trim());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeMovie(String movieTitle) {
        String sql = "DELETE FROM movies WHERE movie_title = ?";
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, movieTitle);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error removing movie: " + e.getMessage());
            return false;
        }
    }

    public static boolean addShowtime(String showTime) {
        if (showTime == null) return false;
        String sql = "INSERT INTO showtimes(show_time) VALUES(?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, showTime.trim());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding showtime: " + e.getMessage());
            return false;
        }
    }

    public static boolean removeShowtime(String showTime) {
        String sql = "DELETE FROM showtimes WHERE show_time = ?";
        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, showTime);
                pstmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error removing showtime: " + e.getMessage());
            return false;
        }
    }

    public static boolean addUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public static String validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("password").equals(password)) {
                    return "USER";
                }
            }
            return "INVALID";
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return "INVALID";
        }
    }

    public static String validateAdmin(String username, String password) {
        String sql = "SELECT admin_password FROM admins WHERE admin_username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("admin_password").equals(password)) {
                    return "ADMIN";
                }
            }
            return "INVALID";
        } catch (SQLException e) {
            System.err.println("Error validating admin: " + e.getMessage());
            return "INVALID";
        }
    }

    public static void addBooking(String username, String movie, String time, List<Seat> seats) {
        String sql = "INSERT INTO bookings(username, movie_title, show_time, seat_id, price) VALUES(?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Seat seat : seats) {
                pstmt.setString(1, username);
                pstmt.setString(2, movie);
                pstmt.setString(3, time);
                pstmt.setString(4, seat.id);
                pstmt.setInt(5, seat.price);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error adding booking: " + e.getMessage());
        }
    }

    public static Set<String> getBookedSeats(String movie, String time) {
        Set<String> bookedSeats = new HashSet<>();
        String sql = "SELECT seat_id FROM bookings WHERE movie_title = ? AND show_time = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getString("seat_id"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booked seats: " + e.getMessage());
        }
        return bookedSeats;
    }

    public static List<BookingRecord> getBookingsForUser(String username) {
        List<BookingRecord> records = new ArrayList<>();
        String sql = "SELECT booking_id, movie_title, show_time, seat_id FROM bookings "
                + "WHERE username = ? ORDER BY booking_date DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new BookingRecord(
                        rs.getInt("booking_id"),
                        username,
                        rs.getString("movie_title"),
                        rs.getString("show_time"),
                        rs.getString("seat_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user bookings: " + e.getMessage());
        }
        return records;
    }

    public static List<BookingRecord> getAllBookings() {
        List<BookingRecord> records = new ArrayList<>();
        String sql = "SELECT booking_id, username, movie_title, show_time, seat_id FROM bookings "
                + "ORDER BY booking_date DESC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                records.add(new BookingRecord(
                        rs.getInt("booking_id"),
                        rs.getString("username"),
                        rs.getString("movie_title"),
                        rs.getString("show_time"),
                        rs.getString("seat_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all bookings: " + e.getMessage());
        }
        return records;
    }

    public static boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
            return false;
        }
    }

    public static List<String> getAllUsernames() {
        List<String> users = new ArrayList<>();
        String sql = "SELECT username FROM users ORDER BY username";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all usernames: " + e.getMessage());
        }
        return users;
    }

    public static boolean deleteUser(String username) {
        String deleteBookingsSql = "DELETE FROM bookings WHERE username = ?";
        String deleteUserSql = "DELETE FROM users WHERE username = ?";
        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtBookings = conn.prepareStatement(deleteBookingsSql)) {
                pstmtBookings.setString(1, username);
                pstmtBookings.executeUpdate();
            }
            try (PreparedStatement pstmtUser = conn.prepareStatement(deleteUserSql)) {
                pstmtUser.setString(1, username);
                int rowsAffected = pstmtUser.executeUpdate();

                conn.commit();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public static int cancelShow(String movie, String time) {
        String sql = "DELETE FROM bookings WHERE movie_title = ? AND show_time = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movie);
            pstmt.setString(2, time);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error canceling show: " + e.getMessage());
            return 0;
        }
    }

    public static int cancelAllBookingsForMovie(String movieTitle) {
        String sql = "DELETE FROM bookings WHERE movie_title = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error canceling all bookings for movie: " + e.getMessage());
            return 0;
        }
    }

    public static int cancelAllBookingsForTime(String showTime) {
        String sql = "DELETE FROM bookings WHERE show_time = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, showTime);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error canceling all bookings for time: " + e.getMessage());
            return 0;
        }
    }

    // Existence checks to avoid duplicate popups/races
    public static boolean showtimeExists(String showTime) {
        String sql = "SELECT 1 FROM showtimes WHERE show_time = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, showTime.trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking showtime existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean movieExists(String movieTitle) {
        String sql = "SELECT 1 FROM movies WHERE movie_title = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle.trim());
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking movie existence: " + e.getMessage());
            return false;
        }
    }
}
