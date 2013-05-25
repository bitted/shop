<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Error</title>

        <link rel="stylesheet" href="<c:url value="/public/css/bootstrap.min.css" />" type="text/css" media="all" />
        <link rel="stylesheet" href="<c:url value="/public/css/bootstrap-responsive.min.css" />" type="text/css" media="all" />
    </head>
    <body style="padding-top: 10px;">

        <div class="container">

            <div class="alert alert-error">${error}</div>

        </div>

    </body>
</html>