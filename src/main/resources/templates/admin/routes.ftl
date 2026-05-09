<#import "../common.ftl" as c>
<@c.page>

    <h2 class="mb-4">⚙️ Управління рейсами</h2>

    <#if successMessage??>
        <div class="alert alert-success">${successMessage}</div>
    </#if>

    <h4>Список рейсів</h4>
    <div class="table-responsive">
        <table class="table table-bordered table-hover">
            <thead class="thead-dark">
            <tr>
                <th>№</th><th>Звідки</th><th>Куди</th><th>Проміжні</th>
                <th>Дата</th><th>Час</th><th>Місць</th><th>Вільно</th><th>Ціна</th><th>Статус</th><th>Дії</th>
            </tr>
            </thead>
            <tbody>
            <#if routes??>
                <#list routes as route>
                    <tr>
                        <td>${route.routeNumber}</td>
                        <td>${route.originCity}</td>
                        <td>${route.finalDestination}</td>
                        <td>${route.intermediateStops!"-"}</td>
                        <td>${route.departureDate}</td>
                        <td>${route.departureTime}</td>
                        <td>${route.totalSeats}</td>
                        <td>${route.availableSeats}</td>
                        <td>${route.ticketPrice}</td>
                        <td>
                            <#if route.isActive>
                                <span class="badge badge-success">Активний</span>
                            <#else>
                                <span class="badge badge-danger">Неактивний</span>
                            </#if>
                        </td>
                        <td>
                            <form action="/admin/routes/toggle/${route.id}" method="post" style="display:inline">
                                <button class="btn btn-sm ${route.isActive?then('btn-warning','btn-success')}">
                                    ${route.isActive?then('Деактивувати','Активувати')}
                                </button>
                            </form>
                            <form action="/admin/routes/delete/${route.id}" method="post" style="display:inline">
                                <button class="btn btn-sm btn-danger" onclick="return confirm('Видалити рейс?')">Видалити</button>
                            </form>
                        </td>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>

    <hr>
    <h4>➕ Додати новий рейс</h4>
    <form action="/admin/routes/add" method="post">
        <div class="form-row">
            <div class="form-group col-md-2">
                <label>Номер рейсу</label>
                <input type="text" class="form-control" name="routeNumber" required>
            </div>
            <div class="form-group col-md-3">
                <label>Місто відправлення</label>
                <input type="text" class="form-control" name="originCity" required>
            </div>
            <div class="form-group col-md-3">
                <label>Кінцевий пункт</label>
                <input type="text" class="form-control" name="finalDestination" required>
            </div>
            <div class="form-group col-md-4">
                <label>Проміжні зупинки (через кому)</label>
                <input type="text" class="form-control" name="intermediateStops" placeholder="Львів, Рівне">
            </div>
        </div>
        <div class="form-row">
            <div class="form-group col-md-2">
                <label>Дата рейсу</label>
                <input type="date" class="form-control" name="departureDate" required>
            </div>
            <div class="form-group col-md-2">
                <label>Час відправлення</label>
                <input type="time" class="form-control" name="departureTime" required>
            </div>
            <div class="form-group col-md-2">
                <label>Кількість місць</label>
                <input type="number" class="form-control" name="totalSeats" value="45" min="1" required>
            </div>
            <div class="form-group col-md-2">
                <label>Ціна (грн)</label>
                <input type="number" step="0.01" class="form-control" name="ticketPrice" min="0" required>
            </div>
        </div>
        <button type="submit" class="btn btn-primary">➕ Додати рейс</button>
    </form>

</@c.page>