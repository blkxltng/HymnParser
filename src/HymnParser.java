import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by firej on 9/13/2016.
 */
public class HymnParser {


    public static void main(String args[]) {
        List<String> list = new ArrayList<String>();
        File file = new File("hymns/S001_cen.txt");
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
        int verseNum = 0;
        int refrainNum = -1;

        Pattern p = Pattern.compile(REGEX_number);
        Pattern p_verseNum = Pattern.compile(REGEX_verseNum);
        Pattern p_verseRefrain = Pattern.compile(REGEX_verseRefrain);
        Pattern p_verseText = Pattern.compile(REGEX_verseText);

        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while ((text = reader.readLine()) != null) {
                //If you find an empty line
                if(text.trim().isEmpty()) {
                    //State so
                    if(foundRefrain)
                        refrain.add(refrainNum, verseText.trim());
                    if(foundVerse)
                        verse.add(verseNum, verseText.trim());

                    foundRefrain = false;
                    foundVerse = false;
                    verseText = "";
                    continue;
                }

                //If the line isn't empty, read it
                else {
                    text = text.trim();

                    Matcher m = p.matcher(text); // get a matcher object
                    if(m.find()){
                        System.out.println("Found Number = " + m.group(1));
                        System.out.println("Found Title = " + m.group(2));
                        continue;
                    }

                    Matcher m_verseNum = p_verseNum.matcher(text);
                    if(m_verseNum.find()){
                        System.out.println("Found Verse Number = " + m_verseNum.group(0));
                        foundVerse = true;
                        verseNum = Integer.parseInt(m_verseNum.group(0)) - 1;
                        continue;
                    }

                    Matcher m_verseRefrain = p_verseRefrain.matcher(text);
                    if(m_verseRefrain.find()) {
                        System.out.println("Found Refrain = " + m_verseRefrain.group());
                        foundRefrain = true;
                        refrainNum++;
                        continue;
                    }

                    Matcher m_verseText = p_verseText.matcher(text);
                    if(m_verseText.find()) {
                        System.out.println("Found Verse Text = " + m_verseText.group(1));
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
                refrain.add(refrainNum, verseText.trim());
            if(foundVerse)
                verse.add(verseNum, verseText.trim());

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

        System.out.println();
        System.out.println();

        //Print out the list
        for(int i = 0; i < verse.size(); i++)
        {
            System.out.println(verse.get(i));
            System.out.println();
        }

        if(refrain.size() != 0)
            System.out.println("Refrain:\n" + refrain.get(0));
    }
}
