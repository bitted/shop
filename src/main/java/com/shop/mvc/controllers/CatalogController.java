package com.shop.mvc.controllers;

import com.nanomvc.LocalFile;
import com.nanomvc.MemcachedFactory;
import com.nanomvc.Model;
import com.nanomvc.ModelFactory;
import com.shop.mvc.jobs.UpdateViewsJob;
import com.shop.mvc.models.Message;
import com.shop.mvc.models.User;
import com.shop.mvc.models.catalog.Category;
import com.shop.mvc.models.catalog.Image;
import com.shop.mvc.models.catalog.Item;
import com.shop.mvc.models.catalog.ItemData;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import javax.servlet.ServletContext;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogController extends MainController {

    private static Logger _log = LoggerFactory.getLogger(CatalogController.class);
    private static Integer LIMIT = Integer.valueOf(24);

    public void doIndex(Integer page) {
        if (getParam("type") != null) {
            storeSet("type", getParam("type"));
        }
        if (getParam("sort") != null) {
            storeSet("sort", getParam("sort"));
        }

        String type = (String) storeGet("type");
        if (type == null) {
            type = "grid";
        }
        String sort = (String) storeGet("sort");
        if (sort == null) {
            sort = "time";
        }
        Integer limit = LIMIT;
        Integer offset = Integer.valueOf(0);
        Long pages = Long.valueOf(1L);

        if (page != null) {
            offset = Integer.valueOf((page.intValue() - 1) * limit.intValue());
        }

        Model itemModel = ModelFactory.loadModel(Item.class);

        if (sort.equals("price")) {
            itemModel.setOrder("price", 0);
        } else {
            itemModel.setOrder("created", 1);
        }
        List items = itemModel.addCriteria("status", Boolean.valueOf(true)).addCriteria("sold", Boolean.valueOf(false)).setLimit(limit, offset).find();

        Long total = itemModel.count();
        pages = Long.valueOf(total.longValue() / limit.intValue() + (total.longValue() % limit.intValue() == 0L ? 0 : 1));

        categories();
        assign("items", items);
        assign("pages", pages);
        assign("page", page);
        assign("type", type);
        assign("sort", sort);
        render();
    }

    public void doCategory(Integer categoryId, Integer page) {
        if (getParam("type") != null) {
            storeSet("type", getParam("type"));
        }
        if (getParam("sort") != null) {
            storeSet("sort", getParam("sort"));
        }

        String type = (String) storeGet("type");
        if (type == null) {
            type = "grid";
        }
        String sort = (String) storeGet("sort");
        if (sort == null) {
            sort = "time";
        }
        Integer limit = LIMIT;
        Integer offset = Integer.valueOf(0);
        Long pages = Long.valueOf(1L);

        if (page != null) {
            offset = Integer.valueOf((page.intValue() - 1) * limit.intValue());
        }

        Model categoryModel = ModelFactory.loadModel(Category.class);
        Category category = (Category) categoryModel.findByPk(categoryId);

        Model itemModel = ModelFactory.loadModel(Item.class);

        if (sort.equals("price")) {
            itemModel.setOrder("price", 0);
        } else {
            itemModel.setOrder("created", 1);
        }
        List items = itemModel.addCriteria("status", Boolean.valueOf(true)).addCriteria("sold", Boolean.valueOf(false)).addCriteria("category", category).setLimit(limit, offset).find();

        Long total = itemModel.count();
        pages = Long.valueOf(total.longValue() / limit.intValue() + (total.longValue() % limit.intValue() == 0L ? 0 : 1));

        assign("items", items);
        assign("pages", pages);
        assign("page", page);
        assign("type", type);
        assign("sort", sort);
        categories(categoryId);
        render("index");
    }

    public void doProfile() {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "index", new Object[0]));
            return;
        }

        User user = getCurrentUser();
        Model itemModel = ModelFactory.loadModel(Item.class);
        List items = itemModel.addCriteria("user", user).setOrder("created", 1).find();

        assign("items", items);
        render();
    }

    public void doBuy(Long itemId) {
        if ((isAjax().booleanValue()) && (getParam("submit") != null)) {
            String title = getParam("title");
            String content = getParam("content");
            if ((!isEmpty(title).booleanValue()) && (!isEmpty(content).booleanValue())) {
                Model itemModel = ModelFactory.loadModel(Item.class);
                Item item = (Item) itemModel.findByPk(itemId);

                Model msgModel = ModelFactory.loadModel(Message.class);

                Message message = new Message();
                message.setCreated(new Date());
                message.setTitle(title);
                message.setContent(content);
                message.setRecipient(item.getUser());
                message.setSender(getCurrentUser());
                message.setStatus(Boolean.valueOf(false));

                msgModel.save(message);
            }
        }
    }

    private void categories() {
        categories(null);
    }

    public void doCreate() {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "index", new Object[0]));
            return;
        }
        try {
            String submit = getParam("submit");
            if (submit != null) {
                Integer categoryId = Integer.valueOf(Integer.parseInt(getParam("category")));
                Model categoryModel = ModelFactory.loadModel(Category.class);
                Category category = (Category) categoryModel.findByPk(categoryId);

                LocalFile file = handleFileUpload("photo", UUID.randomUUID().toString());
                Model itemModel = ModelFactory.loadModel(Item.class);
                Model dataModel = ModelFactory.loadModel(ItemData.class);
                Model imageModel = ModelFactory.loadModel(Image.class);

                Item item = new Item();
                item.setCategory(category);
                item.setUser(getCurrentUser());
                item.setTitle(getParam("title"));
                item.setDescription(getParam("description"));
                item.setPrice(new BigDecimal(getParam("price")));
                item.setStatus(Boolean.valueOf(true));
                item.setSold(Boolean.valueOf(false));
                item.setCreated(new Date());

                itemModel.save(item);

                ItemData data = new ItemData();
                data.setItem(item);
                data.setViews(Integer.valueOf(0));
                dataModel.save(data);

                if (file != null) {
                    Image image = new Image();
                    image.setItem(item);
                    image.setMain(Boolean.valueOf(true));
                    image.setFileName(file.getFileName());
                    image.setAbsolutePath(file.getAbsolutePath());
                    image.setRelativePath(file.getRelativePath());
                    image.setCreated(new Date());

                    imageModel.save(image);

                    thumb(image.getFileName(), image.getRelativePath(), 150, 100, 3);
                    thumb(image.getFileName(), image.getRelativePath(), 300, 200, 3);
                    thumb(image.getFileName(), image.getRelativePath(), 600, 400, 3);
                    thumb(image.getFileName(), image.getRelativePath(), 640, 640, 2);
                }

                redirect(createUrl("catalog", "view", new Object[]{item.getId()}));
                return;
            }
        } catch (Exception e) {
            _log.error("error creating record", e);
        }
        List categories = getCategories();
        assign("categories", categories);
        render();
    }

    public void doView(Long itemId) {
        Model itemModel = ModelFactory.loadModel(Item.class);
        Item item = (Item) itemModel.findByPk(itemId);
        try {
            ExecutorService executor = (ExecutorService) this.context.getAttribute("NANOMVC_EXECUTOR");
            executor.submit(new UpdateViewsJob(item));
        } catch (Exception e) {
        }
        List related = itemModel.addCriteria("category", item.getCategory()).addCriteria("status", Boolean.valueOf(true)).addCriteria("sold", Boolean.valueOf(false)).addCriteria("id", item.getId(), Boolean.valueOf(false)).setLimit(Integer.valueOf(9)).find();

        assign("main.title", item.getTitle());
        assign("item", item);
        assign("items", related);
        assign("main.like", Boolean.valueOf(true));
        render();
    }

    public void doEdit(Long itemId) {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "view", new Object[]{itemId}));
            return;
        }

        Model itemModel = ModelFactory.loadModel(Item.class);
        Item item = (Item) itemModel.findByPk(itemId);

        if (!getCurrentUserId().equals(item.getUser().getId())) {
            redirect(createUrl("catalog", "view", new Object[]{itemId}));
            return;
        }
        try {
            String submit = getParam("submit");
            if (submit != null) {
                Integer categoryId = Integer.valueOf(Integer.parseInt(getParam("category")));
                Model categoryModel = ModelFactory.loadModel(Category.class);
                Category category = (Category) categoryModel.findByPk(categoryId);

                item.setCategory(category);
                item.setUser(getCurrentUser());
                item.setTitle(getParam("title"));
                item.setDescription(getParam("description"));
                item.setPrice(new BigDecimal(getParam("price")));
                item.setSold(Boolean.valueOf(getParam("sold") != null));

                itemModel.save(item);

                List<LocalFile> files = handleMultipleFilesUpload("photos", UUID.randomUUID().toString());
                Model imageModel;
                if (files != null) {
                    imageModel = ModelFactory.loadModel(Image.class);

                    for (LocalFile file : files) {
                        try {
                            Image image = new Image();
                            image.setItem(item);
                            image.setFileName(file.getFileName());
                            image.setAbsolutePath(file.getAbsolutePath());
                            image.setRelativePath(file.getRelativePath());
                            image.setMain(Boolean.valueOf(false));
                            image.setCreated(new Date());

                            imageModel.save(image);
                            try {
                                thumb(image.getFileName(), image.getRelativePath(), Integer.valueOf(150), Integer.valueOf(100), 3);
                                thumb(image.getFileName(), image.getRelativePath(), Integer.valueOf(300), Integer.valueOf(200), 3);
                                thumb(image.getFileName(), image.getRelativePath(), Integer.valueOf(600), Integer.valueOf(400), 3);
                                thumb(image.getFileName(), image.getRelativePath(), Integer.valueOf(640), Integer.valueOf(640), 2);
                            } catch (Exception e) {
                                _log.error("error resizing image", e);
                            }
                        } catch (Exception e) {
                            _log.error("error saving image", e);
                        }
                    }
                }

                redirect(createUrl("catalog", "edit", new Object[]{item.getId()}));
                return;
            }
        } catch (Exception e) {
            _log.error("error saving record", e);
        }

        assign("item", item);
        List categories = getCategories();
        assign("categories", categories);
        render();
    }

    public void doRemove(Long itemId) {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "view", new Object[]{itemId}));
            return;
        }

        Model itemModel = ModelFactory.loadModel(Item.class);
        Item item = (Item) itemModel.findByPk(itemId);

        if (!getCurrentUserId().equals(item.getUser().getId())) {
            redirect(createUrl("catalog", "view", new Object[]{itemId}));
            return;
        }

        itemModel.remove(item);

        redirect(createUrl("catalog", "profile", new Object[0]));
    }

    public void doRemovePhoto(Long photoId) {
        Model imageModel = ModelFactory.loadModel(Image.class);
        Image image = (Image) imageModel.findByPk(photoId);
        image.setItem(null);
        imageModel.save(image);
    }

    public void doMainPhoto(Long itemId, Long photoId) {
        Model itemModel = ModelFactory.loadModel(Item.class);
        Item item = (Item) itemModel.findByPk(itemId);

        for (Image image : item.getImages()) {
            image.setMain(Boolean.valueOf(false));
        }
        itemModel.save(item);

        Model imageModel = ModelFactory.loadModel(Image.class);
        Image image = (Image) imageModel.findByPk(photoId);
        image.setMain(Boolean.valueOf(true));
        imageModel.save(image);
    }

    public void doResize(Integer width, Integer height, Integer mode) {
        Model itemModel = ModelFactory.loadModel(Item.class);
        List<Item> items = itemModel.find();
        for (Item item : items) {
            for (Image image : item.getImages()) {
                try {
                    thumb(image.getFileName(), image.getRelativePath(), width, height, mode.intValue());
                } catch (Exception e) {
                    _log.error("error resizing");
                }
            }
        }
        output("OK");
    }

    private void categories(Integer categoryId) {
        List categories = getCategories();

        Map params = new HashMap();
        params.put("categoryId", categoryId);
        params.put("categories", categories);

        assign("categories", fetch("catalog/categories", params));
    }

    private List<Category> getCategories() {
        List categories = null;
        try {
            categories = (List) MemcachedFactory.getInstance().get("appCatalogCategories");
        } catch (Exception e) {
        }
        if (categories == null) {
            Model model = ModelFactory.loadModel(Category.class);
            categories = model.setOrder("id", 0).addCriteria("parentId", Integer.valueOf(0)).findAll();
            try {
                MemcachedFactory.getInstance().set("appCatalogCategories", 3600, categories);
            } catch (Exception e) {
            }
        }
        return categories;
    }
}