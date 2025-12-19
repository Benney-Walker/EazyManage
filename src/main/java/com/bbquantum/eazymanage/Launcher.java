package com.bbquantum.eazymanage;

import javafx.application.Application;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

public class Launcher {
    public static void main(String[] args) {
        try {
            System.out.println("Starting EazyManage...");
            Application.launch(EazyManage.class, args);
        } catch (Throwable e) {
            System.err.println("Error launching application:");
            e.printStackTrace();
            System.out.println("\nPress Enter to exit...");
            try {
                System.in.read();
            } catch (Exception ex) {
                // ignore
            }
        }
    }
}
