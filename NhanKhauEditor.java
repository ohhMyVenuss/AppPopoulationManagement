package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class NhanKhauEditor {
    private JTable table;

    public NhanKhauEditor(JTable table) {
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

    public void editNhanKhau(DefaultTableModel model) {
        int selectedRow = table.getSelectedRow(); // Lấy dòng được chọn
        if (selectedRow != -1) {
            int id = (int) table.getValueAt(selectedRow, 0); // Lấy ID từ dòng được chọn

            // Lấy thông tin từ dòng được chọn
            JTextField hoTenField = new JTextField((String) table.getValueAt(selectedRow, 1));
            JTextField ngaySinhField = new JTextField(table.getValueAt(selectedRow, 2).toString());
            JTextField gioiTinhField = new JTextField((String) table.getValueAt(selectedRow, 3));
            JTextField diaChiField = new JTextField((String) table.getValueAt(selectedRow, 4));
            JTextField cccdField = new JTextField((String) table.getValueAt(selectedRow, 5));
            JTextField ngheNghiepField = new JTextField((String) table.getValueAt(selectedRow, 6));
            JTextField tinhTrangHonNhanField = new JTextField((String) table.getValueAt(selectedRow, 7));

            // Hiển thị form chỉnh sửa
            Object[] fields = {
                "Họ Tên:", hoTenField,
                "Ngày Sinh (YYYY-MM-DD):", ngaySinhField,
                "Giới Tính:", gioiTinhField,
                "Địa Chỉ:", diaChiField,
                "Số CCCD:", cccdField,
                "Nghề Nghiệp:", ngheNghiepField,
                "Tình Trạng Hôn Nhân:", tinhTrangHonNhanField
            };

            int option = JOptionPane.showConfirmDialog(null, fields, "Chỉnh sửa Nhân Khẩu", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try (Connection conn = connectDatabase()) {
                    if (conn != null) {
                        
                        String query = "UPDATE nhankhau SET ho_ten = ?, ngay_sinh = ?, gioi_tinh = ?, dia_chi = ?, cmnd_cccd = ?, nghe_nghiep = ?, tinh_trang_hon_nhan = ? WHERE id = ?";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, hoTenField.getText());
                        stmt.setString(2, ngaySinhField.getText());
                        stmt.setString(3, gioiTinhField.getText());
                        stmt.setString(4, diaChiField.getText());
                        stmt.setString(5, cccdField.getText());
                        stmt.setString(6, ngheNghiepField.getText());
                        stmt.setString(7, tinhTrangHonNhanField.getText());
                        stmt.setInt(8, id);

                        int affectedRows = stmt.executeUpdate(); // Thực hiện cập nhật
                        if (affectedRows > 0) {
                            // Cập nhật lại dữ liệu trên JTable
                            model.setValueAt(hoTenField.getText(), selectedRow, 1);
                            model.setValueAt(ngaySinhField.getText(), selectedRow, 2);
                            model.setValueAt(gioiTinhField.getText(), selectedRow, 3);
                            model.setValueAt(diaChiField.getText(), selectedRow, 4);
                            model.setValueAt(cccdField.getText(), selectedRow, 5);
                            model.setValueAt(ngheNghiepField.getText(), selectedRow, 6);
                            model.setValueAt(tinhTrangHonNhanField.getText(), selectedRow, 7);

                            JOptionPane.showMessageDialog(null, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Không thể cập nhật thông tin. Vui lòng thử lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để chỉnh sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }
}
