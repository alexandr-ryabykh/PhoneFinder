import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PhoneFinder {

    public static void main(String[] args) {
        System.out.println("\nEnter the path to the folder:");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            PhoneFinder phoneFinder = new PhoneFinder();
            try {
                ArrayList<String> content = phoneFinder.processAllDirectories(path);
                ArrayList<String> allNumbers = phoneFinder.getAllPhoneNumbers(content);
                phoneFinder.formatPhoneNumbers(allNumbers);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Please, provide directory path");
        }
    }

    private ArrayList<String> processAllDirectories(String path) throws IOException {
        List<File> fileList = Files.walk(Paths.get(path)).filter(path1 -> path1.toString().endsWith(".txt")).map(Path::toFile)
                .collect(Collectors.toList());
        ArrayList<String> allPhoneNumbers = new ArrayList<>();
        fileList.forEach(eachFile -> {
            try {
                allPhoneNumbers.addAll(Files.readAllLines(Paths.get(eachFile.getAbsolutePath())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return allPhoneNumbers;
    }

    private ArrayList<String> getAllPhoneNumbers(ArrayList<String> contentArrayList) {
        contentArrayList.replaceAll(e -> e.replaceAll("[^\\d+\\-\\s]", "").trim());
        ArrayList<String> phoneNumbers = new ArrayList<>();
        for (String string : contentArrayList) {
            Pattern pattern = Pattern.compile("((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,15}");
            Matcher matcher = pattern.matcher(string);
            if (matcher.lookingAt()) {
                while (matcher.find()) {
                    MatchResult matchResult = matcher.toMatchResult();
                    String firstPhoneNumber = string.substring(0, matchResult.start());
                    String otherDetectedPhoneNumbers = string.substring(matchResult.start(), matchResult.end());
                    phoneNumbers.add(firstPhoneNumber);
                    phoneNumbers.add(otherDetectedPhoneNumbers);
                }
                phoneNumbers.add(string);
            }
        }
        phoneNumbers.replaceAll(e -> e.replaceAll("[\\s\\-]", ""));
        return phoneNumbers;
    }

    private void formatPhoneNumbers(ArrayList<String> phoneNumbers) {
        Set<String> formattedPhoneNumbers = new TreeSet<>();
        for (String phoneNumber : phoneNumbers)
            if (phoneNumber != null && phoneNumber.length() == 12) {
                String formattedFullNumber = phoneNumber.replaceFirst("(.\\d{1})(\\d{3})(\\d{3})(\\d{4})", "$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumber != null && phoneNumber.length() == 11) {
                String formattedFullNumber = phoneNumber.replaceFirst("(\\d{1})(\\d{3})(\\d{3})(\\d{4})", "+$1 ($2) $3-$4");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumber != null && phoneNumber.length() == 10) {
                String formattedFullNumber = phoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "+7 ($1) $2-$3");
                formattedPhoneNumbers.add(formattedFullNumber);
            } else if (phoneNumber != null && phoneNumber.length() == 7) {
                String formattedFullNumber = phoneNumber.replaceFirst("(\\d{3})(\\d{4})", "+7 (812) $1-$2");
                formattedPhoneNumbers.add(formattedFullNumber);
            }
        formattedPhoneNumbers.forEach(System.out::println);
    }
}