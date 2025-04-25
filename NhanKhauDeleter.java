package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class NhanKhauDeleter {
    private JTable table;

    public NhanKhauDeleter(JTable table) {
        this.table = table;
    }

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

    public void deleteNhanKhau(DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) table.getValueAt(selectedRow, 0); // Lấy ID từ dòng được chọn
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Bạn có chắc chắn muốn xóa nhân khẩu này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = connectDatabase()) {
                    if (conn != null) {
                        String query = "DELETE FROM nhankhau WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setInt(1, id);
                        int affectedRows = stmt.executeUpdate(); // Kiểm tra số dòng bị ảnh hưởng
                        if (affectedRows > 0) {
                            model.removeRow(selectedRow); // Xóa dòng khỏi bảng JTable
                            JOptionPane.showMessageDialog(null, "Xóa nhân khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Không thể xóa nhân khẩu. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi xóa nhân khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
