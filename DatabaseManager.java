import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:user_credentials.db";

    // --- THIS CLASS WAS MISSING ---
    public static class BookingRecord {
        public final String username;
        public final String showTime;
        public final String seatId;

        public BookingRecord(String username, String showTime, String seatId) {
            this.username = username;
            this.showTime = showTime;
            this.seatId = seatId;
        }
    }
    // -----------------------------

    public static void initializeDatabase() {
        String usersTableSql = "CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY NOT NULL, password TEXT NOT NULL);";
        String bookingsTableSql = "CREATE TABLE IF NOT EXISTS bookings ("
                + "booking_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "username TEXT NOT NULL,"
                + "movie_title TEXT NOT NULL,"
                + "show_time TEXT NOT NULL,"
                + "seat_id TEXT NOT NULL,"
                + "price INTEGER NOT NULL,"
                + "booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY (username) REFERENCES users(username));";

        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTableSql);
            stmt.execute(bookingsTableSql);
            System.out.println("Database and tables are ready.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static boolean addUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    public static boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("password").equals(password);
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

    public static void addBooking(String username, String movie, String time, List<BookingPage.Seat> seats) {
        String sql = "INSERT INTO bookings(username, movie_title, show_time, seat_id, price) VALUES(?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (BookingPage.Seat seat : seats) {
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
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    public static List<BookingRecord> getBookingsForMovie(String movieTitle) {
        List<BookingRecord> records = new ArrayList<>();
        String sql = "SELECT username, show_time, seat_id FROM bookings WHERE movie_title = ? ORDER BY show_time, username, seat_id";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, movieTitle);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(new BookingRecord(
                        rs.getString("username"),
                        rs.getString("show_time"),
                        rs.getString("seat_id")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching booking records: " + e.getMessage());
        }
        return records;
    }
}
