package DST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * Methoden zum Finden von Duplikaten Dateien auf verschiedenen Levels
 *
 * @author simon
 */
public class Duplicate {

    /**
     * Verzeichniss
     */
    private final File dir;

    public Duplicate(File directory) {
        dir = directory;
    }

    /**
     * gibt die Ergebnisse von findDuplicates in einen String Arraay
     *
     * @param duplicates Duplikate
     */
    public static String[] toStringArray(Path[] duplicates) {
        ArrayList<String> formated = new ArrayList<>();
        for (Path p : duplicates) {
            formated.add(String.valueOf(p));
        }
        return formated.toArray(new String[0]);
    }

    /**
     * findet Duplikate Dateien anhand deren Inhalt in dir und Unterverzeichnissen
     *
     * @param searchLevel NAME für Vergleich nach Dateiname, CONTENT für Vergleich nach Dateiinhalt, BOTH für beides
     * @return Array mit Pfaden zu allen doppelten Dateien
     * @throws IOException IOException
     */
    public Path[] findDuplicates(Level searchLevel) throws IOException {
        ArrayList<File> duplicates = new ArrayList<>(); //Duplikate
        ArrayList<File> allFiles = new ArrayList<>(); //dir Inhalt + Inhalt aller Unterverzeichnisse

        if (dir.listFiles() != null) {
            Files.walk(Paths.get(dir.getPath())).filter(Files::isRegularFile).forEach(f -> allFiles.add(f.toFile()));

            ArrayList<File> allFiles2 = new ArrayList<>(); // zweite Liste aller Files zum Vergleichen der Duplikate
            for (File file : allFiles) { // allFiles durchgehen und doppelte in duplicates geben
                for (File f : allFiles2) {
                    switch (searchLevel) {
                        case NAME://nur name
                            if (f.getName().equals(file.getName())) duplicates.addAll(Arrays.asList(file, f));
                            break;
                        case CONTENT://nur content
                            if (compareFileContent(file, f)) duplicates.addAll(Arrays.asList(file, f));
                            break;
                        case BOTH://beides
                            if (f.getName().equals(file.getName()) && compareFileContent(file, f)) {
                                duplicates.addAll(Arrays.asList(file, f));
                            }
                            break;
                        default:
                            return null;
                    }
                }
                allFiles2.add(file);
            }

            Path[] paths = new Path[duplicates.size()];
            for (int i = 0; i < duplicates.size(); i++) {
                paths[i] = Path.of(duplicates.get(i).getPath());
            }

            return paths;
        } else
            return null;
    }

    /**
     * vergleicht den Dateiinhalt
     *
     * @param file1 erste zu vergleichende Datei
     * @param file2 zweite zu vergleichende Datei
     * @return boolean: sind die Dateien gleich
     * @throws IOException throws IOException
     */
    private boolean compareFileContent(File file1, File file2) throws IOException {
        if (file1.length() == file2.length()) return true;
        InputStream in1 = Files.newInputStream(Path.of(file1.getPath()));
        CRC32 crc1 = new CRC32();
        byte[] buffer1 = new byte[1024];
        int bytesRead1;
        while ((bytesRead1 = in1.read(buffer1)) != -1) {
            crc1.update(buffer1, 0, bytesRead1);
        }
        InputStream in2 = Files.newInputStream(Path.of(file2.getPath()));
        CRC32 crc2 = new CRC32();
        byte[] buffer2 = new byte[1024];
        int bytesRead2;
        while ((bytesRead2 = in2.read(buffer2)) != -1) {
            crc2.update(buffer2, 0, bytesRead2);
        }
        return crc1.getValue() == crc2.getValue();
    }

    /**
     * Comparison Level
     */
    public enum Level {
        NAME,
        CONTENT,
        BOTH
    }
}
