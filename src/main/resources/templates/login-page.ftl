<#import "common.ftl" as c>
<@c.page>

    <div class="row">
        <div class="col-md-4 offset-md-4">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">🔐 Вхід до системи</h4>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">
                            ${error}
                        </div>
                    </#if>

                    <!-- ★ НОВЕ: повідомлення про блокування -->
                    <#if RequestParameters.blocked??>
                        <div class="alert alert-danger">
                            ⛔ Ваш обліковий запис заблоковано. Зверніться до адміністратора.
                        </div>
                    </#if>

                    <form action="/login" method="post">
                        <div class="form-group">
                            <label>Телефон</label>
                            <input type="tel" name="phone" class="form-control"
                                   placeholder="+380501234567"
                                   pattern="\+380[0-9]{9}"
                                   required>
                        </div>

                        <div class="form-group">
                            <label>Пароль</label>
                            <input type="password" name="password" class="form-control"
                                   placeholder="Введіть пароль"
                                   required>
                        </div>

                        <button type="submit" class="btn btn-primary btn-block">
                            Увійти
                        </button>
                    </form>

                    <hr>
                    <p class="text-center mb-0">
                        Ще не зареєстровані? <a href="/register">Створити акаунт</a>
                    </p>
                </div>
            </div>
        </div>
    </div>

</@c.page>