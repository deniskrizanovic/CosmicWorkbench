package com.fp.model;

import com.fp.dao.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Persisted {
    int id;
    int version;
    String createdBy;
    Date createdTime;
    Repository repository;

    public int getId() {
        return id;
    }

    public Object setId(int id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Repository getRepository() {
        return repository;
    }

    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Persisted persisted = (Persisted) o;

        if (id != persisted.id) return false;
        if (version != persisted.version) return false;
        if (createdBy != null ? !createdBy.equals(persisted.createdBy) : persisted.createdBy != null) return false;
        return !(createdTime != null ? !createdTime.equals(persisted.createdTime) : persisted.createdTime != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + version;
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (createdTime != null ? createdTime.hashCode() : 0);
        return result;
    }
}
