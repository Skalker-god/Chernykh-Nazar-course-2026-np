<#macro page>
    <!DOCTYPE html>
    <html lang="uk">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <title>Каса автовокзалу</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
    </head>
    <body>
    <nav class="navbar navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="/">🚌 Автовокзал</a>
            <div>
                <a class="btn btn-outline-light" href="/">Головна</a>
                <a class="btn btn-outline-light" href="/cart">🛒 Кошик</a>

                <#-- Посадкові відомості тільки для касирів та адмінів -->
                <#if Session.user?? && (Session.user.role == 'CASHIER' || Session.user.role == 'ADMIN')>
                    <a class="btn btn-outline-light" href="/boarding">📋 Посадкові відомості</a>
                </#if>

                <#-- Адмін-панель тільки для адміністратора -->
                <#if Session.user?? && Session.user.role == 'ADMIN'>
                    <a class="btn btn-outline-warning" href="/admin/routes">⚙️ Рейси</a>
                    <a class="btn btn-outline-warning" href="/admin/users">👥 Користувачі</a>
                    <a class="btn btn-outline-warning" href="/admin/tickets">🎫 Квитки</a>
                </#if>

                <a class="btn btn-outline-light" href="/about">Про нас</a>

                <#-- Якщо користувач залогінений -->
                <#if Session.user??>
                    <a class="btn btn-outline-light" href="/profile">👤 ${Session.user.fullName}</a>
                    <a class="btn btn-outline-danger" href="/logout">Вийти</a>
                <#else>
                    <a class="btn btn-outline-light" href="/login">Вхід</a>
                    <a class="btn btn-success" href="/register">Реєстрація</a>
                </#if>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <#nested>
    </div>

    <footer class="mt-5 py-3 bg-light text-center">
        <a href="/about" style="color: #6c757d">Про нас</a>
        <p class="text-muted mb-0">© 2026 Каса автовокзалу</p>
    </footer>

    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
    </body>
    </html>
</#macro>