package org.yearup.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.yearup.models.authentication.Authority;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "user_id")
   private int id;

   @Column(name = "username")
   private String username;

   @JsonIgnore
   @Column(name = "hashed_password")
   private String password;

   @Column(name = "role")
   private String role;

   @JsonIgnore
   @Transient
   private boolean activated;

   @Transient
   private Set<Authority> authorities = new HashSet<>();

   @PostLoad
   private void onLoad() {
      this.activated = true;
      if (role != null) {
         this.authorities = new HashSet<>();
         String[] roles = role.split(",");
         for (String r : roles) {
            String authority = r.trim().contains("ROLE_") ? r.trim() : "ROLE_" + r.trim();
            this.authorities.add(new Authority(authority));
         }
      }
   }

   public User() {
      this.activated = true;
   }

   public User(int id, String username, String password, String authorities) {
      this.id = id;
      this.username = username;
      this.password = password;
      if(authorities != null) this.setAuthorities(authorities);
      this.activated = true;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public boolean isActivated() {
      return activated;
   }

   public void setActivated(boolean activated) {
      this.activated = activated;
   }

   public Set<Authority> getAuthorities() {
      return authorities;
   }

   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }

   public void setAuthorities(String authorities) {
      this.role = authorities;
      String[] roles = authorities.split(",");
      for(String r : roles) {
         addRole(r);
      }
   }

   public void addRole(String role)
   {
      String authority = role.trim().contains("ROLE_") ? role.trim() : "ROLE_" + role.trim();
      this.authorities.add(new Authority(authority));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id &&
              activated == user.activated &&
              Objects.equals(username, user.username) &&
              Objects.equals(password, user.password) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, password, activated, authorities);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", activated=" + activated +
              ", authorities=" + authorities +
              '}';
   }

   @JsonIgnore
   public String getRole()
   {
      if(authorities.size() > 0)
      {
         for(Authority r: authorities)
         {
            return r.getName().toUpperCase();
         }
      }

      return "ROLE_USER";
   }
}
