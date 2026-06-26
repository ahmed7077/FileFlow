package com.dsafileorganiser.cli;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public record CommandLineOptions(
        Path inputPath,
        Path outputZipPath,
        boolean removeCommentsForDuplicateCheck
) {
    public static CommandLineOptions parse(String[] args) {
        if (args.length == 0 || containsHelpFlag(args)) {
            throw new IllegalArgumentException("Missing required arguments.");
        }

        Map<String, String> values = new LinkedHashMap<>();
        boolean keepCommentsInDuplicateCheck = false;

        for (int index = 0; index < args.length; index++) {
            String current = args[index];
            if ("--keep-comments-in-hash".equals(current)) {
                keepCommentsInDuplicateCheck = true;
                continue;
            }

            if (!current.startsWith("--")) {
                throw new IllegalArgumentException("Unexpected argument: " + current);
            }

            if (index + 1 >= args.length || args[index + 1].startsWith("--")) {
                throw new IllegalArgumentException("Missing value for option: " + current);
            }

            values.put(current, args[++index]);
        }

        String inputValue = values.get("--input");
        if (inputValue == null || inputValue.isBlank()) {
            throw new IllegalArgumentException("The --input option is required.");
        }

        String outputValue = values.getOrDefault("--output", "organised-java-codes.zip");

        return new CommandLineOptions(
                Path.of(inputValue).toAbsolutePath().normalize(),
                Path.of(outputValue).toAbsolutePath().normalize(),
                !keepCommentsInDuplicateCheck
        );
    }

    public static void printUsage() {
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar dsa-file-organiser.jar --input <folder-or-zip> --output <result.zip>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --input <path>                  Folder or ZIP containing Java files.");
        System.out.println("  --output <path>                 ZIP file to create. Defaults to organised-java-codes.zip.");
        System.out.println("  --keep-comments-in-hash         Treat comment-only changes as unique code.");
    }

    private static boolean containsHelpFlag(String[] args) {
        for (String arg : args) {
            if ("--help".equals(arg) || "-h".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}
