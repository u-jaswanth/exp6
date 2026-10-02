import java.sql.*;

public class App {

    static final String DB_URL = "jdbc:sqlite:student.db";

    public static void setupDatabase() throws SQLException {

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "marks INTEGER)"
            );

            stmt.execute("DELETE FROM students");

            stmt.execute("INSERT INTO students VALUES (1, 'Amit', 78)");
            stmt.execute("INSERT INTO students VALUES (2, 'Neha', 45)");
            stmt.execute("INSERT INTO students VALUES (3, 'Ravi', 92)");
        }
    }

    public static boolean customCheckpoint(
            int studentId, String expectedStatus) throws SQLException {

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT marks FROM students WHERE id = ?")) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println(
                    "FAIL: No record found for id " + studentId);
                return false;
            }

            int marks = rs.getInt("marks");

            String actualStatus =
                (marks >= 50) ? "Pass" : "Fail";

            if (actualStatus.equals(expectedStatus)) {

                System.out.println(
                    "PASS: Student " + studentId +
                    " status = " + actualStatus +
                    " (Custom check matched)");

                return true;

            } else {

                System.out.println(
                    "FAIL: Student " + studentId +
                    " expected " + expectedStatus +
                    ", got " + actualStatus);

                return false;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        setupDatabase();

        customCheckpoint(1, "Pass");
        customCheckpoint(2, "Fail");
        customCheckpoint(3, "Pass");
    }
}
