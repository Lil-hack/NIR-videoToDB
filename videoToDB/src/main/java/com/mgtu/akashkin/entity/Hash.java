package com.mgtu.akashkin.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "hash")
public class Hash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "hash", length = 80)
    private String hash;

    public Integer getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hash hash = (Hash) o;
        return Objects.equals(id, hash.id) &&
                Objects.equals(this.hash, hash.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hash);
    }

    @Override
    public String toString() {
        return "Hash{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                '}';
    }
}
