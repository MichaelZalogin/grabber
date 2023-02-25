package ru.job4j.dao;

import ru.job4j.entity.Post;

import java.util.List;

public interface Store extends AutoCloseable {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}