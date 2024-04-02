package it.govpay.stampe.test.utils;

public class Utils {

	public static String extractFilename(String headerValue) {
        String[] parts = headerValue.split("; ");
        String filenamePart = null;
        for (String part : parts) {
            if (part.startsWith("filename=")) {
                filenamePart = part;
                break;
            }
        }
        if (filenamePart != null) {
            String[] filenameParts = filenamePart.split("=");
            if (filenameParts.length == 2) {
                return filenameParts[1].replaceAll("\"", "");
            }
        }
        return null;
    }
}
