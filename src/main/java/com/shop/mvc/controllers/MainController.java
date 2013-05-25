package com.shop.mvc.controllers;

import com.nanomvc.Controller;
import com.nanomvc.Model;
import com.nanomvc.ModelFactory;
import com.shop.mvc.models.Message;
import com.shop.mvc.models.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainController extends Controller {

    public void init() {
        Locale lt = new Locale("lt", "LT");
        Long unreadCount = Long.valueOf(0L);
        if (isUserLoggedIn().booleanValue()) {
            Model msgModel = ModelFactory.loadModel(Message.class);
            unreadCount = msgModel.addCriteria("recipient", getCurrentUser()).addCriteria("status", Boolean.valueOf(false)).count();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy", lt);
        assign("main.date", formatter.format(new Date()));
        Map menuParams = new HashMap();
        menuParams.put("userStatus", isUserLoggedIn());
        if (isUserLoggedIn().booleanValue()) {
            menuParams.put("unreadCount", unreadCount);
        }
        assign("main.menu", fetch("layout/menu", menuParams));
        assign("main.pageTitle", "Katalogas");
        assign("main.userStatus", isUserLoggedIn());
        assign("userStatus", isUserLoggedIn());
        if (isUserLoggedIn().booleanValue()) {
            assign("main.unreadCount", Integer.valueOf(0));
            assign("currentUser", getCurrentUser());
        }
    }

    protected void userLogin(User user) {
        storeSet("currentUserId", user.getId());
    }

    protected void userLogout() {
        storeClear();
    }

    protected Boolean isUserLoggedIn() {
        return Boolean.valueOf(storeGet("currentUserId") != null);
    }

    protected Long getCurrentUserId() {
        if (!isUserLoggedIn().booleanValue()) {
            return null;
        }
        return (Long) storeGet("currentUserId");
    }

    protected User getCurrentUser() {
        Model model = ModelFactory.loadModel(User.class);
        return (User) model.findByPk(storeGet("currentUserId"));
    }
}