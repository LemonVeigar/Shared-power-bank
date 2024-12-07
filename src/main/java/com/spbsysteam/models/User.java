package com.spbsysteam.models;

/**
 * User类表示系统中的用户实体。
 */
public class User {
    private int id;
    private String username;
    private String role;

    /**
     * 构造方法，初始化User对象。
     *
     * @param id       用户ID
     * @param username 用户名
     * @param role     用户角色（如 "admin" 或 "user"）
     */
    public User(int id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    // Setters（如果需要修改用户信息，可以添加相应的setter方法）

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
