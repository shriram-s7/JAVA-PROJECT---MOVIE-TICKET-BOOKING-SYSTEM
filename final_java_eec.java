import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class final_java_eec {
    static JFrame frame;
    static CardLayout cardLayout;
    static JPanel mainPanel;
    static String currentUser = "";

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        SwingUtilities.invokeLater(final_java_eec::createLoginPage);
    }

    static void createLoginPage() {
        frame = new JFrame("BookMyShow Deluxe");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ===== LOGIN PANEL =====
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
        JTextField userField = new JTextField(15);
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
        JPasswordField passField = new JPasswordField(15);
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

        // === LOGIN ACTION ===
        loginBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (DatabaseManager.validateUser(user, pass)) {
                currentUser = user;
                openMainBookingPage();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // === SIGNUP ACTION ===
        signupBtn.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both a username and password.");
                return;
            }
            if (DatabaseManager.addUser(user, pass)) {
                JOptionPane.showMessageDialog(frame, "✅ Account created! You can now log in.");
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists. Please choose another one.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(loginPanel, "Login");
        frame.add(mainPanel);
        frame.setVisible(true);
    }

    static void openMainBookingPage() {
        BookingPage booking = new BookingPage(frame, mainPanel, cardLayout, currentUser);
        mainPanel.add(booking, "Booking");
        cardLayout.show(mainPanel, "Booking");
    }

    static void styleButton(JButton btn) {
        btn.setBackground(new Color(0, 180, 90));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}

/* ===================== BOOKING PAGE ===================== */
class BookingPage extends JPanel {
    private final JFrame frame;
    private final String currentUser;
    private final JComboBox<String> movieBox;
    private final JComboBox<String> timeBox;
    private final JLabel totalLabel;
    private final JPanel seatPanel;

    private final Map<String, Seat> allSeats = new HashMap<>();
    private final List<Seat> selectedSeats = new ArrayList<>();
    private int total = 0;

    public static class Seat {
        final String id;
        final String category;
        final int price;
        boolean isSelected = false;

        Seat(String id, String category, int price) {
            this.id = id;
            this.category = category;
            this.price = price;
        }
    }

    BookingPage(JFrame frame, JPanel parent, CardLayout layout, String user) {
        this.frame = frame;
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(new Color(15, 15, 20));

        // Create a top panel to hold the title and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(15, 15, 20)); // Match the page background
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add some padding

        // Title Label (as before)
        JLabel title = new JLabel("Welcome, " + user + " | Book Your Show", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);

        // NEW: Logout Button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(200, 50, 50)); // Red color for exit/logout
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            // Switch back to the "Login" card
            layout.show(parent, "Login");
        });

        // Add components to the top panel
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        // Add the top panel to the main page
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(150, 0, 0),
                        0, getHeight(), new Color(60, 0, 70));
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

        movieBox = new JComboBox<>(new String[]{"Avengers: Endgame", "Leo", "Jawan", "Oppenheimer"});
        styleCombo(movieBox);
        movieBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(movieBox);
        leftPanel.add(Box.createVerticalStrut(35));

        JLabel timeLbl = createStyledLabel("Showtime");
        timeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(timeLbl);
        leftPanel.add(Box.createVerticalStrut(10));

        timeBox = new JComboBox<>(new String[]{"10:00 AM", "1:00 PM", "4:00 PM", "7:00 PM"});
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
        final_java_eec.styleButton(confirmBtn);
        confirmBtn.addActionListener(e -> proceedPayment());

        JButton viewBookingsBtn = new JButton("View All Bookings");
        viewBookingsBtn.setBackground(new Color(0, 120, 215));
        viewBookingsBtn.setForeground(Color.WHITE);
        viewBookingsBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        viewBookingsBtn.setFocusPainted(false);
        viewBookingsBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewBookingsBtn.addActionListener(e -> showBookings());

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

        String selectedMovie = Objects.requireNonNull(movieBox.getSelectedItem()).toString();
        String selectedTime = Objects.requireNonNull(timeBox.getSelectedItem()).toString();
        Set<String> bookedSeats = DatabaseManager.getBookedSeats(selectedMovie, selectedTime);

        for (char r = 'A'; r <= 'F'; r++) {
            for (int c = 1; c <= 10; c++) {
                String id = "" + r + c;
                final int price = (r <= 'B') ? 300 : (r <= 'D' ? 200 : 120);
                final Color color = (r <= 'B') ? new Color(210, 4, 45) : (r <= 'D' ? new Color(255, 191, 0) : new Color(0, 150, 136));
                Seat seat = new Seat(id, "", price);
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

    private void toggleSeatSelection(Seat seat, JButton btn, Color originalColor) {
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
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select at least one seat!", "No Seats Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new PaymentDialog(frame, currentUser,
                Objects.requireNonNull(movieBox.getSelectedItem()).toString(),
                Objects.requireNonNull(timeBox.getSelectedItem()).toString(),
                total, selectedSeats, this::refreshSeatLayout);
    }

    void showBookings() {
        String selectedMovie = Objects.requireNonNull(movieBox.getSelectedItem()).toString();
        List<DatabaseManager.BookingRecord> records = DatabaseManager.getBookingsForMovie(selectedMovie);
        if (records.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "No bookings have been made for this movie yet.",
                    "No Records Found",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            new BookingsDialog(frame, selectedMovie, records);
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

/* ===================== PAYMENT DIALOG ===================== */
class PaymentDialog extends JDialog {
    PaymentDialog(JFrame parent, String user, String movie, String time, int total, List<BookingPage.Seat> seatsToBook, Runnable onPaymentSuccess) {
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

/* ===================== BOOKINGS DIALOG ===================== */
class BookingsDialog extends JDialog {
    BookingsDialog(JFrame parent, String movieTitle, List<DatabaseManager.BookingRecord> records) {
        super(parent, "Bookings for: " + movieTitle, true);
        setSize(500, 400);
        setLocationRelativeTo(parent);

        String[] columnNames = {"Username", "Showtime", "Seat ID"};
        Object[][] data = new Object[records.size()][3];
        for (int i = 0; i < records.size(); i++) {
            DatabaseManager.BookingRecord record = records.get(i);
            data[i][0] = record.username;
            data[i][1] = record.showTime;
            data[i][2] = record.seatId;
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
    }
}
