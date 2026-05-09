<#import "../common.ftl" as c>
<@c.page>

    <h2 class="mb-4">👥 Управління користувачами</h2>

    <#if successMessage??>
        <div class="alert alert-success">${successMessage}</div>
    </#if>

    <table class="table table-bordered table-hover">
        <thead class="thead-dark">
        <tr>
            <th>ID</th>
            <th>ПІБ</th>
            <th>Телефон</th>
            <th>Роль</th>
            <th>Дата реєстрації</th>
            <th>Статус</th>
            <th>Змінити роль</th>
            <th>Дії</th>
        </tr>
        </thead>
        <tbody>
        <#if users??>
            <#list users as user>
                <tr>
                    <td>${user.id}</td>
                    <td>${user.fullName}</td>
                    <td>${user.phone}</td>
                    <td>
                        <#if user.role == 'ADMIN'>
                            <span class="badge badge-danger">ADMIN</span>
                        <#elseif user.role == 'CASHIER'>
                            <span class="badge badge-warning">CASHIER</span>
                        <#else>
                            <span class="badge badge-info">PASSENGER</span>
                        </#if>
                    </td>
                    <td>${user.createdAt}</td>
                    <td>
                        <#if user.isActive>
                            <span class="badge badge-success">Активний</span>
                        <#else>
                            <span class="badge badge-secondary">Заблокований</span>
                        </#if>
                    </td>
                    <td>
                        <form action="/admin/users/role/${user.id}" method="post"
                              style="display:flex; gap:5px">
                            <select name="role" class="form-control form-control-sm">
                                <option value="PASSENGER"
                                        <#if user.role == 'PASSENGER'>selected</#if>>
                                    Пасажир
                                </option>
                                <option value="CASHIER"
                                        <#if user.role == 'CASHIER'>selected</#if>>
                                    Касир
                                </option>
                                <option value="ADMIN"
                                        <#if user.role == 'ADMIN'>selected</#if>>
                                    Адміністратор
                                </option>
                            </select>
                            <button type="submit" class="btn btn-sm btn-primary">
                                Зберегти
                            </button>
                        </form>
                    </td>
                    <td>
                        <form action="/admin/users/toggle/${user.id}" method="post">
                            <button class="btn btn-sm ${user.isActive?then('btn-secondary','btn-success')}">
                                ${user.isActive?then('Заблокувати','Розблокувати')}
                            </button>
                        </form>
                    </td>
                </tr>
            </#list>
        </#if>
        </tbody>
    </table>

    <#if RequestParameters.error?? && RequestParameters.error == "cannot_change_own_role">
        <div class="alert alert-danger">Не можна змінювати власну роль!</div>
    </#if>
    <#if RequestParameters.error?? && RequestParameters.error == "cannot_block_own_account">
        <div class="alert alert-danger">Не можна блокувати власний акаунт!</div>
    </#if>

</@c.page>