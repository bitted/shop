package com.shop.mvc.controllers;

import com.nanomvc.Model;
import com.nanomvc.ModelFactory;
import com.nanomvc.exceptions.ControllerException;
import com.shop.mvc.models.catalog.Item;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PictureSize;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserController extends MainController {

    private static Logger _log = LoggerFactory.getLogger(UserController.class);

    public void doIndex() {
    }

    public void doLogin(String service) {
        if ((service != null) && (service.equals("facebook"))) {
            Facebook facebook = new FacebookFactory().getInstance();
            facebook.setOAuthAppId("353373901437965", "50e189b715553e18c4e84f47243eeb75");
            facebook.setOAuthPermissions("email");
            storeSet("facebook", facebook);
            storeSet("redirectUrl", this.request.getHeader("referer"));
            redirect(facebook.getOAuthAuthorizationURL(createUrl("user", "facebook", new Object[0])));
            return;
        }
    }

    public void doLogout() {
        userLogout();
        redirect("/");
    }

    public void doFacebook() {
        String oauthCode = getParam("code");
        if (oauthCode == null) {
            throw new ControllerException("Bad Request");
        }
        Facebook facebook = (Facebook) storeGet("facebook");
        try {
            facebook.getOAuthAccessToken(oauthCode);

            Model model = ModelFactory.loadModel(com.shop.mvc.models.User.class);
            com.shop.mvc.models.User user = (com.shop.mvc.models.User) model.addCriteria("service", "facebook").addCriteria("userId", facebook.getId()).findOne();

            Date date = new Date();
            if (user == null) {
                user = new com.shop.mvc.models.User();
                user.setService("facebook");
                user.setStatus(Boolean.valueOf(true));
                user.setEmail(facebook.getEmail());
                user.setUsername(facebook.getMe().getUsername());
                user.setUserId(facebook.getId());
                user.setRegistered(date);
            }
            user.setName(facebook.getMe().getName());
            user.setFirstName(facebook.getMe().getFirstName());
            user.setLastName(facebook.getMe().getLastName());
            user.setGender(facebook.getMe().getGender());
            user.setBirthday(facebook.getMe().getBirthday());
            user.setLastLogin(date);
            model.save(user);

            if (facebook.getPictureURL(PictureSize.large) != null) {
                String pictureName = user.getPicture() != null ? user.getPicture() : UUID.randomUUID().toString();

                String picture = saveImageFromUrl(facebook.getPictureURL(PictureSize.large).toString(), pictureName);

                if (picture != null) {
                    user.setPicture(picture);
                    model.save(user);
                }
            }

            userLogin(user);
            storeSet("facebook", null);
            String url = (String) storeGet("redirectUrl");
            storeSet("redirectUrl", null);
            if (url == null) {
                url = "/";
            }
            redirect(url);
        } catch (FacebookException e) {
            throw new ControllerException("Bad Request");
        }
    }

    public void doView(Long userId) {
        Model model = ModelFactory.loadModel(com.shop.mvc.models.User.class);
        com.shop.mvc.models.User user = (com.shop.mvc.models.User) model.findByPk(userId);
        Model itemModel = ModelFactory.loadModel(Item.class);
        List items = itemModel.addCriteria("user", user).setOrder("created", 1).find();

        assign("user", user);
        assign("items", items);
        render();
    }
}