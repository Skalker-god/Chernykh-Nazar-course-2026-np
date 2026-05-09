<#import "../common.ftl" as c>
<@c.page>

    <h2 class="mb-4">🎫 Всі квитки</h2>

    <form action="/admin/tickets" method="get" class="form-inline mb-3">
        <input type="text" class="form-control mr-2" name="phone" placeholder="Пошук за телефоном" value="${searchPhone!}">
        <button type="submit" class="btn btn-primary">Знайти</button>
        <a href="/admin/tickets" class="btn btn-secondary ml-2">Скинути</a>
    </form>

    <div class="table-responsive">
        <table class="table table-bordered table-hover">
            <thead class="thead-dark">
            <tr>
                <th>ID</th>
                <th>Рейс</th>
                <th>Звідки</th>
                <th>Куди (пункт)</th>
                <th>Дата рейсу</th>
                <th>Час</th>
                <th>Пасажир</th>
                <th>Телефон</th>
                <th>Місце</th>
                <th>Тип</th>
                <th>Статус</th>
                <th>Час покупки</th>
            </tr>
            </thead>
            <tbody>
            <#if tickets??>
                <#list tickets as ticket>
                    <tr>
                        <td>${ticket.id}</td>
                        <td>${ticket.busRoute.routeNumber}</td>
                        <td>${ticket.busRoute.originCity}</td>
                        <td>${ticket.destination}</td>
                        <td>${ticket.travelDate}</td>
                        <td>${ticket.busRoute.departureTime}</td>
                        <td>${ticket.passengerName}</td>
                        <td>${ticket.passengerPhone}</td>
                        <td>${ticket.seatNumber}</td>
                        <td>
                            <#if ticket.isAdvancePurchase>
                                <span class="badge badge-info">Попередній</span>
                            <#else>
                                <span class="badge badge-secondary">Поточний</span>
                            </#if>
                        </td>
                        <td>
                            <#if ticket.status == 'ACTIVE'>
                                <span class="badge badge-success">Активний</span>
                            <#elseif ticket.status == 'RETURNED'>
                                <span class="badge badge-warning">Повернутий</span>
                            <#else>
                                <span class="badge badge-dark">Використаний</span>
                            </#if>
                        </td>
                        <td>${ticket.purchaseDateTime}</td>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>

</@c.page>