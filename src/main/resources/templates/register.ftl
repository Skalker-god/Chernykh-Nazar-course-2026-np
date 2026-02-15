<#import "common.ftl" as c>
<@c.page>

    <div class="row">
        <div class="col-md-6 offset-md-3">
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h4 class="mb-0">📝 Реєстрація</h4>
                </div>
                <div class="card-body">
                    <#if error??>
                        <div class="alert alert-danger">
                            ${error}
                        </div>
                    </#if>

                    <form action="/register" method="post">
                        <div class="form-group">
                            <label>ПІБ</label>
                            <input type="text" name="fullName" class="form-control"
                                   placeholder="Іванов Іван Іванович"
                                   required>
                        </div>

                        <div class="form-group">
                            <label>Телефон</label>
                            <input type="tel" name="phone" class="form-control"
                                   placeholder="+380501234567"
                                   pattern="\+380[0-9]{9}"
                                   required>
                            <small class="form-text text-muted">
                                Використовуватиметься як логін
                            </small>
                        </div>

                        <div class="form-group">
                            <label>Пароль</label>
                            <input type="password" name="password" class="form-control"
                                   placeholder="Мінімум 6 символів"
                                   minlength="6"
                                   required>
                        </div>

                        <button type="submit" class="btn btn-success btn-block">
                            Зареєструватися
                        </button>
                    </form>

                    <hr>
                    <p class="text-center mb-0">
                        Вже є акаунт? <a href="/login">Увійти</a>
                    </p>
                </div>
            </div>
        </div>
    </div>

</@c.page>