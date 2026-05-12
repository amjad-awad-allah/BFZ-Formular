package com.dataflex.logic;

import com.dataflex.model.FormConfig;
import com.dataflex.model.FormData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonHandler {
    // Gson-Instanz für schönes JSON-Format
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static FormConfig loadConfig(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, FormConfig.class);
        }
    }

    public static void saveData(String filePath, FormData data) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    public static FormData loadData(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, FormData.class);
        }
    }
}
