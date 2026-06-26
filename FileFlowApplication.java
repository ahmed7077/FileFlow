package com.dsafileorganiser;

import com.dsafileorganiser.cli.CommandLineOptions;
import com.dsafileorganiser.organiser.JavaCodeOrganiser;
import com.dsafileorganiser.organiser.OrganisationSummary;
import com.dsafileorganiser.web.DsaFileOrganiserWebServer;

public class FileFlowApplication {
    private DsaFileOrganiserApplication() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            startWebServer();
            return;
        }

        try {
            CommandLineOptions options = CommandLineOptions.parse(args);
            OrganisationSummary summary = new JavaCodeOrganiser().organise(
                    options.inputPath(),
                    options.outputZipPath(),
                    options.removeCommentsForDuplicateCheck()
            );

            System.out.println("Organisation complete.");
            System.out.println("Java files scanned: " + summary.scannedFileCount());
            System.out.println("Unique files kept: " + summary.uniqueFileCount());
            System.out.println("Duplicate files removed: " + summary.duplicateFileCount());
            System.out.println("Downloadable ZIP: " + summary.outputZipPath());
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            CommandLineOptions.printUsage();
            System.exit(1);
        } catch (Exception exception) {
            System.err.println("Failed to organise Java files: " + exception.getMessage());
            exception.printStackTrace(System.err);
            System.exit(2);
        }
    }

    private static void startWebServer() {
        try {
            DsaFileOrganiserWebServer webServer = new DsaFileOrganiserWebServer(8080);
            webServer.start();
            System.out.println("DSA File Organiser web UI is running.");
            System.out.println("Open http://localhost:8080 in your browser.");
        } catch (Exception exception) {
            System.err.println("Failed to start web server: " + exception.getMessage());
            exception.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
