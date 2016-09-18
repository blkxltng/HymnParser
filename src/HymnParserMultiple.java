import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by firej on 9/15/2016.
 */
public class HymnParserMultiple {

    public static String DB_NAME = "hymns.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NAME = "Hymns";

    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "Title";
    public static final String COL_NUMBER = "Number";
    public static final String COL_SECTION = "Section";
    public static final String COL_SUBSECTION = "SubSection";
    public static final String COL_FIRSTHYMN = "FirstHymn";
    public static final String COL_LASTHYMN = "LastHymn";
    public static final String COL_REFRAIN = "Refrain";
    public static final String COL_REFRAIN2 = "'Refrain 2'";
    public static final String COL_VERSE = "Verse";
    public static final String COL_VERSE1 = "'Verse 1'";
    public static final String COL_VERSE2 = "'Verse 2'";
    public static final String COL_VERSE3 = "'Verse 3'";
    public static final String COL_VERSE4 = "'Verse 4'";
    public static final String COL_VERSE5 = "'Verse 5'";
    public static final String COL_VERSE6 = "'Verse 6'";
    public static final String COL_VERSE7 = "'Verse 7'";

    static int hymnCount = 1;
    public static int arraySection[] = {70, 74, 115, 257, 271, 279, 344, 380, 438, 455, 650, 660, 696};
    public static int arraySubSection[] = {39, 46, 59, 64, 70, 74, 82, 92, 99, 105, 115, 118, 144, 154, 165, 177, 181,
            200, 222, 228, 257, 271, 279, 291, 297, 301, 332, 334, 344, 355, 376, 377, 379, 380, 396, 412, 413, 415, 418,
            420, 438, 455, 461, 472, 478, 506, 536, 556, 567, 571, 585, 590, 592, 606, 620, 634, 642, 645, 650, 656, 660,
            696};

