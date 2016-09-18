import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by firej on 9/15/2016.
 */
public class SectionMaker {

    public static String DB_NAME = "hymns.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "Sections";

    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "Title";
    public static final String COL_FIRSTHYMN = "FirstHymn";
    public static final String COL_LASTHYMN = "LastHymn";

    public static String arrayTitles[] = {"Worship", "Trinity", "God the Father", "Jesus Christ", "Holy Spirit",
            "Holy Scriptures", "Gospel", "Christian Church", "Doctrines", "Early Advent", "Christian Life",
            "Christian Home", "Sentences and Responses", "Worship Aids"};
    public static int arraySection[] = {70, 74, 115, 257, 271, 279, 344, 380, 438, 455, 650, 660, 696, 921};

    public static void main(String args[]) {
        createDatabase(DB_NAME);
        createTable(DB_NAME);
        for(int i=0; i < arraySection.length; i++) {
            String title = "'" + arrayTitles[i] + "'";
            int first = getFirst(i);
            int last = getLast(i);
            addSection(DB_NAME, i+1, title, first, last);
        }
    }

    private static void createDatabase(String fileName) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
        } catch ( Exception e ) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    private static void createTable(String fileName) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE " + TABLE_NAME +  " " +
                    "(" + COL_ID +  " INT PRIMARY KEY NOT NULL," +
                    " " + COL_TITLE + " TEXT, " +
                    " " + COL_FIRSTHYMN + " INT, " +
                    " " + COL_LASTHYMN + " INT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    private static void addSection(String fileName, int id, String title, int first, int last) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO " + TABLE_NAME +
                    " (" + COL_ID + "," + COL_TITLE + "," + COL_FIRSTHYMN + "," + COL_LASTHYMN + ") " +
                    "VALUES (" + id + "," + title + "," + first + "," + last + ");";
            System.out.println(sql);
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }

    private static int getFirst(int num) {
        if(num == 0)
            return 1;
        else
            return (arraySection[num-1]);
    }

    private static int getLast(int num) {
        return arraySection[num]-1;
    }
}
