<#import "common.ftl" as c>
<@c.page>

    <h2>🛒 Кошик квитків</h2>

    <#if cart?? && !cart.empty>
        <div class="card mb-4">
            <div class="card-header bg-primary text-white">
                <h5>Квитки у кошику: ${cart.itemCount}</h5>
            </div>
            <div class="card-body">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Рейс</th>
                        <th>Напрямок</th>
                        <th>Пункт</th>
                        <th>Час</th>
                        <th>Місце</th>
                        <th>Ціна</th>
                        <th>Дія</th>
                    </tr>
                    </thead>
                    <tbody>
                    <#list cart.items as item>
                        <tr>
                            <td><strong>${item.route.routeNumber}</strong></td>
                            <td>${item.route.finalDestination}</td>
                            <td>${item.destination}</td>
                            <td>${item.route.departureTime}</td>
                            <td>№${item.seatNumber}</td>
                            <td><strong>${item.price} грн</strong></td>
                            <td>
                                <form action="/cart/remove" method="post" style="display:inline;">
                                    <input type="hidden" name="routeId" value="${item.route.id}">
                                    <input type="hidden" name="seatNumber" value="${item.seatNumber}">
                                    <button type="submit" class="btn btn-sm btn-danger">
                                        🗑️ Видалити
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </#list>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="5" class="text-right"><strong>Всього:</strong></td>
                        <td colspan="2"><strong>${cart.total} грн</strong></td>
                    </tr>
                    </tfoot>
                </table>
            </div>
        </div>

        <div class="row">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h5>Дані пасажира</h5>
                    </div>
                    <div class="card-body">
                        <form action="/cart/checkout" method="post">
                            <div class="form-group">
                                <label>ПІБ пасажира</label>
                                <input type="text" name="passengerName" class="form-control"
                                       placeholder="Іванов Іван Іванович" required>
                                <small class="form-text text-muted">Вказане ім'я буде на всіх квитках</small>
                            </div>

                            <div class="form-group">
                                <label>Телефон</label>
                                <input type="tel" name="passengerPhone" class="form-control"
                                       placeholder="+380501234567" pattern="\+380[0-9]{9}" required>
                            </div>

                            <div class="form-group">
                                <div class="form-check">
                                    <input type="checkbox" class="form-check-input"
                                           id="isAdvance" name="isAdvance" value="true">
                                    <label class="form-check-label" for="isAdvance">
                                        Попередній продаж
                                    </label>
                                </div>
                            </div>

                            <button type="submit" class="btn btn-success btn-lg btn-block">
                                ✅ Оформити всі квитки (${cart.total} грн)
                            </button>
                        </form>
                    </div>
                </div>
            </div>

            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5>Підсумок</h5>
                        <p>Квитків: <strong>${cart.itemCount}</strong></p>
                        <p>До сплати: <strong>${cart.total} грн</strong></p>
                        <hr>
                        <form action="/cart/clear" method="post">
                            <button type="submit" class="btn btn-outline-danger btn-block">
                                🗑️ Очистити кошик
                            </button>
                        </form>
                        <a href="/" class="btn btn-outline-secondary btn-block mt-2">
                            Продовжити покупки
                        </a>
                    </div>
                </div>
            </div>
        </div>

    <#else>
        <div class="alert alert-info text-center">
            <h4>Кошик порожній</h4>
            <p>Оберіть квитки на головній сторінці</p>
            <a href="/" class="btn btn-primary mt-2">До розкладу рейсів</a>
        </div>
    </#if>

</@c.page>