package com.shop.mvc;

import com.nanomvc.Router;
import java.util.HashMap;
import java.util.Map;

public class ShopRouter extends Router {

    public Map routes() {
        Map routes = new HashMap();

        routes.put("/papuosalai",               "Catalog.index");
        routes.put("/papuosalai/kategorija",    "Catalog.category");
        routes.put("/papuosalai/prideti",       "Catalog.create");
        routes.put("/papuosalai/mano",          "Catalog.profile");
        routes.put("/papuosalas",               "Catalog.view");
        routes.put("/papuosalas/pirkti",        "Catalog.buy");
        routes.put("/papuosalas/redaguoti",     "Catalog.edit");

        routes.put("/pranesimai",               "Message.index");
        routes.put("/pranesimai/gauti",         "Message.inbox");
        routes.put("/pranesimai/siusti",        "Message.outbox");
        
        routes.put("/vartotojas",               "User.view");
        routes.put("/profilis",                 "User.profile");

        routes.put("/apie",                     "Static.about");
        routes.put("/pagalba",                  "Static.help");

        return routes;
    }
}