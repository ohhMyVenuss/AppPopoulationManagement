package CuoiKy1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginFormUI {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Phần mềm quản lý nhân khẩu");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        JPanel leftPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = new ImageIcon("src/Res_Image/UI.png");
                g.drawImage(icon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        leftPanel.setPreferredSize(new Dimension(400, 500));


        JPanel rightPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 102, 102);
                Color color2 = new Color(153, 51, 102);
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        rightPanel.setLayout(null);

        // Form đăng nhập
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setBounds(50, 100, 300, 30);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(50, 130, 300, 30);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setBounds(50, 180, 300, 30);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 210, 300, 30);

        JButton btnSignIn = new JButton("Sign In");
        btnSignIn.setBounds(50, 260, 100, 30);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(200, 260, 100, 30);

        JLabel lblMessage = new JLabel("");
        lblMessage.setForeground(Color.YELLOW);
        lblMessage.setBounds(50, 310, 300, 30);

        btnSignIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                if (validateLogin(username, password)) {
                    lblMessage.setText("Đăng nhập thành công!");
                    lblMessage.setForeground(Color.GREEN);
                    frame.dispose(); 
                    new QuanLyNhanKhau(); 
                } else {
                    lblMessage.setText("Sai Username hoặc Password!");
                    lblMessage.setForeground(Color.WHITE);
                }
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterForm();
            }
        });

        rightPanel.add(lblUsername);
        rightPanel.add(txtUsername);
        rightPanel.add(lblPassword);
        rightPanel.add(txtPassword);
        rightPanel.add(btnSignIn);
        rightPanel.add(btnRegister);
        rightPanel.add(lblMessage);

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static boolean validateLogin(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/user"; 
        String dbUser = "root";
        String dbPass = "nhatminh";

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username và Password không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    private static void openRegisterForm() {
        JFrame registerFrame = new JFrame("Register Form");
        registerFrame.setSize(400, 300);
        registerFrame.setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 50, 100, 30);

        JTextField txtUsername = new JTextField();
        txtUsername.setBounds(150, 50, 200, 30);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 100, 100, 30);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setBounds(150, 100, 200, 30);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(150, 150, 100, 30);

        JLabel lblMessage = new JLabel("");
        lblMessage.setBounds(50, 200, 300, 30);

        btnRegister.addActionListener(e -> {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());

            if (registerUser(username, password)) {
                lblMessage.setText("Đăng ký thành công!");
                lblMessage.setForeground(Color.GREEN);
            } else {
                lblMessage.setText("Đăng ký thất bại!");
                lblMessage.setForeground(Color.RED);
            }
        });

        registerFrame.add(lblUsername);
        registerFrame.add(txtUsername);
        registerFrame.add(lblPassword);
        registerFrame.add(txtPassword);
        registerFrame.add(btnRegister);
        registerFrame.add(lblMessage);

        registerFrame.setVisible(true);
    }

    private static boolean registerUser(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/user"; 
        String dbUser = "root";
        String dbPass = "nhatminh";

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username và Password không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(null, "Username đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
