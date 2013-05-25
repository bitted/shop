package com.shop.mvc.jobs;

import com.nanomvc.Model;
import com.nanomvc.ModelFactory;
import com.shop.mvc.models.catalog.Item;
import com.shop.mvc.models.catalog.ItemData;

public class UpdateViewsJob
        implements Runnable {

    private Item item;

    public UpdateViewsJob(Item item) {
        this.item = item;
    }

    public void run() {
        Integer views = Integer.valueOf(this.item.getData().getViews().intValue() + 1);
        this.item.getData().setViews(views);
        ModelFactory.loadModel(Item.class).save(this.item);
    }
}