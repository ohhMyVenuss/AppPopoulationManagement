package CuoiKy1;


import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ThongKeService {

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

    public JFreeChart getBarChartDoTuoi() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String query = "SELECT CASE " +
                       "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) < 18 THEN 'Dưới 18' " +
                       "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) BETWEEN 18 AND 35 THEN '18-35' " +
                       "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) BETWEEN 36 AND 60 THEN '36-60' " +
                       "ELSE 'Trên 60' END AS do_tuoi, COUNT(*) AS so_luong " +
                       "FROM nhankhau GROUP BY do_tuoi";

        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dataset.addValue(rs.getInt("so_luong"), "Số lượng", rs.getString("do_tuoi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ChartFactory.createBarChart("Thống kê Độ tuổi", "Độ tuổi", "Số lượng", dataset);
    }

    public JFreeChart getPieChartGioiTinh() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        String query = "SELECT gioi_tinh, COUNT(*) AS so_luong FROM nhankhau GROUP BY gioi_tinh";

        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                dataset.setValue(rs.getString("gioi_tinh"), rs.getInt("so_luong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return ChartFactory.createPieChart("Thống kê Giới tính", dataset, true, true, false);
    }

    public int getTotalNhanKhau() {
        String query = "SELECT COUNT(*) AS total FROM nhankhau";
        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalHoGiaDinh() {
        String query = "SELECT COUNT(*) AS total FROM hogiadinh";
        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public String getGenderRatio() {
        String ratio = "";
        String query = "SELECT " +
                       "(SELECT COUNT(*) FROM nhankhau WHERE gioi_tinh = 'Nam') AS so_luong_nam, " +
                       "(SELECT COUNT(*) FROM nhankhau WHERE gioi_tinh = 'Nữ') AS so_luong_nu";

        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int soLuongNam = rs.getInt("so_luong_nam");
                int soLuongNu = rs.getInt("so_luong_nu");
                ratio = soLuongNam + " / " + soLuongNu; // Tạo chuỗi tỷ lệ Nam/Nữ
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê tỷ lệ giới tính!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        return ratio;
    }

}
