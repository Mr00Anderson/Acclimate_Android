package com.acclimate.payne.simpletestapp.deviceStorage.localStorage;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Use this class to perform multiple read() write() to different files to write arrays of objects.
 *
 * @param <T>
 */
public class LocalStorageController<T> {

    private ObjectMapper mapper; // create once, reuse
    private ObjectMapper collectionMapper; // create once, reuse

    private FileLocalStorage storage;

    /**
     *
     * @param localStorage
     */
    public LocalStorageController(FileLocalStorage<T> localStorage){
        this.mapper = new ObjectMapper();
        this.collectionMapper = new ObjectMapper();
        storage = localStorage;
    }
/*

    */
/**
     *
     * @param elements
     * @param filename
     * @throws IOException
     *//*

    public void writeAll(ArrayList<T> elements, String filename) throws IOException {

        CollectionLocalStorage<T> elementList = new CollectionLocalStorage<>(elements);
        FileLocalStorage<CollectionLocalStorage<T>> fileToSave =
                new FileLocalStorage<>(filename, elementList, collectionMapper);
        fileToSave.write();

    }
*/

    /**
     *
     * @param filename
     * @param data
     * @throws IOException
     */
    public void write(String filename, T data) throws IOException {
        storage.setData(data);
        storage.updateFile(filename);
        // FileLocalStorage<T> file = new FileLocalStorage<>(filename, data, mapper);
        storage.write();

    }

    /**
     *
     * @param filename
     * @param data
     * @param ctx
     * @throws IOException
     */
    public void write(String filename, T data, Context ctx) throws IOException {

        FileLocalStorage<T> file = new FileLocalStorage<>(filename, data, ctx, mapper);
        file.write();

    }

    // TODO : read files [WIP : has been tested, need to be implemented]

    @SuppressWarnings("unchecked")
    public T readSingleValue(String filename) throws IOException {
        T value = mapper.readValue(filename, (Class<T>) storage.getData());
        return value;

    }

}
