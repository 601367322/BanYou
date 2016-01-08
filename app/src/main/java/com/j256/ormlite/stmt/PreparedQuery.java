package com.j256.ormlite.stmt;

/**
 * Interface returned by the {@link QueryBuilder#prepare()} which supports custom SELECT queries. This should be in turn
 * passed to the {@link com.j256.ormlite.dao.Dao#query(PreparedQuery)} or {@link com.j256.ormlite.dao.Dao#iterator(PreparedQuery)} methods.
 *
 * @param <T> The class that the code will be operating on.
 * @author graywatson
 */
public interface PreparedQuery<T> extends PreparedStmt<T> {
}
