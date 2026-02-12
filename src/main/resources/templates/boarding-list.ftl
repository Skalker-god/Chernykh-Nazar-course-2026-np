<#import "common.ftl" as c>
<@c.page>

    <h2>Посадкові відомості</h2>

    <div class="card mb-4">
        <div class="card-header">Оберіть рейс</div>
        <div class="card-body">
            <#if routes?? && (routes?size > 0)>
                <div class="list-group">
                    <#list routes as route>
                        <a href="/boarding/view/${route.id}" class="list-group-item list-group-item-action">
                            <strong>Рейс ${route.routeNumber}</strong> - ${route.finalDestination}
                            (${route.departureTime})
                        </a>
                    </#list>
                </div>
            <#else>
                <p>Немає активних рейсів</p>
            </#if>
        </div>
    </div>

    <#if boardingList??>
        <div class="card">
            <div class="card-header bg-primary text-white">
                Посадкова відомість: Рейс ${route.routeNumber} (${travelDate})
            </div>
            <div class="card-body">
                <p><strong>Напрямок:</strong> ${route.finalDestination}</p>
                <p><strong>Час відправлення:</strong> ${route.departureTime}</p>

                <#if boardingList.passengers?? && (boardingList.passengers?size > 0)>
                    <table class="table table-striped mt-3">
                        <thead class="thead-dark">
                        <tr>
                            <th>№</th>
                            <th>ПІБ</th>
                            <th>Телефон</th>
                            <th>Місце</th>
                            <th>Пункт</th>
                            <th>Статус</th>
                        </tr>
                        </thead>
                        <tbody>
                        <#list boardingList.passengers as passenger>
                            <tr>
                                <td>${passenger?counter}</td>
                                <td>${passenger.ticket.passengerName}</td>
                                <td>${passenger.ticket.passengerPhone}</td>
                                <td>№${passenger.ticket.seatNumber}</td>
                                <td>${passenger.ticket.destination}</td>
                                <td>
                                    <#if passenger.hasBoarded>
                                        <span class="badge badge-success">Посаджений</span>
                                    <#else>
                                        <span class="badge badge-warning">Очікується</span>
                                    </#if>
                                </td>
                            </tr>
                        </#list>
                        </tbody>
                    </table>
                    <p><strong>Всього пасажирів:</strong> ${boardingList.passengers?size}</p>
                <#else>
                    <div class="alert alert-info">Немає пасажирів</div>
                </#if>
            </div>
        </div>
    <#elseif route??>
        <div class="alert alert-warning">Посадкову відомість не створено</div>
    </#if>

</@c.page>