<#import "common.ftl" as c>
<@c.page>

    <#if RequestParameters.purchased??>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>✅ Успіх!</strong> Всі квитки з кошика успішно оформлені!
            <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
        </div>
    </#if>

    <h2 class="mb-4">🚌 Пошук автобусних рейсів</h2>

    <div class="card mb-5">
        <div class="card-body">
            <form action="/search" method="get" class="row">
                <div class="col-md-4">
                    <label>📌 Звідки</label>
                    <input list="originCities" name="origin" class="form-control"
                           placeholder="Місто відправлення" value="${searchOrigin!''}"
                           autocomplete="off">
                    <datalist id="originCities">
                        <#if originCities??>
                            <#list originCities as city>
                                <option value="${city}">${city}</option>
                            </#list>
                        </#if>
                    </datalist>
                </div>
                <div class="col-md-4">
                    <label>🎯 Куди</label>
                    <input list="destCities" name="destination" class="form-control"
                           placeholder="Місто призначення" value="${searchDestination!''}"
                           autocomplete="off">
                    <datalist id="destCities">
                        <#if destinationCities??>
                            <#list destinationCities as city>
                                <option value="${city}">${city}</option>
                            </#list>
                        </#if>
                    </datalist>
                </div>
                <div class="col-md-3">
                    <label>📅 Дата</label>
                    <input type="date" name="date" class="form-control"
                           value="${searchDate!''}" min="${today}">
                </div>
                <div class="col-md-1 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">🔍 Знайти</button>
                </div>
            </form>
        </div>
    </div>

    <#if searchPerformed?? && (routes?size == 0)>
        <div class="alert alert-warning">⚠️ Рейсів за заданими критеріями не знайдено. Спробуйте інші дати або напрямки.</div>
    </#if>

    <#if routes?? && (routes?size > 0)>
        <h4>📋 Знайдені рейси</h4>
        <div class="table-responsive">
            <table class="table table-bordered table-hover">
                <thead class="thead-dark">
                <tr>
                    <th>Рейс</th>
                    <th>Звідки</th>
                    <th>Куди</th>
                    <th>Проміжні зупинки</th>
                    <th>Дата відправлення</th>
                    <th>Час</th>
                    <th>Вільні місця</th>
                    <th>Ціна</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <#list routes as route>
                    <tr>
                        <td><strong>${route.routeNumber}</strong></td>
                        <td>${route.originCity}</td>
                        <td>${route.finalDestination}</td>
                        <td>${route.intermediateStops!'—'}</td>
                        <td>${route.departureDate}</td>
                        <td>${route.departureTime}</td>
                        <td>${route.availableSeats} / ${route.totalSeats}</td>
                        <td>${route.ticketPrice} грн</td>
                        <td>
                            <#if route.availableSeats gt 0>
                                <a href="/ticket/book/${route.id}" class="btn btn-sm btn-success">Купити</a>
                            <#else>
                                <button class="btn btn-sm btn-secondary" disabled>Немає місць</button>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    <#elseif !searchPerformed??>
        <div class="alert alert-info">✨ Введіть маршрут і дату для пошуку, або перегляньте найближчі рейси нижче.</div>
        <h4>🕒 Найближчі рейси</h4>
        <div class="table-responsive">
            <table class="table table-striped">
                <thead class="thead-light">
                <tr>
                    <th>Рейс</th><th>Звідки</th><th>Куди</th><th>Дата</th><th>Час</th><th>Місця</th><th>Ціна</th><th></th>
                </tr>
                </thead>
                <tbody>
                <#list routes as route>
                    <tr>
                        <td>${route.routeNumber}</td>
                        <td>${route.originCity}</td>
                        <td>${route.finalDestination}</td>
                        <td>${route.departureDate}</td>
                        <td>${route.departureTime}</td>
                        <td>${route.availableSeats}</td>
                        <td>${route.ticketPrice} грн</td>
                        <td><a href="/ticket/book/${route.id}" class="btn btn-sm btn-primary">Вибрати</a></td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </div>
    </#if>

</@c.page>