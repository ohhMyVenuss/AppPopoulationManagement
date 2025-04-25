package CuoiKy1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import CuoiKy1.HoGiaDinhManager;
import CuoiKy1.NhanKhauAdder;
import CuoiKy1.NhanKhauDeleter;
import CuoiKy1.NhanKhauEditor;
import CuoiKy1.NhanKhauSearcher;
import CuoiKy1.ThongKeManager;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuanLyNhanKhau {
    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTable table;

    public QuanLyNhanKhau() {
        frame = new JFrame("Phần mềm Quản lý Nhân khẩu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());

        // side bar
        JPanel sidebar = createSidebar();
        frame.add(sidebar, BorderLayout.WEST);

        // Nội dung side bar
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        mainPanel.add(createDashboardPanel(), "TrangChu");
        mainPanel.add(createQuanLyNhanKhauPanel(), "QuanLyNhanKhau");
        mainPanel.add(createQuanLyHoGiaDinhPanel(), "QuanLyHoGiaDinh");
        mainPanel.add(createThongKePanel(), "ThongKe");
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
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
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(220, 220, 220)); 
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10)); 
        sidebar.setPreferredSize(new Dimension(200, 0)); 

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Load and scale icons
        ImageIcon homeIcon = scaleIcon("/res_icon/home.png", 30, 30);
        ImageIcon userIcon = scaleIcon("/res_icon/user.png", 30, 30);
        ImageIcon managerIcon = scaleIcon("/res_icon/manager.png", 30, 30);
        ImageIcon analysisIcon = scaleIcon("/res_icon/analysis.png", 30, 30);

        JButton btnTrangChu = new JButton("Trang chủ", homeIcon);
        JButton btnQuanLyNhanKhau = new JButton("Quản lý nhân khẩu", userIcon);
        JButton btnQuanLyHoGiaDinh = new JButton("Quản lý hộ gia đình", managerIcon);
        JButton btnThongKe = new JButton("Thống kê", analysisIcon);

        // Đặt vị trí chữ nằm bên phải icon
        btnTrangChu.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnQuanLyNhanKhau.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnQuanLyHoGiaDinh.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnThongKe.setHorizontalTextPosition(SwingConstants.RIGHT);

        btnTrangChu.addActionListener(e -> cardLayout.show(mainPanel, "TrangChu"));
        btnQuanLyNhanKhau.addActionListener(e -> cardLayout.show(mainPanel, "QuanLyNhanKhau"));
        btnQuanLyHoGiaDinh.addActionListener(e -> cardLayout.show(mainPanel, "QuanLyHoGiaDinh"));
        btnThongKe.addActionListener(e -> cardLayout.show(mainPanel, "ThongKe"));

        buttonPanel.add(btnTrangChu);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnQuanLyNhanKhau);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnQuanLyHoGiaDinh);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnThongKe);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        for (Component comp : buttonPanel.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).setMaximumSize(new Dimension(200, 50));
            }
        }

        sidebar.add(buttonPanel, BorderLayout.CENTER);

        return sidebar;
    }

    // Hàm scale icon
    private ImageIcon scaleIcon(String resourcePath, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(resourcePath));
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Không thể tải hoặc scale ảnh: " + resourcePath);
            e.printStackTrace();
            return null; // Trả về null nếu xảy ra lỗi
        }
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Trang chủ: Thống kê và thông tin tổng quan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        dashboardPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        dashboardPanel.add(chartPanel, BorderLayout.CENTER);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 2 , 10, 10));
        dashboardPanel.add(summaryPanel, BorderLayout.SOUTH);

        ThongKeService service = new ThongKeService();
        JFreeChart ageChart = service.getBarChartDoTuoi();
        
        JFreeChart genderChart = service.getPieChartGioiTinh();

        ChartPanel ageChartPanel = new ChartPanel(ageChart);
        ChartPanel genderChartPanel = new ChartPanel(genderChart);
        ageChartPanel.setPreferredSize(new Dimension(400, 300));
        genderChartPanel.setPreferredSize(new Dimension(400, 300));
        
        chartPanel.add(ageChartPanel);
        chartPanel.add(genderChartPanel);

        summaryPanel.add(createSummaryBox("Tổng nhân khẩu", String.valueOf(service.getTotalNhanKhau())));
        summaryPanel.add(createSummaryBox("Tổng hộ gia đình", String.valueOf(service.getTotalHoGiaDinh())));

        summaryPanel.add(createSummaryBox("Tỷ lệ giới tính (Nam/Nữ)", String.valueOf(service.getGenderRatio())));

        return dashboardPanel;
    }


    private JPanel createSummaryBox(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }


    // fix all -> its done lol
    private JPanel createQuanLyNhanKhauPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Quản lý nhân khẩu", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        JPanel crudPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnDelete = new JButton("Xóa");
        JButton btnEdit = new JButton("Chỉnh sửa");
        JButton btnSearch = new JButton("Tìm kiếm");
      
        
        crudPanel.add(btnAdd);
        crudPanel.add(btnDelete);
        crudPanel.add(btnSearch);
        crudPanel.add(btnEdit);
        panel.add(crudPanel, BorderLayout.NORTH);
        
        
        String[] columns = {"ID", "Họ Tên", "Ngày Sinh", "Giới Tính", "Địa Chỉ", "CMND/CCCD", "Nghề Nghiệp", "Tình Trạng Hôn Nhân", "Ngày Tạo"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        loadData(model);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        NhanKhauAdder adder = new NhanKhauAdder(); 
        NhanKhauDeleter deleter = new NhanKhauDeleter(table);
        NhanKhauSearcher searcher = new NhanKhauSearcher();
        NhanKhauEditor editor = new NhanKhauEditor(table);

        btnAdd.addActionListener(e -> adder.addNhanKhau(model));
        btnDelete.addActionListener(e -> deleter.deleteNhanKhau(model));
        btnSearch.addActionListener(e -> searcher.searchNhanKhau(model));
        btnEdit.addActionListener(e -> editor.editNhanKhau(model));
        return panel;
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
    
    private JPanel createQuanLyHoGiaDinhPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Quản lý Hộ Gia Đình", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Tạo panel chứa các nút chức năng
        JPanel crudPanel = new JPanel();
        JButton btnAddHoGiaDinh = new JButton("Thêm Hộ Gia Đình");
        JButton btnDeleteHoGiaDinh = new JButton("Xóa Hộ Gia Đình");
        JButton btnSearchHoGiaDinh = new JButton("Tìm Kiếm Hộ Gia Đình");
        JButton btnManageMembers = new JButton("Quản Lý Thành Viên");
        JButton btnReloadData = new JButton("Tải Lại Dữ Liệu");

        // Thêm các nút vào panel CRUD
        crudPanel.add(btnAddHoGiaDinh);
        crudPanel.add(btnDeleteHoGiaDinh);
        crudPanel.add(btnSearchHoGiaDinh);
        crudPanel.add(btnManageMembers);
        crudPanel.add(btnReloadData);
        panel.add(crudPanel, BorderLayout.NORTH);

        // Tạo bảng hiển thị danh sách hộ gia đình
        String[] columns = {"ID", "ID Chủ Hộ", "Địa Chỉ", "Số Thành Viên trong hộ", "Ngày Tạo"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        HoGiaDinhManager manager = new HoGiaDinhManager();

        btnAddHoGiaDinh.addActionListener(e -> manager.addHoGiaDinh(model)); 
        btnDeleteHoGiaDinh.addActionListener(e -> manager.deleteHoGiaDinh(model, table)); 
        btnSearchHoGiaDinh.addActionListener(e -> manager.searchHoGiaDinh(model)); 
        btnManageMembers.addActionListener(e -> manager.manageMembers(table)); 
        btnReloadData.addActionListener(e -> manager.loadHoGiaDinhData(model)); 

        // Tải dữ liệu ban đầu
        manager.loadHoGiaDinhData(model);

        return panel;
    }

    private JPanel createThongKePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Thống kê dân số", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 10, 10));

        // Load và scale icon
        ImageIcon ageIcon = scaleIcon("/res_icon/growth.png", 40, 40);
        ImageIcon genderIcon = scaleIcon("/res_icon/sex.png", 40, 40);
        ImageIcon hometownIcon = scaleIcon("/res_icon/map.png", 40, 40);
        ImageIcon marriageIcon = scaleIcon("/res_icon/wedding.png", 40, 40);

        JButton btnAgeStats = new JButton("Thống kê theo Độ tuổi", ageIcon);
        JButton btnGenderStats = new JButton("Thống kê theo Giới tính", genderIcon);
        JButton btnHometownStats = new JButton("Thống kê theo Quê quán", hometownIcon);
        JButton btnHonNhanStats = new JButton("Thống kê tình trạng hôn nhân", marriageIcon);

        btnAgeStats.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnGenderStats.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnHometownStats.setHorizontalTextPosition(SwingConstants.RIGHT);
        btnHonNhanStats.setHorizontalTextPosition(SwingConstants.RIGHT);

        buttonPanel.add(btnAgeStats);
        buttonPanel.add(btnGenderStats);
        buttonPanel.add(btnHometownStats);
        buttonPanel.add(btnHonNhanStats);

        panel.add(buttonPanel, BorderLayout.CENTER);

        ThongKeManager manager = new ThongKeManager();

        btnAgeStats.addActionListener(e -> manager.thongKeTheoDoTuoi());
        btnGenderStats.addActionListener(e -> manager.thongKeTheoGioiTinh());
        btnHometownStats.addActionListener(e -> manager.thongKeTheoQueQuan());
        btnHonNhanStats.addActionListener(e -> manager.hienThiBieuDoTinhTrangHonNhan());

        return panel;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuanLyNhanKhau::new);
        
    }
}



