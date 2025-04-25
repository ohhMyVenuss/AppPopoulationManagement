package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NhanKhauAdder {
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

    public void addNhanKhau(DefaultTableModel model) {
        JTextField hoTenField = new JTextField();
        JTextField ngaySinhField = new JTextField();
        JTextField gioiTinhField = new JTextField();
        JTextField diaChiField = new JTextField();
        JTextField cccdField = new JTextField();
        JTextField ngheNghiepField = new JTextField();
        JTextField tinhTrangHonNhanField = new JTextField();

        Object[] fields = {
            "Họ Tên:", hoTenField,
            "Ngày Sinh (YYYY-MM-DD):", ngaySinhField,
            "Giới Tính:", gioiTinhField,
            "Địa Chỉ:", diaChiField,
            "Số CCCD:", cccdField,
            "Nghề Nghiệp:", ngheNghiepField,
            "Tình Trạng Hôn Nhân:", tinhTrangHonNhanField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Thêm Nhân Khẩu", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = connectDatabase()) {
                if (conn != null) {
                    String query = "INSERT INTO nhankhau (ho_ten, ngay_sinh, gioi_tinh, dia_chi, cmnd_cccd, nghe_nghiep, tinh_trang_hon_nhan, ngay_tao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    stmt.setString(1, hoTenField.getText());
                    stmt.setString(2, ngaySinhField.getText());
                    stmt.setString(3, gioiTinhField.getText());
                    stmt.setString(4, diaChiField.getText());
                    stmt.setString(5, cccdField.getText());
                    stmt.setString(6, ngheNghiepField.getText());
                    stmt.setString(7, tinhTrangHonNhanField.getText());

                    // Lấy thời gian thực cho ngày tạo
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentTime = sdf.format(new Date());
                    stmt.setString(8, currentTime);

                    stmt.executeUpdate();

                    // Lấy ID tự động tạo
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        model.addRow(new Object[]{
                            id,
                            hoTenField.getText(),
                            ngaySinhField.getText(),
                            gioiTinhField.getText(),
                            diaChiField.getText(),
                            cccdField.getText(),
                            ngheNghiepField.getText(),
                            tinhTrangHonNhanField.getText(),
                            currentTime
                        });

                        JOptionPane.showMessageDialog(null, "Thêm nhân khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi thêm nhân khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
