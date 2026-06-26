package com.fileflow;

import com.fileflow.cli.CommandLineOptions;
import com.fileflow.organiser.JavaCodeOrganiser;
import com.fileflow.organiser.OrganisationSummary;
import com.fileflow.web.fileflowWebServer;

public final class FileFlowApplication {
    private FileFlowApplication() {
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
            FileFlowWebServer webServer = new FileFlowWebServer(8080);
            webServer.start();
            System.out.println("FileFlow web UI is running.");
            System.out.println("Open http://localhost:8080 in your browser.");
        } catch (Exception exception) {
            System.err.println("Failed to start web server: " + exception.getMessage());
            exception.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
