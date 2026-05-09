<#import "common.ftl" as c>
<@c.page>

    <h2 class="mb-4">📋 Посадкові відомості</h2>

    <h5>Оберіть рейс:</h5>
    <div class="mb-4">
        <#if routes??>
            <#list routes as r>
                <a href="/boarding/view/${r.id}" class="btn btn-outline-primary mr-2 mb-2">
                    Рейс ${r.routeNumber} — ${r.finalDestination} (${r.departureTime})
                </a>
            </#list>
        </#if>
    </div>

    <#if boardingList??>
        <hr>
        <h4>Рейс ${route.routeNumber} → ${route.finalDestination}
            | Дата: ${travelDate}</h4>

        <#if boardingList.isClosed>
            <div class="alert alert-warning">Відомість закрита</div>
        </#if>

        <#if boardingList.passengers?size == 0>
            <div class="alert alert-info">На цей рейс квитків ще немає</div>
        <#else>
            <p>Пасажирів: <strong>${boardingList.passengers?size}</strong> |
                Посаджено: <strong>
                    <#assign boardedCount = 0>
                    <#list boardingList.passengers as bp>
                        <#if bp.hasBoarded><#assign boardedCount = boardedCount + 1></#if>
                    </#list>
                    ${boardedCount}
                </strong>
            </p>

            <table class="table table-bordered table-hover">
                <thead class="thead-dark">
                <tr>
                    <th>№ місця</th>
                    <th>ПІБ пасажира</th>
                    <th>Телефон</th>
                    <th>До зупинки</th>
                    <th>Тип квитка</th>
                    <th>Статус</th>
                    <th>Дія</th>
                </tr>
                </thead>
                <tbody>
                <#list boardingList.passengers as bp>
                    <tr class="${bp.hasBoarded?then('table-success','')}">
                        <td>${bp.ticket.seatNumber}</td>
                        <td>${bp.ticket.passengerName}</td>
                        <td>${bp.ticket.passengerPhone}</td>
                        <td>${bp.ticket.destination}</td>
                        <td>
                            <#if bp.ticket.isAdvancePurchase>
                                <span class="badge badge-info">Попередній</span>
                            <#else>
                                <span class="badge badge-secondary">Поточний</span>
                            </#if>
                        </td>
                        <td>
                            <#if bp.hasBoarded>
                                <span class="badge badge-success">✅ Посаджений</span>
                            <#else>
                                <span class="badge badge-warning">⏳ Очікує</span>
                            </#if>
                        </td>
                        <td>
                            <#if !boardingList.isClosed>
                                <form action="/boarding/board/${bp.id}" method="post">
                                    <button class="btn btn-sm
                        ${bp.hasBoarded?then('btn-secondary','btn-success')}">
                                        ${bp.hasBoarded?then('Скасувати','✅ Посадити')}
                                    </button>
                                </form>
                            </#if>
                        </td>
                    </tr>
                </#list>
                </tbody>
            </table>
        </#if>
    </#if>

</@c.page>