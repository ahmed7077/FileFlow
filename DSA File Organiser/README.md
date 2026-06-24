# DSA File Organiser

This project is a Java code organiser with both a web UI and CLI mode. It scans a folder or ZIP of `.java` files, removes duplicate source files, organises the remaining files by package and class name, and returns a clean ZIP.

## What It Does

- Accepts a folder or `.zip` file as input.
- Finds all `.java` files inside it.
- Normalises code before duplicate checks.
- Removes duplicate files using SHA-256 hashes.
- Ignores whitespace and comments by default when checking duplicates.
- Organises files by Java package name.
- Creates a downloadable ZIP.
- Adds an `organisation-report.txt` file inside the ZIP.

## Project Structure

```text
src/main/java/com/dsafileorganiser/
  DsaFileOrganiserApplication.java
  cli/
    CommandLineOptions.java
  duplicate/
    DuplicateCodeDetector.java
    DuplicateDetectionResult.java
    DuplicateJavaFile.java
  hash/
    Sha256HashGenerator.java
  normalise/
    JavaCodeNormalizer.java
  organiser/
    JavaCodeOrganiser.java
    OrganisationSummary.java
  output/
    OrganisedCodeWriter.java
    OrganisedJavaFile.java
  report/
    OrganisationReportWriter.java
  source/
    JavaSourceFile.java
    JavaSourceMetadata.java
    JavaSourceMetadataExtractor.java
    JavaSourceScanner.java
  zip/
    SafeZipExtractor.java
    ZipArchiveService.java
  web/
    DsaFileOrganiserWebServer.java
    FileOrganiseHttpHandler.java
    MultipartFormDataParser.java
    StaticFileHttpHandler.java
src/main/resources/static/
  index.html
  styles.css
  app.js
```

## Run the Web UI

Recommended Maven Wrapper command:

```bash
./mvnw exec:java
```

On Windows PowerShell:

```powershell
.\mvnw.cmd exec:java
```

The wrapper downloads Maven into `.mvn/` the first time, so Maven does not need to be installed globally.

If you want to run without Maven, compile the app manually:

```bash
javac -d target/classes src/main/java/com/dsafileorganiser/**/*.java src/main/java/com/dsafileorganiser/*.java
```

Start the web server:

```bash
java -cp target/classes com.dsafileorganiser.DsaFileOrganiserApplication
```

Then open:

```text
http://localhost:8080
```

Upload a `.zip` file containing Java files. The browser will download `organised-java-codes.zip` after processing.

## Run the CLI

With Maven Wrapper:

```bash
./mvnw package
java -jar target/dsa-file-organiser-0.1.0.jar --input path/to/java-codes.zip --output organised-java-codes.zip
```

Without Maven:

```bash
java -cp target/classes com.dsafileorganiser.DsaFileOrganiserApplication --input path/to/java-codes.zip --output organised-java-codes.zip
```

You can also pass a folder:

```bash
java -cp target/classes com.dsafileorganiser.DsaFileOrganiserApplication --input path/to/java-folder --output organised-java-codes.zip
```

## Options

```text
--input <path>                  Folder or ZIP containing Java files.
--output <path>                 ZIP file to create. Defaults to organised-java-codes.zip.
--keep-comments-in-hash         Treat comment-only changes as unique code.
```

## File Type Support

The current version is Java-focused. It only scans `.java` files, extracts Java package/class names, and organises output using Java naming rules.

The architecture can support more file types later by adding language-specific scanners and normalisers. For example:

- Python: scan `.py`, normalise comments/spacing, organise by folder or detected functions/classes.
- C/C++: scan `.c`, `.cpp`, `.h`, `.hpp`, organise by filename or include structure.
- JavaScript/TypeScript: scan `.js`, `.ts`, `.jsx`, `.tsx`, organise by exports or project folder.
- Plain text notes: scan `.txt` or `.md`, remove duplicate notes by content hash.

## Future Ideas

1. Add JavaParser-based duplicate detection for renamed but logically identical Java solutions.
2. Group DSA files by topic, such as arrays, trees, graphs, dynamic programming, and strings.
3. Show a preview report in the browser before downloading.
4. Support multiple uploaded ZIPs in one run.
5. Add tests for duplicate detection, ZIP safety, and folder organisation.
6. Add support for other languages by introducing file-type-specific scanners and normalisers.
