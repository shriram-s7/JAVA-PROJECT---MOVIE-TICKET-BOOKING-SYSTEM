package src;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;
import java.util.Set;
public class MovieTicketBookingSystem {
    static JFrame frame;
    static CardLayout cardLayout;
    static JPanel mainPanel;
    static String currentUser = "";
    static JTextField userField;
    static JPasswordField passField;
    static Vector<String> movieModel;
    static Vector<String> timeModel;
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        movieModel = DatabaseManager.loadMovies();
        timeModel = DatabaseManager.loadShowtimes();
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("BookMyShow Deluxe");
            frame.setSize(1400, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            cardLayout = new CardLayout();
            mainPanel = new JPanel(cardLayout);
            JPanel userLoginPanel = createUserLoginPage();
            JPanel adminLoginPanel = createAdminLoginPage();
            mainPanel.add(userLoginPanel, "UserLogin");
            mainPanel.add(adminLoginPanel, "AdminLogin");
            frame.add(mainPanel);
            cardLayout.show(mainPanel, "UserLogin");
            frame.setVisible(true);
        });
    }
    static JPanel createUserLoginPage() {
        // (unchanged) copy your existing user login implementation here
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(20, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel title = new JLabel("BookMyShow Deluxe");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        loginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setForeground(Color.WHITE);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginPanel.add(userLbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        userField = new JTextField(15);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginPanel.add(userField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setForeground(Color.WHITE);
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginPanel.add(passLbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passField = new JPasswordField(15);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginPanel.add(passField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn);
        loginBtn.setPreferredSize(new Dimension(100, 35));
        JButton signupBtn = new JButton("Sign Up");
        styleButton(signupBtn);
        signupBtn.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(loginBtn);
        buttonPanel.add(signupBtn);
        loginPanel.add(buttonPanel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton adminPortalButton = new JButton("Admin Portal Login");
        adminPortalButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adminPortalButton.setForeground(new Color(100, 150, 255));
        adminPortalButton.setOpaque(false);
        adminPortalButton.setContentAreaFilled(false);
        adminPortalButton.setBorderPainted(false);
        adminPortalButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        adminPortalButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "AdminLogin");
        });
        loginPanel.add(adminPortalButton, gbc);

        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String role = DatabaseManager.validateUser(user, pass);

            if (role.equals("USER")) {
                currentUser = user;
                openMainBookingPage();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both a username and password to sign up.");
                return;
            }
            showConfirmPasswordDialog(user, pass);
        });

        return loginPanel;
    }

    static JPanel createAdminLoginPage() {
        JPanel adminLoginPanel = new JPanel(new GridBagLayout());
        adminLoginPanel.setBackground(new Color(30, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel title = new JLabel("Admin Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        adminLoginPanel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel userLbl = new JLabel("Admin Username:");
        userLbl.setForeground(Color.WHITE);
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adminLoginPanel.add(userLbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JTextField adminUserField = new JTextField(15);
        adminUserField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adminLoginPanel.add(adminUserField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        JLabel passLbl = new JLabel("Admin Password:");
        passLbl.setForeground(Color.WHITE);
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adminLoginPanel.add(passLbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        JPasswordField adminPassField = new JPasswordField(15);
        adminPassField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        adminLoginPanel.add(adminPassField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton adminLoginBtn = new JButton("Login");
        styleButton(adminLoginBtn);
        adminLoginBtn.setPreferredSize(new Dimension(100, 35));
        adminLoginPanel.add(adminLoginBtn, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        JButton returnButton = new JButton("Return to User Login");
        returnButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        returnButton.setForeground(new Color(150, 150, 150));
        returnButton.setOpaque(false);
        returnButton.setContentAreaFilled(false);
        returnButton.setBorderPainted(false);
        returnButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        returnButton.addActionListener(e -> {
            cardLayout.show(mainPanel, "UserLogin");
        });
        adminLoginPanel.add(returnButton, gbc);

        adminLoginBtn.addActionListener(e -> {
            String user = adminUserField.getText();
            String pass = new String(adminPassField.getPassword());
            String role = DatabaseManager.validateAdmin(user, pass);

            if (role.equals("ADMIN")) {
                currentUser = user;
                openAdminPage();
                adminUserField.setText("");
                adminPassField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Admin credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        return adminLoginPanel;
    }

    static void showConfirmPasswordDialog(String username, String password) {
        JDialog dialog = new JDialog(frame, "Confirm Password", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(30, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel infoLabel = new JLabel("Please re-type your password to confirm:");
        infoLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(infoLabel, gbc);

        JPasswordField confirmPassField = new JPasswordField(15);
        confirmPassField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy++;
        dialog.add(confirmPassField, gbc);

        JButton confirmBtn = new JButton("Confirm Sign Up");
        styleButton(confirmBtn);
        gbc.gridy++;
        dialog.add(confirmBtn, gbc);

        confirmBtn.addActionListener(e -> {
            String confirmPass = new String(confirmPassField.getPassword());
            if (password.equals(confirmPass)) {
                if (DatabaseManager.addUser(username, password)) {
                    JOptionPane.showMessageDialog(dialog, "✅ Account created! You can now log in.");
                    userField.setText("");
                    passField.setText("");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Username already exists. Please choose another one.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Passwords do not match. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.setVisible(true);
    }

    static void openMainBookingPage() {
        BookingPage booking = new BookingPage(frame, mainPanel, cardLayout, currentUser, movieModel, timeModel);
        mainPanel.add(booking, "Booking");
        cardLayout.show(mainPanel, "Booking");
    }

    static void openAdminPage() {
        AdminPage adminPanel = new AdminPage(frame, mainPanel, cardLayout, currentUser, movieModel, timeModel);
        mainPanel.add(adminPanel, "Admin");
        cardLayout.show(mainPanel, "Admin");
    }

    static void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 180, 90));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}

/* ----------------
   BookingPage class
   ---------------- */
class BookingPage extends JPanel {
    private final JFrame frame;
    private final String currentUser;
    private final JComboBox<String> movieBox;
    private final JComboBox<String> timeBox;
    private final JLabel totalLabel;
    private final JPanel seatPanel;

    private final Map<String, DatabaseManager.Seat> allSeats = new HashMap<>();
    private final List<DatabaseManager.Seat> selectedSeats = new ArrayList<>();
    private int total = 0;

    BookingPage(JFrame frame, JPanel parent, CardLayout layout, String user, Vector<String> movieModel, Vector<String> timeModel) {
        this.frame = frame;
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 15, 20));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Welcome, " + user + " | Book Your Show", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            MovieTicketBookingSystem.userField.setText("");
            MovieTicketBookingSystem.passField.setText("");
            layout.show(parent, "UserLogin");
        });

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 0, 0), 0, getHeight(), new Color(60, 0, 70));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        leftPanel.setOpaque(false);

        JLabel movieLbl = createStyledLabel("Movie");
        movieLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(movieLbl);
        leftPanel.add(Box.createVerticalStrut(10));

        movieBox = new JComboBox<>(movieModel);
        styleCombo(movieBox);
        movieBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(movieBox);
        leftPanel.add(Box.createVerticalStrut(35));

        JLabel timeLbl = createStyledLabel("Showtime");
        timeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(timeLbl);
        leftPanel.add(Box.createVerticalStrut(10));

        timeBox = new JComboBox<>(timeModel);
        styleCombo(timeBox);
        timeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(timeBox);

        ActionListener refreshListener = e -> refreshSeatLayout();
        movieBox.addActionListener(refreshListener);
        timeBox.addActionListener(refreshListener);
        add(leftPanel, BorderLayout.WEST);

        JPanel centerStagePanel = new JPanel(new BorderLayout());
        centerStagePanel.setBackground(new Color(15, 15, 20));
        JLabel screenLabel = new JLabel("◄ SCREEN THIS WAY ►", JLabel.CENTER);
        screenLabel.setForeground(Color.WHITE);
        screenLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        screenLabel.setOpaque(true);
        screenLabel.setBackground(new Color(60, 60, 70));
        screenLabel.setPreferredSize(new Dimension(0, 50));
        screenLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10))
        );
        centerStagePanel.add(screenLabel, BorderLayout.NORTH);

        seatPanel = new JPanel(new GridLayout(6, 10, 8, 8));
        seatPanel.setBackground(new Color(15, 15, 20));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerStagePanel.add(seatPanel, BorderLayout.CENTER);
        add(centerStagePanel, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel();
        summaryPanel.setBackground(new Color(30, 30, 40));
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setPreferredSize(new Dimension(250, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        totalLabel = new JLabel("Total: ₹0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(Color.WHITE);

        JButton confirmBtn = new JButton("Proceed to Payment");
        MovieTicketBookingSystem.styleButton(confirmBtn);
        confirmBtn.addActionListener(e -> proceedPayment());

        JButton viewBookingsBtn = new JButton("My Bookings");
        viewBookingsBtn.setBackground(new Color(0, 120, 215));
        viewBookingsBtn.setForeground(Color.WHITE);
        viewBookingsBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        viewBookingsBtn.setFocusPainted(false);
        viewBookingsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBookingsBtn.addActionListener(e -> showMyBookings());

        summaryPanel.add(createStyledLabel("Booking Summary"));
        summaryPanel.add(Box.createVerticalStrut(15));
        summaryPanel.add(totalLabel);
        summaryPanel.add(Box.createVerticalStrut(30));
        summaryPanel.add(confirmBtn);
        summaryPanel.add(Box.createVerticalStrut(15));
        summaryPanel.add(viewBookingsBtn);
        add(summaryPanel, BorderLayout.EAST);

        refreshSeatLayout();
    }

    void refreshSeatLayout() {
        seatPanel.removeAll();
        allSeats.clear();
        selectedSeats.clear();
        total = 0;
        updateTotal();

        if (movieBox.getItemCount() == 0 || timeBox.getItemCount() == 0) {
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        String selectedMovie = Objects.requireNonNull(movieBox.getSelectedItem()).toString();
        String selectedTime = Objects.requireNonNull(timeBox.getSelectedItem()).toString();
        Set<String> bookedSeats = DatabaseManager.getBookedSeats(selectedMovie, selectedTime);
        for (char r = 'A'; r <= 'F'; r++) {
            for (int c = 1; c <= 10; c++) {
                String id = "" + r + c;
                final int price = (r <= 'B') ? 300 : (r <= 'D' ? 200 : 120);
                final Color color = (r <= 'B') ? new Color(210, 4, 45) : (r <= 'D' ? new Color(255, 191, 0) : new Color(0, 150, 136));
                DatabaseManager.Seat seat = new DatabaseManager.Seat(id, "", price);
                allSeats.put(id, seat);
                JButton btn = new JButton(id);
                btn.setBackground(color);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                if (bookedSeats.contains(id)) {
                    btn.setBackground(Color.DARK_GRAY);
                    btn.setText("X");
                    btn.setEnabled(false);
                } else {
                    btn.addActionListener(e -> toggleSeatSelection(seat, btn, color));
                }
                seatPanel.add(btn);
            }
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void toggleSeatSelection(DatabaseManager.Seat seat, JButton btn, Color originalColor) {
        seat.isSelected = !seat.isSelected;
        if (seat.isSelected) {
            selectedSeats.add(seat);
            total += seat.price;
            btn.setBackground(Color.GREEN);
        } else {
            selectedSeats.remove(seat);
            total -= seat.price;
            btn.setBackground(originalColor);
        }
        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText("Total: ₹" + total);
    }

    void proceedPayment() {
        if (movieBox.getItemCount() == 0 || timeBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "There are no shows available to book.", "Booking Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select at least one seat!", "No Seats Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new PaymentDialog(frame, currentUser,
                Objects.requireNonNull(movieBox.getSelectedItem()).toString(),
                Objects.requireNonNull(timeBox.getSelectedItem()).toString(),
                total, selectedSeats, this::refreshSeatLayout);
    }

    void showMyBookings() {
        List<DatabaseManager.BookingRecord> records = DatabaseManager.getBookingsForUser(currentUser);
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "You have not made any bookings yet.",
                    "No Bookings Found",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new BookingsDialog(frame, "My Bookings", records, true, this::refreshSeatLayout);
        }
    }

    void styleCombo(JComboBox<String> box) {
        box.setBackground(new Color(40, 40, 50));
        box.setForeground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setFocusable(false);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                return lbl;
            }
        });
    }

    JLabel createStyledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        return l;
    }
}

/* ----------------
   AdminPage class (edited mostly around showtime UI)
   ---------------- */
class AdminPage extends JPanel {
    private final JFrame frame;
    private final String currentUser;
    private final JComboBox<String> movieBox;
    private final JComboBox<String> timeBox;
    private final JPanel seatPanel;
    private JTable allBookingsTable;
    private final List<DatabaseManager.BookingRecord> allBookings = new ArrayList<>();

    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    private final Vector<String> movieModel;
    private final Vector<String> timeModel;
    private JList<String> movieJList;
    private JList<String> timeJList;

    AdminPage(JFrame frame, JPanel parent, CardLayout layout, String user, Vector<String> movieModel, Vector<String> timeModel) {
        this.frame = frame;
        this.currentUser = user;
        this.movieModel = movieModel;
        this.timeModel = timeModel;

        setLayout(new BorderLayout());
        setBackground(new Color(25, 35, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 20, 15));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Admin Dashboard | Welcome, " + user, JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            layout.show(parent, "AdminLogin");
        });

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // All Bookings tab (same as original)
        JPanel allBookingsPanel = new JPanel(new BorderLayout());
        allBookingsPanel.setBackground(new Color(25, 35, 25));
        allBookingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        allBookingsTable = new JTable();
        updateAllBookingsTable();
        JScrollPane scrollPane = new JScrollPane(allBookingsTable);
        allBookingsPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel allBookingsActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        allBookingsActionsPanel.setOpaque(false);
        JButton refreshBookingsBtn = new JButton("Refresh Bookings");
        MovieTicketBookingSystem.styleButton(refreshBookingsBtn);
        refreshBookingsBtn.addActionListener(e -> updateAllBookingsTable());
        allBookingsActionsPanel.add(refreshBookingsBtn);
        JButton cancelBookingBtn = new JButton("Cancel Selected Booking");
        cancelBookingBtn.setBackground(new Color(200, 50, 50));
        cancelBookingBtn.setForeground(Color.WHITE);
        cancelBookingBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cancelBookingBtn.addActionListener(e -> cancelSelectedBooking());
        allBookingsActionsPanel.add(cancelBookingBtn);
        allBookingsPanel.add(allBookingsActionsPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("All Bookings", allBookingsPanel);

        // Occupancy tab (same)
        JPanel occupancyPanel = new JPanel(new BorderLayout());
        occupancyPanel.setBackground(new Color(25, 35, 25));
        occupancyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel occupancyControls = new JPanel(new FlowLayout());
        occupancyControls.setOpaque(false);
        JLabel movieLbl = new JLabel("Movie:");
        movieLbl.setForeground(Color.WHITE);
        movieBox = new JComboBox<>(movieModel);
        JLabel timeLbl = new JLabel("Time:");
        timeLbl.setForeground(Color.WHITE);
        timeBox = new JComboBox<>(timeModel);
        JButton showSeatsBtn = new JButton("Show Occupancy");
        MovieTicketBookingSystem.styleButton(showSeatsBtn);
        showSeatsBtn.addActionListener(e -> refreshSeatOccupancy());
        occupancyControls.add(movieLbl);
        occupancyControls.add(movieBox);
        occupancyControls.add(timeLbl);
        occupancyControls.add(timeBox);
        occupancyControls.add(showSeatsBtn);
        occupancyPanel.add(occupancyControls, BorderLayout.NORTH);
        seatPanel = new JPanel(new GridLayout(6, 10, 8, 8));
        seatPanel.setBackground(new Color(15, 15, 20));
        seatPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        occupancyPanel.add(seatPanel, BorderLayout.CENTER);
        JButton cancelShowBtn = new JButton("Cancel This Entire Show");
        cancelShowBtn.setBackground(new Color(200, 50, 50));
        cancelShowBtn.setForeground(Color.WHITE);
        cancelShowBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cancelShowBtn.addActionListener(e -> cancelShow());
        occupancyPanel.add(cancelShowBtn, BorderLayout.SOUTH);
        tabbedPane.addTab("Seat Occupancy", occupancyPanel);

        // User management tab (same)
        JPanel userMgmtPanel = new JPanel(new BorderLayout());
        userMgmtPanel.setBackground(new Color(25, 35, 25));
        userMgmtPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userMgmtPanel.add(userScrollPane, BorderLayout.CENTER);
        JPanel userActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        userActionsPanel.setOpaque(false);
        JButton deleteUserBtn = new JButton("Delete Selected User");
        deleteUserBtn.setBackground(new Color(200, 50, 50));
        deleteUserBtn.setForeground(Color.WHITE);
        deleteUserBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        JButton refreshUsersBtn = new JButton("Refresh List");
        MovieTicketBookingSystem.styleButton(refreshUsersBtn);
        refreshUsersBtn.addActionListener(e -> refreshUserList());
        userActionsPanel.add(refreshUsersBtn);
        userActionsPanel.add(deleteUserBtn);
        userMgmtPanel.add(userActionsPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("User Management", userMgmtPanel);

        // Show management (this contains the changed time UI)
        JPanel showMgmtPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        showMgmtPanel.setBackground(new Color(25, 35, 25));
        showMgmtPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Movie management panel (left)
        JPanel moviePanel = new JPanel(new BorderLayout(5, 5));
        moviePanel.setOpaque(false);
        moviePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Manage Movies", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        movieJList = new JList<>(movieModel);
        movieJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        moviePanel.add(new JScrollPane(movieJList), BorderLayout.CENTER);

        JPanel movieAddPanel = new JPanel(new BorderLayout());
        movieAddPanel.setOpaque(false);
        JTextField movieField = new JTextField();
        JButton addMovieBtn = new JButton("Add");
        MovieTicketBookingSystem.styleButton(addMovieBtn);
        movieAddPanel.add(movieField, BorderLayout.CENTER);
        movieAddPanel.add(addMovieBtn, BorderLayout.EAST);
        moviePanel.add(movieAddPanel, BorderLayout.NORTH);

        JButton removeMovieBtn = new JButton("Remove Selected Movie");
        removeMovieBtn.setBackground(new Color(200, 50, 50));
        removeMovieBtn.setForeground(Color.WHITE);
        removeMovieBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        moviePanel.add(removeMovieBtn, BorderLayout.SOUTH);

        showMgmtPanel.add(moviePanel);

        // Time management panel (right) - replaced typing field with full dropdown
        JPanel timePanel = new JPanel(new BorderLayout(5, 5));
        timePanel.setOpaque(false);
        timePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Manage Showtimes", 0, 0, new Font("Segoe UI", Font.BOLD, 14), Color.WHITE));

        timeJList = new JList<>(timeModel);
        timeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timePanel.add(new JScrollPane(timeJList), BorderLayout.CENTER);

        JPanel timeAddPanel = new JPanel(new BorderLayout());
        timeAddPanel.setOpaque(false);

        // Build full time list: every 30 minutes from 6:00 AM to 11:30 PM
        java.util.List<String> allTimes = new ArrayList<>();
        // 6:00 AM ... 11:30 AM
        for (int h = 6; h <= 11; h++) {
            allTimes.add(String.format("%d:00 AM", h));
            allTimes.add(String.format("%d:30 AM", h));
        }
        // 12:00 PM
        allTimes.add("12:00 PM");
        allTimes.add("12:30 PM");
        // 1:00 PM .. 11:30 PM
        for (int h = 1; h <= 11; h++) {
            allTimes.add(String.format("%d:00 PM", h));
            allTimes.add(String.format("%d:30 PM", h));
        }

        JComboBox<String> timeField = new JComboBox<>(allTimes.toArray(new String[0]));
        timeField.setEditable(false);
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton addTimeBtn = new JButton("Add");
        MovieTicketBookingSystem.styleButton(addTimeBtn);
        timeAddPanel.add(timeField, BorderLayout.CENTER);
        timeAddPanel.add(addTimeBtn, BorderLayout.EAST);
        timePanel.add(timeAddPanel, BorderLayout.NORTH);

        JButton removeTimeBtn = new JButton("Remove Selected Time");
        removeTimeBtn.setBackground(new Color(200, 50, 50));
        removeTimeBtn.setForeground(Color.WHITE);
        removeTimeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timePanel.add(removeTimeBtn, BorderLayout.SOUTH);

        showMgmtPanel.add(timePanel);

        tabbedPane.addTab("Show Management", showMgmtPanel);

        // Attach action listeners (movie/time add/remove)
        // Ensure only single listener attached to each button
        for (ActionListener al : addMovieBtn.getActionListeners()) addMovieBtn.removeActionListener(al);
        addMovieBtn.addActionListener(e -> addMovie(movieField.getText(), movieField));

        for (ActionListener al : removeMovieBtn.getActionListeners()) removeMovieBtn.removeActionListener(al);
        removeMovieBtn.addActionListener(e -> removeMovie());

        // time add: remove previous listeners and attach guarded one
        for (ActionListener al : addTimeBtn.getActionListeners()) addTimeBtn.removeActionListener(al);
        addTimeBtn.addActionListener(e -> {
            addTimeBtn.setEnabled(false);
            try {
                Object sel = timeField.getSelectedItem();
                String selStr = sel == null ? "" : sel.toString();
                addTime(selStr, null);
            } finally {
                addTimeBtn.setEnabled(true);
            }
        });

        for (ActionListener al : removeTimeBtn.getActionListeners()) removeTimeBtn.removeActionListener(al);
        removeTimeBtn.addActionListener(e -> removeTime());

        add(tabbedPane, BorderLayout.CENTER);

        refreshSeatOccupancy();
        refreshUserList();
    }

    // ---- admin helper methods (cancelSelectedBooking, removeMovie, removeTime, deleteSelectedUser etc.)
    // I will include the modified addTime and addMovie implementations here:

    private void addMovie(String movieName, JTextField field) {
        if (movieName == null || movieName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Movie name cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String nameTrim = movieName.trim();

        // Prevent duplicates quickly (UI-level)
        if (movieModel.contains(nameTrim)) {
            JOptionPane.showMessageDialog(frame, "'" + nameTrim + "' already exists.", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (movieJList != null) movieJList.setSelectedValue(nameTrim, true);
            return;
        }

        // Check DB first to avoid double-insert race
        if (DatabaseManager.movieExists(nameTrim)) {
            JOptionPane.showMessageDialog(frame, "'" + nameTrim + "' already exists in DB.", "Info", JOptionPane.INFORMATION_MESSAGE);
            if (movieJList != null) {
                movieJList.setListData(movieModel.toArray(new String[0]));
                movieJList.setSelectedValue(nameTrim, true);
            }
            return;
        }

        // Try insert once
        if (DatabaseManager.addMovie(nameTrim)) {
            // update model & list view
            movieModel.addElement(nameTrim);
            if (movieJList != null) {
                movieJList.setListData(movieModel.toArray(new String[0]));
                movieJList.setSelectedValue(nameTrim, true);
                movieJList.revalidate();
                movieJList.repaint();
            }
            if (field != null) field.setText("");
            JOptionPane.showMessageDialog(frame, "'" + nameTrim + "' has been added.", "Movie Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, "Could not add movie (it may already exist).", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTime(String showTime, JTextField field) {
        if (showTime == null) {
            JOptionPane.showMessageDialog(frame, "Showtime cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String timeTrim = showTime.trim();
        if (timeTrim.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Showtime cannot be empty.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // if model already contains it, show one info and return
        if (timeModel.contains(timeTrim)) {
            if (timeJList != null) timeJList.setSelectedValue(timeTrim, true);
            JOptionPane.showMessageDialog(frame, "'" + timeTrim + "' already exists.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // check DB first (prevent race)
        if (DatabaseManager.showtimeExists(timeTrim)) {
            if (timeJList != null) timeJList.setSelectedValue(timeTrim, true);
            JOptionPane.showMessageDialog(frame, "'" + timeTrim + "' already exists.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // try to add once
        boolean added = DatabaseManager.addShowtime(timeTrim);
        if (!added) {
            JOptionPane.showMessageDialog(frame, "Could not add showtime (it may already exist).", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // success -> update model/list and show exactly ONE success message
        timeModel.addElement(timeTrim);
        if (timeJList != null) {
            timeJList.setListData(timeModel.toArray(new String[0]));
            timeJList.setSelectedValue(timeTrim, true);
            timeJList.revalidate();
            timeJList.repaint();
        }
        if (field != null) field.setText("e.g., 9:00 PM");
        JOptionPane.showMessageDialog(frame, "'" + timeTrim + "' has been added.", "Showtime Added", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- rest of admin methods (cancelSelectedBooking, removeMovie, removeTime, deleteSelectedUser, refreshUserList, cancelShow, updateAllBookingsTable, refreshSeatOccupancy)

    private void cancelSelectedBooking() {
        int selectedRow = allBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a booking from the table to cancel.", "No Booking Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String user = (String) allBookingsTable.getValueAt(selectedRow, 0);
        String seat = (String) allBookingsTable.getValueAt(selectedRow, 3);
        int bookingId = (int) allBookingsTable.getValueAt(selectedRow, 4);

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to cancel this booking?\n"
                        + "User: " + user + "\n"
                        + "Seat: " + seat + "\n"
                        + "Booking ID: " + bookingId,
                "Confirm Booking Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteBooking(bookingId)) {
                JOptionPane.showMessageDialog(frame, "Booking " + bookingId + " has been canceled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateAllBookingsTable();
                refreshSeatOccupancy();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not cancel booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeMovie() {
        String selectedMovie = movieJList.getSelectedValue();
        if (selectedMovie == null) {
            JOptionPane.showMessageDialog(frame, "Please select a movie to remove.", "No Movie Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to remove the movie '" + selectedMovie + "'?\n"
                        + "This will CANCEL ALL BOOKINGS for this movie.",
                "Confirm Movie Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.removeMovie(selectedMovie)) {
                movieModel.removeElement(selectedMovie);
                if (movieJList != null) {
                    movieJList.setListData(movieModel.toArray(new String[0]));
                }
                JOptionPane.showMessageDialog(frame,
                        "Movie '" + selectedMovie + "' and all associated bookings were removed.",
                        "Movie Removed", JOptionPane.INFORMATION_MESSAGE);
                updateAllBookingsTable();
                refreshSeatOccupancy();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not remove movie from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeTime() {
        String selectedTime = timeJList.getSelectedValue();
        if (selectedTime == null) {
            JOptionPane.showMessageDialog(frame, "Please select a showtime to remove.", "No Time Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to remove the showtime '" + selectedTime + "'?\n"
                        + "This will CANCEL ALL BOOKINGS for this time slot.",
                "Confirm Time Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.removeShowtime(selectedTime)) {
                timeModel.removeElement(selectedTime);
                if (timeJList != null) timeJList.setListData(timeModel.toArray(new String[0]));
                JOptionPane.showMessageDialog(frame,
                        "Showtime '" + selectedTime + "' and all associated bookings were removed.",
                        "Showtime Removed", JOptionPane.INFORMATION_MESSAGE);
                updateAllBookingsTable();
                refreshSeatOccupancy();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not remove showtime from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        String selectedUser = userList.getSelectedValue();
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(frame, "Please select a user from the list to delete.", "No User Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to PERMANENTLY DELETE user '" + selectedUser + "'?\n"
                        + "This will also delete all of their bookings.",
                "Confirm User Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteUser(selectedUser)) {
                JOptionPane.showMessageDialog(frame, "User '" + selectedUser + "' has been deleted.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                refreshUserList();
                updateAllBookingsTable();
            } else {
                JOptionPane.showMessageDialog(frame, "Could not delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshUserList() {
        userListModel.clear();
        List<String> users = DatabaseManager.getAllUsernames();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    private void cancelShow() {
        if (movieBox.getItemCount() == 0 || timeBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No movies or times available to cancel.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedMovie = Objects.requireNonNull(movieBox.getSelectedItem()).toString();
        String selectedTime = Objects.requireNonNull(timeBox.getSelectedItem()).toString();

        int confirm = JOptionPane.showConfirmDialog(frame,
                "Are you sure you want to CANCEL the entire show:\n"
                        + "Movie: " + selectedMovie + "\n"
                        + "Time: " + selectedTime + "\n\n"
                        + "This will delete ALL bookings for this show.",
                "Confirm Show Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int bookingsDeleted = DatabaseManager.cancelShow(selectedMovie, selectedTime);
            JOptionPane.showMessageDialog(frame,
                    bookingsDeleted + " bookings were canceled.\n"
                            + "The show is now empty.",
                    "Show Canceled",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshSeatOccupancy();
            updateAllBookingsTable();
        }
    }

    private void updateAllBookingsTable() {
        allBookings.clear();
        allBookings.addAll(DatabaseManager.getAllBookings());

        String[] columnNames = {"User", "Movie", "Showtime", "Seat ID", "Booking ID"};
        Object[][] data = new Object[allBookings.size()][5];

        for (int i = 0; i < allBookings.size(); i++) {
            DatabaseManager.BookingRecord record = allBookings.get(i);
            data[i][0] = record.username;
            data[i][1] = record.movieTitle;
            data[i][2] = record.showTime;
            data[i][3] = record.seatId;
            data[i][4] = record.bookingId;
        }

        allBookingsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        });
        allBookingsTable.setFillsViewportHeight(true);
        allBookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        allBookingsTable.setRowHeight(25);
    }

    private void refreshSeatOccupancy() {
        seatPanel.removeAll();

        if (movieBox.getItemCount() == 0 || timeBox.getItemCount() == 0) {
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        String selectedMovie = Objects.requireNonNull(movieBox.getSelectedItem()).toString();
        String selectedTime = Objects.requireNonNull(timeBox.getSelectedItem()).toString();
        Set<String> bookedSeats = DatabaseManager.getBookedSeats(selectedMovie, selectedTime);

        for (char r = 'A'; r <= 'F'; r++) {
            for (int c = 1; c <= 10; c++) {
                String id = "" + r + c;
                JLabel seatLabel = new JLabel(id, SwingConstants.CENTER);
                seatLabel.setOpaque(true);
                seatLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                seatLabel.setForeground(Color.WHITE);

                if (bookedSeats.contains(id)) {
                    seatLabel.setBackground(Color.DARK_GRAY);
                    seatLabel.setText("X");
                    seatLabel.setToolTipText("Booked");
                } else {
                    seatLabel.setBackground(new Color(0, 100, 0));
                    seatLabel.setToolTipText("Available");
                }
                seatPanel.add(seatLabel);
            }
        }
        seatPanel.revalidate();
        seatPanel.repaint();
    }
}

/*  PaymentDialog and BookingsDialog classes same as your original code; copy them from earlier file */
class PaymentDialog extends JDialog {
    PaymentDialog(JFrame parent, String user, String movie, String time, int total, List<DatabaseManager.Seat> seatsToBook, Runnable onPaymentSuccess) {
        super(parent, "Payment Gateway", true);
        setSize(400, 350);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(20, 20, 25));
        setLocationRelativeTo(parent);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Secure Payment", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 1, 10, 10));
        center.setBackground(new Color(20, 20, 25));
        center.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        ButtonGroup group = new ButtonGroup();
        for (String label : new String[]{"Debit/Credit Card", "UPI Payment", "Scan QR"}) {
            JRadioButton rb = new JRadioButton(label);
            rb.setBackground(new Color(20, 20, 25));
            rb.setForeground(Color.WHITE);
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            group.add(rb);
            center.add(rb);
            if (label.equals("Debit/Credit Card")) rb.setSelected(true);
        }
        add(center, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(20, 20, 25));
        JLabel amt = new JLabel("Total Payable: ₹" + total, JLabel.CENTER);
        amt.setForeground(Color.YELLOW);
        amt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bottomPanel.add(amt, BorderLayout.NORTH);

        JButton payBtn = new JButton("Pay Now");
        payBtn.setBackground(new Color(0, 200, 100));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        payBtn.setPreferredSize(new Dimension(200, 40));
        payBtn.setFocusPainted(false);
        bottomPanel.add(payBtn, BorderLayout.CENTER);

        payBtn.addActionListener(e -> {
            DatabaseManager.addBooking(user, movie, time, seatsToBook);
            dispose();
            JOptionPane.showMessageDialog(parent, "Payment Successful!\nMovie: " + movie + "\nTime: " + time + "\nAmount: ₹" + total, "Ticket Confirmed", JOptionPane.INFORMATION_MESSAGE);
            onPaymentSuccess.run();
        });

        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}

class BookingsDialog extends JDialog {
    private final List<DatabaseManager.BookingRecord> records;
    private final JTable table;

    BookingsDialog(JFrame parent, String title, List<DatabaseManager.BookingRecord> records, boolean isUserView, Runnable refreshCallback) {
        super(parent, title, true);
        this.records = records;
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        String[] columnNames;
        Object[][] data;

        if (isUserView) {
            columnNames = new String[]{"Movie", "Showtime", "Seat ID", "Booking ID"};
            data = new Object[records.size()][4];
            for (int i = 0; i < records.size(); i++) {
                DatabaseManager.BookingRecord record = records.get(i);
                data[i][0] = record.movieTitle;
                data[i][1] = record.showTime;
                data[i][2] = record.seatId;
                data[i][3] = record.bookingId;
            }
        } else {
            columnNames = new String[]{"Username", "Movie", "Showtime", "Seat ID", "Booking ID"};
            data = new Object[records.size()][5];
            for (int i = 0; i < records.size(); i++) {
                DatabaseManager.BookingRecord record = records.get(i);
                data[i][0] = record.username;
                data[i][1] = record.movieTitle;
                data[i][2] = record.showTime;
                data[i][3] = record.seatId;
                data[i][4] = record.bookingId;
            }
        }

        table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        if (isUserView) {
            JPanel bottomPanel = new JPanel();
            bottomPanel.setBackground(new Color(30, 30, 40));
            JButton cancelBtn = new JButton("Cancel Selected Booking");
            cancelBtn.setBackground(new Color(200, 50, 50));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            cancelBtn.setFocusPainted(false);

            cancelBtn.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(this, "Please select a booking from the table to cancel.", "No Booking Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int bookingId = (int) table.getValueAt(selectedRow, 3);

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to cancel this booking?\nSeat: " + table.getValueAt(selectedRow, 2),
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (DatabaseManager.deleteBooking(bookingId)) {
                        JOptionPane.showMessageDialog(this, "Booking Canceled. Your refund will be processed in 24 hours.", "Cancellation Successful", JOptionPane.INFORMATION_MESSAGE);
                        if (refreshCallback != null) {
                            refreshCallback.run();
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Could not cancel booking. Please contact support.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            bottomPanel.add(cancelBtn);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        setVisible(true);
    }
}
