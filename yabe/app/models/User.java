package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

@Entity
public class User extends Model {

    public String email;
    public String password;
    public String fullName;
    public boolean isAdmin;

    public User(String email, String password, String fullName) {

        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

}
