package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.*;
import java.sql.Timestamp;

public class HoGiaDinhManager {

    private Connection connectDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/quanlynhankhau";
            String username = "root";
            String password = "nhatminh";
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Kết nối cơ sở dữ liệu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Thêm hộ gia đình
    public void addHoGiaDinh(DefaultTableModel model) {
        JTextField chuHoIdField = new JTextField();
        JTextField diaChiField = new JTextField();

        Object[] fields = {
            "ID Chủ Hộ:", chuHoIdField,
            "Địa Chỉ:", diaChiField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Thêm Hộ Gia Đình", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = connectDatabase()) {
                if (conn != null) {
                    String query = "INSERT INTO hogiadinh (chu_ho_id, dia_chi, ngay_tao) VALUES (?, ?, CURRENT_TIMESTAMP)";
                    PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, Integer.parseInt(chuHoIdField.getText()));
                    stmt.setString(2, diaChiField.getText());
                    stmt.executeUpdate();

                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        model.addRow(new Object[]{id, chuHoIdField.getText(), diaChiField.getText(), new Timestamp(System.currentTimeMillis())});
                        JOptionPane.showMessageDialog(null, "Thêm hộ gia đình thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi thêm hộ gia đình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Xóa hộ gia đình
    public void deleteHoGiaDinh(DefaultTableModel model, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Bạn có chắc chắn muốn xóa hộ gia đình này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = connectDatabase()) {
                    if (conn != null) {
                        String query = "DELETE FROM hogiadinh WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setInt(1, id);
                        int affectedRows = stmt.executeUpdate();
                        if (affectedRows > 0) {
                            model.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(null, "Xóa hộ gia đình thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi xóa hộ gia đình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Tìm kiếm hộ gia đình dựa trên ID chủ hộ
    public void searchHoGiaDinh(DefaultTableModel model) {
        // Hiển thị hộp thoại nhập thông tin tìm kiếm
        String searchInput = JOptionPane.showInputDialog(
            null, 
            "Nhập ID chủ hộ:", 
            "Tìm kiếm Hộ Gia Đình", 
            JOptionPane.QUESTION_MESSAGE
        );

        // Kiểm tra xem người dùng có nhập thông tin không
        if (searchInput != null && !searchInput.trim().isEmpty()) {
            try (Connection conn = connectDatabase()) {
                if (conn != null) {
                    // Truy vấn tìm kiếm dựa trên ID chủ hộ
                    String query = "SELECT * FROM hogiadinh WHERE chu_ho_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(searchInput.trim())); // Ép kiểu sang số nguyên

                    ResultSet rs = stmt.executeQuery();

                    // Xóa dữ liệu cũ trên bảng
                    model.setRowCount(0);

                    // Hiển thị kết quả tìm kiếm
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("chu_ho_id"),
                            rs.getString("dia_chi"),
                            rs.getTimestamp("ngay_tao")
                        });
                    }

                    // Nếu không có kết quả phù hợp
                    if (model.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy hộ gia đình với ID chủ hộ: " + searchInput, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ID chủ hộ phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm hộ gia đình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập ID chủ hộ để tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }

        // Hỏi người dùng có muốn quay lại danh sách ban đầu
        int confirm = JOptionPane.showConfirmDialog(
            null, 
            "Bạn có muốn quay lại danh sách ban đầu?", 
            "Quay lại", 
            JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            loadHoGiaDinhData(model); // Tải lại toàn bộ dữ liệu
        }
    }

    public void loadHoGiaDinhData(DefaultTableModel model) {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT id, chu_ho_id, dia_chi, " +
                               "(SELECT COUNT(*) FROM thanhvien_hogiadinh WHERE hogiadinh_id = hogiadinh.id) AS so_thanh_vien, " +
                               "ngay_tao FROM hogiadinh";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                model.setRowCount(0); // Xóa dữ liệu cũ trong bảng

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getInt("chu_ho_id"),
                            rs.getString("dia_chi"),
                            rs.getInt("so_thanh_vien"),
                            rs.getTimestamp("ngay_tao")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu hộ gia đình!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Quản lý thành viên hộ gia đình
    public void manageMembers(JTable hoGiaDinhTable) {
        int selectedRow = hoGiaDinhTable.getSelectedRow();
        if (selectedRow != -1) {
            int hoGiaDinhId = (int) hoGiaDinhTable.getValueAt(selectedRow, 0);

            JFrame frame = new JFrame("Quản lý thành viên - Hộ Gia Đình ID: " + hoGiaDinhId);
            frame.setSize(600, 400);
            frame.setLayout(new BorderLayout());

            JLabel label = new JLabel("Thành viên Hộ Gia Đình ID: " + hoGiaDinhId, SwingConstants.CENTER);
            frame.add(label, BorderLayout.NORTH);

            String[] columns = {"ID", "ID Nhân Khẩu", "Quan Hệ Với Chủ Hộ", "Ngày Thêm"};
            DefaultTableModel memberModel = new DefaultTableModel(columns, 0);
            JTable memberTable = new JTable(memberModel);
            JScrollPane scrollPane = new JScrollPane(memberTable);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton btnAdd = new JButton("Thêm Thành Viên");
            JButton btnDelete = new JButton("Xóa Thành Viên");
            buttonPanel.add(btnAdd);
            buttonPanel.add(btnDelete);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            loadMembers(memberModel, hoGiaDinhId);

            btnAdd.addActionListener(e -> addMember(memberModel, hoGiaDinhId));
            btnDelete.addActionListener(e -> deleteMember(memberModel, memberTable));

            frame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một hộ gia đình để quản lý thành viên!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadMembers(DefaultTableModel model, int hoGiaDinhId) {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT * FROM thanhvien_hogiadinh WHERE hogiadinh_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, hoGiaDinhId);
                ResultSet rs = stmt.executeQuery();

                model.setRowCount(0); // Xóa dữ liệu cũ trong bảng

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("nhankhau_id"),
                        rs.getString("quan_he_voi_chu_ho"),
                        rs.getTimestamp("ngay_them")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu thành viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addMember(DefaultTableModel model, int hoGiaDinhId) {
        JTextField nhanKhauIdField = new JTextField();
        JTextField quanHeField = new JTextField();

        Object[] fields = {
            "ID Nhân Khẩu:", nhanKhauIdField,
            "Quan Hệ Với Chủ Hộ:", quanHeField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Thêm Thành Viên", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = connectDatabase()) {
                if (conn != null) {
                    String query = "INSERT INTO thanhvien_hogiadinh (hogiadinh_id, nhankhau_id, quan_he_voi_chu_ho, ngay_them) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                    PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, hoGiaDinhId);
                    stmt.setInt(2, Integer.parseInt(nhanKhauIdField.getText()));
                    stmt.setString(3, quanHeField.getText());
                    stmt.executeUpdate();

                    
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        model.addRow(new Object[]{id, nhanKhauIdField.getText(), quanHeField.getText()});

                        // Cập nhật số lượng thành viên trong bảng hogiadinh
                        String updateQuery = "UPDATE hogiadinh SET so_thanh_vien = so_thanh_vien + 1 WHERE id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setInt(1, hoGiaDinhId);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Thêm thành viên thành công và cập nhật số lượng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi thêm thành viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public void deleteMember(DefaultTableModel model, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) table.getValueAt(selectedRow, 0); 
            int hoGiaDinhId = (int) table.getValueAt(selectedRow, 1); 

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Bạn có chắc chắn muốn xóa thành viên này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = connectDatabase()) {
                    if (conn != null) {
                        // Xóa thành viên khỏi bảng thanhvien_hogiadinh
                        String query = "DELETE FROM thanhvien_hogiadinh WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                        model.removeRow(selectedRow);

                        // Cập nhật số lượng thành viên trong bảng hogiadinh
                        String updateQuery = "UPDATE hogiadinh SET so_thanh_vien = so_thanh_vien - 1 WHERE id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setInt(1, hoGiaDinhId);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Xóa thành viên thành công và cập nhật số lượng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi xóa thành viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

}
