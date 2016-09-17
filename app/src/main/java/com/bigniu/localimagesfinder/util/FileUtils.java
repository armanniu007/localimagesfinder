/*
 * Copyright (C) 2013  WhiteCat 白猫 (www.thinkandroid.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bigniu.localimagesfinder.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * @author bigniu
 * @version V1.0
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    public static boolean deleteFileAndChildren(File pFile, @Nullable FilenameFilter pFilter) {
        if (pFile.isFile()) {
            return pFile.delete();
        } else {
            String[] filenames = pFilter == null ? pFile.list() : pFile.list(pFilter);
            if (filenames != null && filenames.length > 0) {
                for (String filename : filenames) {
                    if (!deleteFileAndChildren(new File(pFile, filename), pFilter)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static File getRootFile(Context context) throws Exception {
        try {
            File file = Environment.getExternalStorageDirectory();
            file = new File(file, context.getPackageName());
            if (file.exists() || file.mkdir()) {
                return file;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        return null;
    }


    public static File getFile(Context context, String... names) {

        try {
            File file = getRootFile(context);
            if (null != file) {
                return getFile(file, names);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Construct a file from the set of name elements.
     *
     * @param directory the parent directory
     * @param names     the name elements
     * @return the file
     * @since 2.1
     */
    public static File getFile(File directory, String... names) {
        if (directory == null || names == null || names.length == 0) {
            throw new IllegalArgumentException();
        }
        File file = directory;
        for (String name : names) {
            file = new File(file, name);
        }
        return file;
    }


    /**
     * Makes a directory, including any necessary but nonexistent parent
     * directories. If a file already exists with specified name but it is
     * not a directory then an IOException is thrown.
     * If the directory cannot be created (or does not already exist)
     * then an IOException is thrown.
     *
     * @param directory directory to create, must not be {@code null}
     * @throws NullPointerException if the directory is {@code null}
     * @throws IOException          if the directory cannot be created or the file already exists but is not a directory
     */
    public static void forceMkdir(File directory) throws IOException {
        if (directory.exists()) {
            if (!directory.isDirectory()) {
                String message =
                        "File "
                                + directory
                                + " exists and is "
                                + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else {
            if (!directory.mkdirs()) {
                // Double-update that some other thread or process hasn't made
                // the directory in the background
                if (!directory.isDirectory()) {
                    String message =
                            "Unable to create directory " + directory;
                    throw new IOException(message);
                }
            }
        }
    }

    public static boolean mkdirs(File directory) {
        try {
            forceMkdir(directory);
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    public static boolean createNewFile(File pFile) {
        if (mkdirs(pFile.getParentFile())) {

            if (pFile.exists() && !pFile.delete()) {
                return false;
            }

            try {
                if (pFile.createNewFile()) {
                    return true;
                }
            } catch (IOException pE) {
                pE.printStackTrace();
            }
        }
        return false;
    }

    public static boolean saveString(String pStr, File pFile) {
        try {
            forceMkdir(pFile.getParentFile());
            saveBytes(pStr.getBytes(), pFile);
        } catch (IOException pE) {
            return false;
        }
        return false;
    }

    public static boolean saveBytes(byte[] pBytes, File pFile) {

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(pFile);
        } catch (FileNotFoundException pE) {
            Log.e(TAG, "saveBytes: ", pE);
            return false;
        }

        try {
            outputStream.write(pBytes);
            outputStream.flush();
        } catch (IOException pE) {
            pE.printStackTrace();
        } finally {
            try {
                outputStream.close();
            } catch (IOException pE) {
                pE.printStackTrace();
            }
        }
        return false;
    }
}
