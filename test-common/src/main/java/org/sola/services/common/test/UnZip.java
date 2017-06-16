package org.sola.services.common.test;


/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java 
 * language and environment is gratefully acknowledged.
 * 
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * UnZip -- print or unzip a JAR or PKZIP file using java.util.zip. Command-line
 * version: extracts files.
 * 
 * @author Ian Darwin, Ian@DarwinSys.com $Id: UnZip.java,v 1.7 2004/03/07
 *         17:40:35 ian Exp $
 *  
 *         Modified by soladev to extract a Jar file to a specified location
 *         2011/05/15
 */
public class UnZip {

    /** Constants for mode listing or mode extracting. */
    public static final int LIST = 0, EXTRACT = 1;
    /** Whether we are extracting or just printing TOC */
    protected int mode = EXTRACT;
    /** The ZipFile that is used to read an archive */
    protected ZipFile zippy;
    /** The buffer for reading/writing the ZipFile data */
    protected byte[] b;

    /**
     * Simple main program, construct an UnZipper, process each .ZIP file from
     * argv[] through that object.
     */
//  public static void main(String[] argv) {
//    UnZip u = new UnZip();
//
//    for (int i = 0; i < argv.length; i++) {
//      if ("-x".equals(argv[i])) {
//        u.setMode(EXTRACT);
//        continue;
//      }
//      String candidate = argv[i];
//      // System.err.println("Trying path " + candidate);
//      if (candidate.endsWith(".zip") || candidate.endsWith(".jar"))
//        u.unZip(candidate, "");
//      else
//        System.err.println("Not a zip file? " + candidate);
//    }
//    System.err.println("All done!");
//  }
    /** Construct an UnZip object. Just allocate the buffer */
    UnZip() {
        b = new byte[8092];

    }

    /** Set the Mode (list, extract). */
    protected void setMode(int m) {
        if (m == LIST || m == EXTRACT) {
            mode = m;
        }
    }
    /** Cache of paths we've mkdir()ed. */
    protected SortedSet dirsMade;

    /** For a given Zip file, process each entry. */
    public void unZip(String fileName, String outputPath) {
        dirsMade = new TreeSet();
        try {
            zippy = new ZipFile(fileName);
            Enumeration all = zippy.entries();
            while (all.hasMoreElements()) {
                getFile((ZipEntry) all.nextElement(), outputPath);
            }
        } catch (IOException err) {
            System.err.println("IO Error: " + err);
            return;
        }
    }
    protected boolean warnedMkDir = false;

    /**
     * Process one file from the zip, given its name. Either print the name, or
     * create the file on disk.
     */
    public void getFile(ZipEntry e, String outputPath) throws IOException {
        String zipName = e.getName();
        switch (mode) {
            case EXTRACT:
                if (zipName.startsWith("/")) {
                    if (!warnedMkDir) {
                        System.out.println("Ignoring absolute paths");
                    }
                    warnedMkDir = true;
                    zipName = zipName.substring(1);

                }
                zipName = outputPath + zipName;
                System.out.println("zipName " + zipName);
                // if a directory, just return. We mkdir for every file,
                // since some widely-used Zip creators don't put out
                // any directory entries, or put them in the wrong place.
                // solaDev - make this check with e.isDirectory
                //      if (zipName.endsWith("/")) {
                //        return;
                //      }
                if (e.isDirectory()) {
                    return;
                }
                // Else must be a file; open the file for output
                // Get the directory part.
                int ix = zipName.lastIndexOf('/');
                if (ix > 0) {
                    String dirName = zipName.substring(0, ix);
                    if (!dirsMade.contains(dirName)) {
                        File d = new File(dirName);
                        // If it already exists as a dir, don't do anything
                        if (!(d.exists() && d.isDirectory())) {
                            // Try to create the directory, warn if it fails
                            System.out.println("Creating Directory: " + dirName);
                            if (!d.mkdirs()) {
                                System.err.println("Warning: unable to mkdir "
                                        + dirName);
                            }
                            dirsMade.add(dirName);
                        }
                    }
                }
                System.err.println("Creating " + zipName);
                FileOutputStream os = new FileOutputStream(zipName);
                InputStream is = zippy.getInputStream(e);
                int n = 0;
                while ((n = is.read(b)) > 0) {
                    os.write(b, 0, n);
                }
                is.close();
                os.close();
                break;
            case LIST:
                // Not extracting, just list
                if (e.isDirectory()) {
                    System.out.println("Directory " + zipName);
                } else {
                    System.out.println("File " + zipName);
                }
                break;
            default:
                throw new IllegalStateException("mode value (" + mode + ") bad");
        }
    }
}