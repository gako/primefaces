/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.owasp.esapi.SafeFile;
import org.owasp.esapi.errors.ValidationException;
import org.primefaces.component.fileupload.FileUpload;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.virusscan.VirusException;

/**
 * Utilities for FileUpload components.
 */
public class FileUploadUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUploadUtils.class.getName());

    private static final Pattern INVALID_FILENAME_PATTERN = Pattern.compile("([\\/:*?\"<>|])");

    private FileUploadUtils() {
    }

    public static String getValidFilename(String filename) {
        if (LangUtils.isValueBlank(filename)) {
            return null;
        }

        if (isSystemWindows()) {
            if (!filename.contains("\\\\")) {
                String[] parts = filename.substring(FilenameUtils.getPrefixLength(filename)).split(Pattern.quote(File.separator));
                for (String part : parts) {
                    if (INVALID_FILENAME_PATTERN.matcher(part).find()) {
                        throw new FacesException("Invalid filename: " + filename);
                    }
                }
            }
            else {
                throw new FacesException("Invalid filename: " + filename);
            }
        }

        String name = FilenameUtils.getName(filename);
        String extension = FilenameUtils.getExtension(filename);
        if (name.isEmpty() && extension.isEmpty()) {
            throw new FacesException("Filename can not be the empty string");
        }

        return name;
    }

    public static String getValidFilePath(String filePath) throws ValidationException {
        if (filePath == null || filePath.trim().equals("")) {
            throw new FacesException("Path can not be the empty string or null");
        }

        try {
            SafeFile file = new SafeFile(filePath);
            File parentFile = file.getParentFile();

            if (!file.exists()) {
                throw new ValidationException("Invalid directory", "Invalid directory, \"" + file + "\" does not exist.");
            }
            if (!parentFile.exists()) {
                throw new ValidationException("Invalid directory", "Invalid directory, specified parent does not exist.");
            }
            if (!parentFile.isDirectory()) {
                throw new ValidationException("Invalid directory", "Invalid directory, specified parent is not a directory.");
            }
            if (!file.getCanonicalPath().startsWith(parentFile.getCanonicalPath())) {
                throw new ValidationException("Invalid directory", "Invalid directory, \"" + file + "\" does not inside specified parent.");
            }

            if (!file.getCanonicalPath().equals(filePath)) {
                throw new ValidationException("Invalid directory", "Invalid directory name does not match the canonical path");
            }
        }
        catch (IOException ex) {
            throw new ValidationException("Invalid directory", "Failure to validate directory path");
        }

        return filePath;
    }

    public static boolean isSystemWindows() {
        return File.separatorChar == '\\';
    }

    /**
     * Check if an uploaded file meets all specifications regarding its filename and content type. It evaluates {@link FileUpload#getAllowTypes}
     * as well as {@link FileUpload#getAccept} and uses the installed {@link java.nio.file.spi.FileTypeDetector} implementation.
     * For most reliable content type checking it's recommended to plug in Apache Tika as an implementation.
     * @param fileUpload the fileUpload component
     * @param fileName the name of the uploaded file
     * @param inputStream the input stream to receive the file's content from
     * @return <code>true</code>, if all validations regarding filename and content type passed, <code>false</code> else
     */
    public static boolean isValidType(FileUpload fileUpload, String fileName, InputStream inputStream) {
        try {
            boolean validType = isValidFileName(fileUpload, fileName) && isValidFileContent(fileUpload, fileName, inputStream);
            if (validType) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("The uploaded file %s meets the filename and content type specifications", fileName));
                }
            }
            return validType;
        }
        catch (IOException ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, String.format("The type of the uploaded file %s could not be validated", fileName), ex);
            }
            return false;
        }
    }

    private static boolean isValidFileName(FileUpload fileUpload, String fileName) {
        String javascriptRegex = fileUpload.getAllowTypes();
        if (LangUtils.isValueBlank(javascriptRegex)) {
            return true;
        }

        String javaRegex = convertJavaScriptRegex(javascriptRegex);
        if (LangUtils.isValueBlank(javaRegex)) {
            return true;
        }

        fileName = EscapeUtils.forJavaScriptAttribute(fileName);

        final Pattern allowTypesPattern = Pattern.compile(javaRegex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        final Matcher fileNameMatcher = allowTypesPattern.matcher(fileName);
        boolean isValid = fileNameMatcher.find();
        if (!isValid) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.warning(String.format("The uploaded filename %s does not match the specified regex %s", fileName, javaRegex));
            }
            return false;
        }

        return true;
    }

    /**
     * Converts a JavaScript regular expression like '/(\.|\/)(gif|jpe?g|png)$/i'
     * to the Java usable format '(\\.|\\/)(gif|jpe?g|png)$'
     * @param jsRegex the client side JavaScript regex
     * @return the Java converted version of the regex
     */
    protected static String convertJavaScriptRegex(String jsRegex) {
        int start = 0;
        int end = jsRegex.length() - 1;
        if (jsRegex.charAt(0) == '/') {
            start = 1;
        }
        char endChar = jsRegex.charAt(end);
        if (endChar != '/' && endChar == 'i' || endChar == 'g') {
            end = end - 1;
        }
        return LangUtils.substring(jsRegex, start, end);
    }

    private static boolean isValidFileContent(FileUpload fileUpload, String fileName, InputStream inputStream) throws IOException {
        if (!fileUpload.isValidateContentType()) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Content type checking is disabled");
            }
            return true;
        }
        if (LangUtils.isValueBlank(fileUpload.getAccept())) {
            //Short circuit
            return true;
        }

        boolean tika = LangUtils.tryToLoadClassForName("org.apache.tika.filetypedetector.TikaFileTypeDetector") != null;
        if (!tika && LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning("Could not find Apache Tika in classpath which is recommended for reliable content type checking");
        }

        //If Tika is in place, we drop the original file extension to avoid short circuit detection by just looking at the file extension
        String tempFileSuffix = tika ? null : "." + FilenameUtils.getExtension(fileName);
        String tempFilePrefix = UUID.randomUUID().toString();
        Path tempFile = Files.createTempFile(tempFilePrefix, tempFileSuffix);
        try {
            InputStream in = new PushbackInputStream(new BufferedInputStream(inputStream));
            try (OutputStream out = new FileOutputStream(tempFile.toFile())) {
                IOUtils.copyLarge(in, out);
            }
            String contentType = Files.probeContentType(tempFile);
            if (contentType == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.warning(String.format("Could not determine content type of uploaded file %s, consider plugging in an adequate " +
                            "FileTypeDetector implementation", fileName));
                }
                return false;
            }
            //Comma-separated values: file_extension|audio/*|video/*|image/*|media_type (see https://www.w3schools.com/tags/att_input_accept.asp)
            String[] accepts = fileUpload.getAccept().split(",");
            boolean accepted = false;
            for (String accept : accepts) {
                accept = accept.trim().toLowerCase();
                if (accept.startsWith(".") && fileName.toLowerCase().endsWith(accept)) {
                    accepted = true;
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("The file extension %s of the uploaded file %s is accepted", accept, fileName));
                    }
                    break;
                }
                //Now we have a media type that may contain wildcards
                if (FilenameUtils.wildcardMatch(contentType.toLowerCase(), accept)) {
                    accepted = true;
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("The content type %s of the uploaded file %s is accepted by %s", contentType, fileName, accept));
                    }
                    break;
                }
            }
            if (!accepted) {
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("The uploaded file %s with content type %s does not match the accept specification %s", fileName, contentType,
                            fileUpload.getAccept()));
                }
                return false;
            }
        }
        finally {
            try {
                Files.delete(tempFile);
            }
            catch (Exception ex) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, String.format("Could not delete temporary file %s, will try to delete on JVM exit",
                            tempFile.toAbsolutePath()), ex);
                    try {
                        tempFile.toFile().deleteOnExit();
                    }
                    catch (Exception ex1) {
                        if (LOGGER.isLoggable(Level.WARNING)) {
                            LOGGER.log(Level.WARNING, String.format("Could not register temporary file %s for deletion on JVM exit",
                                    tempFile.toAbsolutePath()), ex1);
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void performVirusScan(FacesContext facesContext, FileUpload fileUpload, InputStream inputStream) throws VirusException {
        if (fileUpload.isPerformVirusScan()) {
            PrimeApplicationContext.getCurrentInstance(facesContext).getVirusScannerService().performVirusScan(inputStream);
        }
    }

    /**
     * OWASP prevent directory path traversal of "../../image.png".
     *
     * @see https://www.owasp.org/index.php/Path_Traversal
     * @param relativePath the relative path to check for path traversal
     * @return the relative path
     * @throws FacesException if any error is detected
     */
    public static String checkPathTraversal(String relativePath) {
        File file = new File(relativePath);

        if (file.isAbsolute()) {
            throw new FacesException("Path traversal attempt - absolute path not allowed.");
        }

        try  {
            String pathUsingCanonical = file.getCanonicalPath();
            String pathUsingAbsolute = file.getAbsolutePath();

            // Require the absolute path and canonicalized path match.
            // This is done to avoid directory traversal
            // attacks, e.g. "1/../2/"
            if (!pathUsingCanonical.equals(pathUsingAbsolute))  {
                throw new FacesException("Path traversal attempt for path " + relativePath);
            }
        }
        catch (IOException ex) {
            throw new FacesException("Path traversal - unexpected exception.", ex);
        }
        return relativePath;
    }
}