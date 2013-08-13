<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="tiles/head.jsp"/>

<body>

<jsp:include page="tiles/header.jsp"/>

<div class="container">
    <div class="content">
        <div class="row">
            <div class="span12">
                <div class="login-form">
                    <h2>Login</h2>

                    <form name='f' action='/j_spring_security_check' method='POST'>
                        <fieldset>
                            <div class="clearfix">
                                <input type="text" name="j_username" placeholder="Username">
                            </div>
                            <div class="clearfix">
                                <input type="password" name="j_password" placeholder="Password">
                            </div>
                            <button class="btn primary" name="submit" type="submit">Sign in</button>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <hr>

    <jsp:include page="tiles/footer.jsp"/>
</div>

</body>
</html>
