package com.shop.mvc.controllers;

import com.nanomvc.Model;
import com.nanomvc.ModelFactory;
import com.shop.mvc.models.Message;
import com.shop.mvc.models.User;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageController extends MainController {

    private static Logger _log = LoggerFactory.getLogger(MessageController.class);

    public void doIndex() {
        redirect(createUrl("message", "inbox", new Object[0]));
    }

    public void doInbox() {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "index", new Object[0]));
            return;
        }

        Model msgModel = ModelFactory.loadModel(Message.class);
        List messages = msgModel.setOrder("created", 1).addCriteria("recipient", getCurrentUser()).find();

        assign("messages", messages);
        render();
    }

    public void doOutbox() {
        if (!isUserLoggedIn().booleanValue()) {
            redirect(createUrl("catalog", "index", new Object[0]));
            return;
        }

        Model msgModel = ModelFactory.loadModel(Message.class);
        List messages = msgModel.setOrder("created", 1).addCriteria("sender", getCurrentUser()).find();

        assign("messages", messages);
        render();
    }

    public void doContent(Long id) {
        if (isAjax().booleanValue()) {
            Model msgModel = ModelFactory.loadModel(Message.class);
            Message message = (Message) msgModel.findByPk(id);

            if (message.getRecipient().getId().equals(getCurrentUserId())) {
                message.setStatus(Boolean.valueOf(true));
                msgModel.save(message);
                assign("reply", Boolean.valueOf(true));
            }
            assign("message", message);
            output(fetch("message/content"));
        }
    }

    public void doReply() {
        if (isAjax().booleanValue()) {
            JSONObject result = new JSONObject();
            try {
                result.put("status", "error");
                if (getParam("submit") != null) {
                    String title = getParam("title");
                    String content = getParam("content");
                    if ((!isEmpty(title).booleanValue()) && (!isEmpty(content).booleanValue())) {
                        Long id = Long.valueOf(getParam("reply"));

                        Model msgModel = ModelFactory.loadModel(Message.class);
                        Message message = (Message) msgModel.findByPk(id);

                        Message reply = new Message();
                        reply.setCreated(new Date());
                        reply.setTitle(title);
                        reply.setContent(content);
                        reply.setRecipient(message.getSender());
                        reply.setSender(getCurrentUser());
                        reply.setStatus(Boolean.valueOf(false));

                        msgModel.save(reply);
                        result.put("status", "ok");
                    }
                }
            } catch (Exception e) {
                _log.error("error", e);
            }
            output(result.toString());
        }
    }
}