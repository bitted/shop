package com.shop.mvc.models.catalog;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "appCatalogImages")
public class Image
        implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "itemId")
    private Item item;
    @Column
    private Boolean main;
    @Column
    private String absolutePath;
    @Column
    private String relativePath;
    @Column
    private String fileName;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return this.item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Boolean getMain() {
        return this.main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    public String getAbsolutePath() {
        return this.absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public String getRelativePath() {
        return this.relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getImage() {
        if ((this.absolutePath == null) || (this.fileName == null)) {
            return null;
        }
        return this.absolutePath + "/" + this.fileName;
    }

    public String getImage(String path) {
        if ((this.absolutePath == null) || (this.fileName == null)) {
            return null;
        }
        path = "/" + path;
        return this.absolutePath + path + "/" + this.fileName;
    }

    public String getImage(Integer width, Integer height) {
        if ((this.absolutePath == null) || (this.fileName == null)) {
            return null;
        }
        String path = "/" + width + "x" + height;
        return this.absolutePath + path + "/" + this.fileName;
    }
}