    public static void main(String args[]) {
        List<String> list = new ArrayList<String>();
        File folder = new File("hymns/");
        //File file = new File("hymns/S001_cen.txt");
        BufferedReader reader = null;
        final String REGEX_number = "(\\d{3}) - (.+)";
        final String REGEX_verseNum = "(\\d)$";
        final String REGEX_verseRefrain = "Refrain";
        final String REGEX_verseText = "^\\s*(\\D+)\\s*";

        boolean foundRefrain = false;
        boolean foundVerse = false;

        ArrayList<String> verse = new ArrayList<>();
        ArrayList<String> refrain = new ArrayList<>();
        String verseText = "";

        Pattern p = Pattern.compile(REGEX_number);
        Pattern p_verseNum = Pattern.compile(REGEX_verseNum);
        Pattern p_verseRefrain = Pattern.compile(REGEX_verseRefrain);
        Pattern p_verseText = Pattern.compile(REGEX_verseText);

        createDatabase(DB_NAME);
        createTable(DB_NAME);

        try {

            for(File file : folder.listFiles()) {
                reader = new BufferedReader(new FileReader(file));
                String text = null;
                int hymnNumber = 0;
                String hymnTitle = "";
                int verseNum = 0;
                int refrainNum = 0;
//                refrain = new ArrayList<>();
//                verse = new ArrayList<>();
                refrain.add(0, null);
                refrain.add(1, null);
                verse.add(0, null);
                verse.add(1, null);
                verse.add(2, null);
                verse.add(3, null);
                verse.add(4, null);
                verse.add(5, null);
                verse.add(6, null);
                foundRefrain = false;
                foundVerse = false;

                while ((text = reader.readLine()) != null) {
                    //If you find an empty line
                    if(text.trim().isEmpty()) {
                        //State so
                        if(foundRefrain) {
                            refrain.add(refrainNum, "'" + verseText.trim() + "'");
                            refrainNum++;
                        }

                        if(foundVerse)
                            verse.add(verseNum, "'" + verseText.trim() + "'");

                        foundRefrain = false;
                        foundVerse = false;
                        verseText = "";
                        continue;
                    }

                    //If the line isn't empty, read it
                    else {
                        text = text.trim();
                        text = text.replace("�", "'");
                        text = text.replace("'", "''");

                        Matcher m = p.matcher(text); // get a matcher object
                        if(m.find()){
//                            System.out.println("Found Number = " + m.group(1));
                            hymnNumber = Integer.parseInt(m.group(1));
//                            System.out.println("Found Title = " + m.group(2));
                            hymnTitle = "'" + m.group(2) + "'";
                            continue;
                        }

                        Matcher m_verseNum = p_verseNum.matcher(text);
                        if(m_verseNum.find()){
//                            System.out.println("Found Verse Number = " + m_verseNum.group(0));
                            foundVerse = true;
                            verseNum = Integer.parseInt(m_verseNum.group(0)) - 1;
                            continue;
                        }

                        Matcher m_verseRefrain = p_verseRefrain.matcher(text);
                        if(m_verseRefrain.find()) {
//                            System.out.println("Found Refrain = " + m_verseRefrain.group());
                            foundRefrain = true;
                            continue;
                        }

                        Matcher m_verseText = p_verseText.matcher(text);
                        if(m_verseText.find()) {
//                            System.out.println("Found Verse Text = " + m_verseText.group(1));
                            if(foundVerse) {
                                verseText += (text + "\n");
                            }
                            else if(foundRefrain) {
                                verseText += (text + "\n");
                            }
                        }

                        list.add(text.trim()); //Trim any unnecessary spaces
                    }
                }

                if(foundRefrain)
                    refrain.add(refrainNum, "'" + verseText.trim() + "'");
                if(foundVerse)
                    verse.add(verseNum, "'" + verseText.trim() + "'");

                addHymn(DB_NAME, hymnNumber, hymnTitle, refrain.get(0), refrain.get(1), verse.get(0), verse.get(1),
                        verse.get(2), verse.get(3), verse.get(4), verse.get(5), verse.get(6));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }

//        System.out.println();
//        System.out.println();
//
//        //Print out the list
//        for(int i = 0; i < verse.size(); i++)
//        {
//            System.out.println(verse.get(i));
//            System.out.println();
//        }
//
//        if(refrain.size() != 0)
//            System.out.println("Refrain:\n" + refrain.get(0));
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
                    " " + COL_NUMBER + " INT, " +
                    " " + COL_TITLE + " TEXT, " +
                    " " + COL_REFRAIN + " TEXT, " +
                    " " + COL_REFRAIN2 + " TEXT, " +
                    " " + COL_VERSE1 + " TEXT, " +
                    " " + COL_VERSE2 + " TEXT, " +
                    " " + COL_VERSE3 + " TEXT, " +
                    " " + COL_VERSE4 + " TEXT, " +
                    " " + COL_VERSE5 + " TEXT, " +
                    " " + COL_VERSE6 + " TEXT, " +
                    " " + COL_VERSE7 + " TEXT, " +
                    " " + COL_SECTION + " INT, " +
                    " " + COL_SUBSECTION + " INT)";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
    }

    private static void addHymn(String fileName, int hymnNumber, String hymnTitle, String hymnRefrain, String hymnRefrain2,
                                String hymnVerse1, String hymnVerse2, String hymnVerse3, String hymnVerse4, String hymnVerse5,
                                String hymnVerse6, String hymnVerse7) {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + fileName);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            int section = getSection(hymnCount);
            int subSection = getSubSection(hymnCount);

            stmt = c.createStatement();
            String sql = "INSERT INTO " + TABLE_NAME +
                    " (" + COL_ID + "," + COL_NUMBER + "," + COL_TITLE + "," + COL_REFRAIN + "," + COL_REFRAIN2 + "," +
                    COL_VERSE1 + "," + COL_VERSE2 + "," + COL_VERSE3 + "," + COL_VERSE4 + "," + COL_VERSE5 + "," + COL_VERSE6 + "," +
                    COL_VERSE7 + "," + COL_SECTION + "," + COL_SUBSECTION + ") " +
                    "VALUES (" + hymnNumber + "," + hymnNumber + "," + hymnTitle + "," + hymnRefrain + "," + hymnRefrain2 + "," +
                    hymnVerse1 + "," + hymnVerse2 + "," + hymnVerse3 + "," + hymnVerse4 + "," + hymnVerse5 + "," + hymnVerse6 + "," +
                    hymnVerse7 + "," + section + "," + subSection + ");";
            System.out.println(sql);
            hymnCount++;
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

    private static int getSection(int hymnNum) {
        for(int i = 0; i < arraySection.length; i++) {
            if(hymnNum < arraySection[i])
                return i+1;
        }

        return 0;
    }

    private static int getSubSection(int hymnNum) {
        for(int i = 0; i < arraySubSection.length; i++) {
            if(hymnNum < arraySubSection[i])
                return i+1;
        }

        return 0;
    }
}
