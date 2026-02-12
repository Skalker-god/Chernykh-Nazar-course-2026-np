<#import "common.ftl" as c>
<@c.page>

    <h2>Розклад автобусних рейсів</h2>
    <p class="text-muted">Поточний час: ${currentTime}</p>

    <div class="row mb-3">
        <div class="col-md-6">
            <form action="/search" method="get">
                <div class="input-group">
                    <input type="text" name="destination" class="form-control"
                           placeholder="Пункт призначення" value="${searchQuery!''}">
                    <div class="input-group-append">
                        <button class="btn btn-primary" type="submit">Знайти</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <#if routes?? && (routes?size > 0)>
        <table class="table table-striped">
            <thead class="thead-dark">
            <tr>
                <th>Рейс</th>
                <th>Кінцевий пункт</th>
                <th>Проміжні зупинки</th>
                <th>Відправлення</th>
                <th>Вільні місця</th>
                <th>Ціна</th>
                <th>Дія</th>
            </tr>
            </thead>
            <tbody>
            <#list routes as route>
                <tr>
                    <td><strong>${route.routeNumber}</strong></td>
                    <td>${route.finalDestination}</td>
                    <td>${route.intermediateStops!'—'}</td>
                    <td>${route.departureTime}</td>
                    <td>${route.availableSeats} / ${route.totalSeats}</td>
                    <td>${route.ticketPrice} грн</td>
                    <td>
                        <#if route.availableSeats gt 0>
                            <a href="/ticket/book/${route.id}" class="btn btn-sm btn-success">
                                Купити квиток
                            </a>
                        <#else>
                            <button class="btn btn-sm btn-secondary" disabled>Немає місць</button>
                        </#if>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    <#else>
        <div class="alert alert-warning">Рейсів не знайдено</div>
    </#if>

</@c.page>