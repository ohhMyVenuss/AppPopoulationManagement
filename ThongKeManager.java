package CuoiKy1;

import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.BorderLayout;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ThongKeManager {

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

    // Thống kê theo độ tuổi
    public void thongKeTheoDoTuoi() {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT CASE " +
                               "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) < 18 THEN 'Dưới 18' " +
                               "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) BETWEEN 18 AND 35 THEN '18-35' " +
                               "WHEN YEAR(CURDATE()) - YEAR(ngay_sinh) BETWEEN 36 AND 60 THEN '36-60' " +
                               "ELSE 'Trên 60' END AS do_tuoi, COUNT(*) AS so_luong " +
                               "FROM nhankhau GROUP BY do_tuoi";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                // Tạo dataset cho biểu đồ
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                while (rs.next()) {
                    dataset.addValue(rs.getInt("so_luong"), "Số lượng", rs.getString("do_tuoi"));
                }

                // Tạo biểu đồ
                JFreeChart chart = ChartFactory.createBarChart(
                        "Thống kê dân số theo Độ tuổi",
                        "Độ tuổi",
                        "Số lượng",
                        dataset
                );

                // Hiển thị biểu đồ
                ChartPanel chartPanel = new ChartPanel(chart);
                JFrame frame = new JFrame("Thống kê Dân số theo Độ tuổi");
                frame.setSize(800, 600);
                frame.add(chartPanel);
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy dữ liệu thống kê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thống kê theo giới tính
    public void thongKeTheoGioiTinh() {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT gioi_tinh, COUNT(*) AS so_luong FROM nhankhau GROUP BY gioi_tinh";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                // Tạo dataset cho biểu đồ
                DefaultPieDataset dataset = new DefaultPieDataset();
                while (rs.next()) {
                    dataset.setValue(rs.getString("gioi_tinh"), rs.getInt("so_luong"));
                }

                // Tạo biểu đồ
                JFreeChart chart = ChartFactory.createPieChart(
                        "Thống kê dân số theo Giới tính",
                        dataset,
                        true,
                        true,
                        false
                );

                // Hiển thị biểu đồ
                ChartPanel chartPanel = new ChartPanel(chart);
                JFrame frame = new JFrame("Thống kê Dân số theo Giới tính");
                frame.setSize(800, 600);
                frame.add(chartPanel);
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy dữ liệu thống kê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thống kê theo quê quán
    public void thongKeTheoQueQuan() {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String query = "SELECT dia_chi AS que_quan, COUNT(*) AS so_luong FROM nhankhau GROUP BY dia_chi";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                // Tạo dataset cho biểu đồ
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();
                while (rs.next()) {
                    dataset.addValue(rs.getInt("so_luong"), "Số lượng", rs.getString("que_quan"));
                }

                // Tạo biểu đồ
                JFreeChart chart = ChartFactory.createBarChart(
                        "Thống kê dân số theo Quê quán",
                        "Quê quán",
                        "Số lượng",
                        dataset
                );

                // Hiển thị biểu đồ
                ChartPanel chartPanel = new ChartPanel(chart);
                JFrame frame = new JFrame("Thống kê Dân số theo Quê quán");
                frame.setSize(800, 600);
                frame.add(chartPanel);
                frame.setVisible(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy dữ liệu thống kê!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Map<String, Integer> thongKeTinhTrangHonNhan() {
        Map<String, Integer> result = new HashMap<>();
        String query = "SELECT tinh_trang_hon_nhan, COUNT(*) AS so_luong " +
                       "FROM nhankhau " +
                       "GROUP BY tinh_trang_hon_nhan";

        try (Connection conn = connectDatabase();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String tinhTrang = rs.getString("tinh_trang_hon_nhan");
                int soLuong = rs.getInt("so_luong");
                result.put(tinhTrang, soLuong);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi thống kê tình trạng hôn nhân!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        return result;
    }

    // Phương thức hiển thị biểu đồ thống kê tình trạng hôn nhân
    public void hienThiBieuDoTinhTrangHonNhan() {
        Map<String, Integer> data = thongKeTinhTrangHonNhan();

        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không có dữ liệu thống kê tình trạng hôn nhân!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tạo dataset cho biểu đồ
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            dataset.addValue(entry.getValue(), "Tình trạng hôn nhân", entry.getKey());
        }

        // Tạo biểu đồ
        JFreeChart barChart = ChartFactory.createBarChart(
            "Thống kê Tình Trạng Hôn Nhân", // Tiêu đề
            "Tình Trạng", // Trục X
            "Số Lượng",   // Trục Y
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            false, true, false);

        // Hiển thị biểu đồ trong JFrame
        ChartPanel chartPanel = new ChartPanel(barChart);
        JFrame frame = new JFrame("Biểu đồ Tình Trạng Hôn Nhân");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(chartPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

}
