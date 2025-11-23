package com.project.system_log_analyzer.system;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.win32.W32APIOptions;

import java.io.File;

public class WindowsElevationManager {

    public interface Shell32 extends com.sun.jna.Library {
        Shell32 INSTANCE = Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);

        Pointer ShellExecuteW(
                Pointer hwnd,
                WString lpOperation,
                WString lpFile,
                WString lpParameters,
                WString lpDirectory,
                int nShowCmd
        );
    }

    public static boolean relaunchAsAdmin(String params) {
        try {
            String exePath = new File(System.getProperty("user.dir"),
                    "System_Log_Analyzer.exe").getAbsolutePath();

            Pointer p = Shell32.INSTANCE.ShellExecuteW(
                    null,
                    new WString("runas"),
                    new WString(exePath),
                    params == null ? null : new WString(params),
                    null,
                    1
            );

            long result = Pointer.nativeValue(p);
            return result > 32;

        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public static boolean isCurrentUserAdmin() {
        try {
            new java.io.File("C:\\Windows\\System32\\config\\systemprofile").list();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
