package com.github.jiizuz.booking.data;

import lombok.NonNull;

import java.util.Collection;

/**
 * Represents a repository that handles the data.
 *
 * <p>Take note that the key cannot be {@code null}.
 *
 * @param <T> type of the data that the repository handles
 * @param <K> type of the key to identify the data
 * @author <a href="mailto:masterchack92@hotmail.com">Jiizuz</a>
 * @since 1.0
 */
public interface Repository<T, K> {

    /**
     * Saves the data in the repository.
     *
     * <p>If the data is already saved, it will be updated.
     *
     * @param data the data to save
     * @return the key of the saved data
     * @throws NullPointerException if the data is {@code null}
     */
    K save(@NonNull T data);

    /**
     * Retrieves data from the repository.
     *
     * @param key the key of the data
     * @return the data
     * @throws NullPointerException if the key is {@code null}
     */
    T get(@NonNull K key);

    /**
     * Deletes data from the repository.
     *
     * @param key the key of the data
     * @return the deleted data
     * @throws NullPointerException if the key is {@code null}
     */
    T delete(@NonNull K key);

    /**
     * Retrieves all the data from the repository.
     *
     * @return the collection of all the data
     * @apiNote The collection does not have any specific order.
     */
    @NonNull
    Collection<T> getAll();
}
