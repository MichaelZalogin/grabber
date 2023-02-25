package ru.job4j.dao;

import java.sql.*;

import ru.job4j.entity.Post;
import ru.job4j.utils.ConnectionManager;
import ru.job4j.utils.PropertiesUtil;

import java.util.ArrayList;
import java.util.List;

public class PsqlStore implements Store {

    private Connection cn;

    public PsqlStore(String propertyPath) {
        var propertiesUtil = new PropertiesUtil(propertyPath);
        cn = new ConnectionManager(propertiesUtil).open();
    }

    @Override
    public void save(Post post) {
        try (var statement = cn.prepareStatement("""
                    INSERT INTO post_schema.post (title, link, description, created)
                    VALUES (?,?,?,?)
                    ON CONFLICT (link) DO NOTHING 
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                post.setId(generatedKeys.getInt("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (var statement = cn.prepareStatement("""
                    SELECT (id, title, link, description, created)
                    FROM post_schema.post;
                """, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.executeQuery();
            ResultSet setPosts = statement.getResultSet();
            while (setPosts.next()) {
                posts.add(new Post(setPosts.getInt("id"),
                        setPosts.getString("title"),
                        setPosts.getString("link"),
                        setPosts.getString("description"),
                        setPosts.getTimestamp("created").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (var statement = cn.prepareStatement(
                """
                        SELECT id, title, link, description, created
                        FROM post_schema.post
                        WHERE id = ?;
                        """)) {
            statement.setInt(1, id);
            var setPosts = statement.executeQuery();
            if (setPosts.next()) {
                post = new Post(setPosts.getInt("id"),
                        setPosts.getString("title"),
                        setPosts.getString("link"),
                        setPosts.getString("description"),
                        setPosts.getTimestamp("created").toLocalDateTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cn != null) {
            cn.close();
        }
    }
}