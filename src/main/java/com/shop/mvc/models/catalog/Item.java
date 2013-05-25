package com.shop.mvc.models.catalog;

import com.shop.mvc.models.User;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "appCatalogItems")
public class Item
        implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {javax.persistence.CascadeType.MERGE})
    @JoinColumn(name = "userId")
    private User user;
    @ManyToOne(cascade = {javax.persistence.CascadeType.MERGE})
    @JoinColumn(name = "categoryId")
    private Category category;
    @Column
    private String title;
    @Column
    private String description;
    @Column
    private BigDecimal price;
    @Column
    private Boolean status;
    @Column
    private Boolean sold;
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    @Fetch(FetchMode.SUBSELECT)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<Image> images;
    @OneToOne(mappedBy = "item")
    @Fetch(FetchMode.JOIN)
    @Cascade({org.hibernate.annotations.CascadeType.ALL})
    private ItemData data;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getSold() {
        return this.sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }

    public Date getCreated() {
        return this.created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public ItemData getData() {
        return this.data;
    }

    public void setData(ItemData data) {
        this.data = data;
    }

    public Image getMainImage() {
        if ((this.images == null) || (this.images.size() == 0)) {
            return null;
        }
        for (Image image : this.images) {
            if (image.getMain().booleanValue()) {
                return image;
            }
        }
        return (Image) this.images.get(0);
    }

    public Integer getMainImageIndex() {
        Integer index = Integer.valueOf(0);
        Integer i = Integer.valueOf(0);
        if (this.images != null) {
            Integer localInteger1;
            Integer localInteger2;
            for (Iterator i$ = this.images.iterator(); i$.hasNext();
                    localInteger2 = i = Integer.valueOf(i.intValue() + 1)) {
                Image image = (Image) i$.next();
                if (image.getMain().booleanValue()) {
                    index = i;
                    break;
                }
                localInteger1 = i;
            }
            return index;
        }
        return null;
    }
}