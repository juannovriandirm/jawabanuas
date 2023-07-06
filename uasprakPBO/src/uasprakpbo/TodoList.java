package uasprakpbo;

import java.sql.*;
import java.util.Scanner;

public class TodoList {
    // Konfigurasi koneksi database
    static final String DB_URL = "jdbc:mysql://localhost/todo_list";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Menghubungkan ke database
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Membuat tabel jika belum ada
            stmt = conn.createStatement();
            String createTableSql = "CREATE TABLE IF NOT EXISTS todos ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "todo VARCHAR(255),"
                    + "kategori VARCHAR(255),"
                    + "tanggal_selesai DATE,"
                    + "status VARCHAR(255)"
                    + ")";
            stmt.executeUpdate(createTableSql);

            // Menjalankan aplikasi CRUD
            boolean exit = false;
            Scanner scanner = new Scanner(System.in);
            while (!exit) {
                System.out.println("=== TODO LIST ===");
                System.out.println("1. Tampilkan Todo List");
                System.out.println("2. Tambah Todo");
                System.out.println("3. Ubah Todo");
                System.out.println("4. Hapus Todo");
                System.out.println("5. Keluar");
                System.out.print("Pilih menu: ");
                int menu = scanner.nextInt();
                scanner.nextLine(); // Membaca newline setelah input menu

                switch (menu) {
                    case 1:
                        showTodoList(conn);
                        break;
                    case 2:
                        addTodoItem(conn, scanner);
                        break;
                    case 3:
                        updateTodoItem(conn, scanner);
                        break;
                    case 4:
                        deleteTodoItem(conn, scanner);
                        break;
                    case 5:
                        exit = true;
                        break;
                    default:
                        System.out.println("Menu tidak valid.");
                }
                System.out.println();
            }

            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showTodoList(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM todos";
        ResultSet rs = stmt.executeQuery(sql);

        System.out.println("=== TODO LIST ===");
        while (rs.next()) {
            int id = rs.getInt("id");
            String todo = rs.getString("todo");
            String kategori = rs.getString("kategori");
            Date tanggalSelesai = rs.getDate("tanggal_selesai");
            String status = rs.getString("status");

            System.out.println("ID: " + id);
            System.out.println("Todo: " + todo);
            System.out.println("Kategori: " + kategori);
            System.out.println("Tanggal Selesai: " + tanggalSelesai);
            System.out.println("Status: " + status);
            System.out.println("-----------------");
        }

        rs.close();
        stmt.close();
    }

    private static void addTodoItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Masukkan Todo: ");
        String todo= scanner.nextLine();
        System.out.print("Masukkan Kategori: ");
        String kategori = scanner.nextLine();
        System.out.print("Masukkan Tanggal Selesai (yyyy-MM-dd): ");
        String tanggalSelesaiStr = scanner.nextLine();
        Date tanggalSelesai = Date.valueOf(tanggalSelesaiStr);
        System.out.print("Masukkan Status: ");
        String status = scanner.nextLine();

        String sql = "INSERT INTO todos (todo, kategori, tanggal_selesai, status) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, todo);
        stmt.setString(2, kategori);
        stmt.setDate(3, tanggalSelesai);
        stmt.setString(4, status);
        stmt.executeUpdate();

        System.out.println("Todo berhasil ditambahkan.");
        stmt.close();
    }

    private static void updateTodoItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Todo yang akan diubah: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membaca newline setelah input ID

        String sql = "SELECT * FROM todos WHERE id = ?";
        PreparedStatement selectStmt = conn.prepareStatement(sql);
        selectStmt.setInt(1, id);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            System.out.println("=== DETAIL TODO ===");
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Todo: " + rs.getString("todo"));
            System.out.println("Kategori: " + rs.getString("kategori"));
            System.out.println("Tanggal Selesai: " + rs.getDate("tanggal_selesai"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println();

            System.out.print("Masukkan Todo Baru: ");
            String newTodo = scanner.nextLine();
            System.out.print("Masukkan Kategori Baru: ");
            String newKategori = scanner.nextLine();
            System.out.print("Masukkan Tanggal Selesai Baru (yyyy-MM-dd): ");
            String newTanggalSelesaiStr = scanner.nextLine();
            Date newTanggalSelesai = Date.valueOf(newTanggalSelesaiStr);
            System.out.print("Masukkan Status Baru: ");
            String newStatus = scanner.nextLine();

            String updateSql = "UPDATE todos SET todo = ?, kategori = ?, tanggal_selesai = ?, status = ? WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newTodo);
            updateStmt.setString(2, newKategori);
            updateStmt.setDate(3, newTanggalSelesai);
            updateStmt.setString(4, newStatus);
            updateStmt.setInt(5, id);
            updateStmt.executeUpdate();

            System.out.println("Todo berhasil diubah.");
            updateStmt.close();
        } else {
            System.out.println("Todo dengan ID tersebut tidak ditemukan.");
        }

        rs.close();
        selectStmt.close();
    }

    private static void deleteTodoItem(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Masukkan ID Todo yang akan dihapus: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Membaca newline setelah input ID

        String sql = "SELECT * FROM todos WHERE id = ?";
        PreparedStatement selectStmt = conn.prepareStatement(sql);
        selectStmt.setInt(1, id);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            System.out.println("=== DETAIL TODO ===");
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Todo: " + rs.getString("todo"));
            System.out.println("Kategori: " + rs.getString("kategori"));
            System.out.println("Tanggal Selesai: " + rs.getDate("tanggal_selesai"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println();

            System.out.print("Apakah Anda yakin ingin menghapus todo ini? (y/n): ");
            String confirmation = scanner.nextLine();

            if (confirmation.equalsIgnoreCase("y")) {
                String deleteSql = "DELETE FROM todos WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                deleteStmt.setInt(1, id);
                deleteStmt.executeUpdate();

                System.out.println("Todo berhasil dihapus.");
                deleteStmt.close();
            } else {
                System.out.println("Penghapusan todo dibatalkan.");
            }
        } else {
            System.out.println("Todo dengan ID tersebut tidak ditemukan.");
        }

        rs.close();
        selectStmt.close();
    }
}