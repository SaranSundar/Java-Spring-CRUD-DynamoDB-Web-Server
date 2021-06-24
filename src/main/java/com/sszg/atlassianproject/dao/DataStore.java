package com.sszg.atlassianproject.dao;

import java.util.List;

public interface DataStore<T> {
    public void saveItem(T item);

    public List<T> queryForItem(String globalSIN, String tableIndex, String valueToFind);

    public T getItem(String key);

    public void deleteItem(T item);
}
