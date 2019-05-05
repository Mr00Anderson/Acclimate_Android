package com.acclimate.payne.simpletestapp.deviceStorage.localStorage;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

import lombok.Getter;
import lombok.Setter;

public class FileLocalStorage<T> {

    private static ObjectMapper mapper = new ObjectMapper(); // create once, reuse

    private File file;
    private Context ctx;
    @Getter @Setter private T data;


    /**
     *
     * @param fileName
     * @param ctx
     * @param data
     */
    public FileLocalStorage(String fileName, T data, Context ctx) {

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.file = new File(ctx.getFilesDir(), fileName);
        this.data = data;
        this.ctx = ctx;
    }


    /**
     * Default constructor to set the contxt to the main activity
     *
     * @param fileName
     * @param data
     *//*
    public FileLocalStorage(String fileName, T data, ObjectMapper objectMapper) {
        this(fileName, data, App.getInstance().getActivityInstance(), objectMapper);
    }
*/
    /**
     *
     * @param fileName
     * @param ctx
     * @param data
     */
    public FileLocalStorage(String fileName, T data, Context ctx, ObjectMapper objectMapper) {

        mapper = objectMapper;
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.file = new File(ctx.getFilesDir(), fileName);
        this.data = data;
        this.ctx = ctx;
    }



    @SuppressWarnings("unchecked")
    public T read() throws IOException {
        return mapper.readValue(file, (Class<T>) this.data.getClass());
    }


    public void write() throws IOException {
        mapper.writeValue(file, data);
    }

    public void updateFile(String filename){
        this.file = new File(ctx.getFilesDir(), filename);
    }

}

