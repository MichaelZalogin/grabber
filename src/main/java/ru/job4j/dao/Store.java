package ru.job4j.dao;

import ru.job4j.entity.Post;

import java.util.List;
import java.util.Optional;

public interface Store extends AutoCloseable {
    void save(Post post);

    List<Post> getAll();

    Optional<Post> findById(int id);
}