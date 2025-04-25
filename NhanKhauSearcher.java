package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class NhanKhauSearcher {
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

    public void searchNhanKhau(DefaultTableModel model) {
        String searchInput = JOptionPane.showInputDialog(null, "Nhập thông tin tìm kiếm (Họ tên/CCCD):", "Tìm kiếm", JOptionPane.QUESTION_MESSAGE);
        if (searchInput != null && !searchInput.trim().isEmpty()) {
            try (Connection conn = connectDatabase()) {
                if (conn != null) {
                    String query = "SELECT * FROM nhankhau WHERE ho_ten LIKE ? OR cmnd_cccd LIKE ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, "%" + searchInput + "%");
                    stmt.setString(2, "%" + searchInput + "%");
                    ResultSet rs = stmt.executeQuery();

                    model.setRowCount(0); // Xóa dữ liệu cũ trong bảng

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("ho_ten"),
                            rs.getDate("ngay_sinh"),
                            rs.getString("gioi_tinh"),
                            rs.getString("dia_chi"),
                            rs.getString("cmnd_cccd"),
                            rs.getString("nghe_nghiep"),
                            rs.getString("tinh_trang_hon_nhan"),
                            rs.getTimestamp("ngay_tao")
                        });
                    }

                    if (model.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(null, "Không tìm thấy kết quả phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi tìm kiếm nhân khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập thông tin để tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }

        // Hỏi người dùng có muốn quay lại danh sách gốc
        int confirm = JOptionPane.showConfirmDialog(null, "Bạn có muốn quay lại danh sách ban đầu?", "Quay lại", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            loadData(model);
        }
    }

    private void loadData(DefaultTableModel model) {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT * FROM nhankhau";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                model.setRowCount(0); // Xóa dữ liệu cũ trong bảng

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("ho_ten"),
                        rs.getDate("ngay_sinh"),
                        rs.getString("gioi_tinh"),
                        rs.getString("dia_chi"),
                        rs.getString("cmnd_cccd"),
                        rs.getString("nghe_nghiep"),
                        rs.getString("tinh_trang_hon_nhan"),
                        rs.getTimestamp("ngay_tao")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
