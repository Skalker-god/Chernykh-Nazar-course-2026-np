<#import "common.ftl" as c>
<@c.page>

    <h2>Оформлення квитка</h2>

    <#if route??>
        <div class="card mb-3">
            <div class="card-header bg-primary text-white">
                Рейс ${route.routeNumber}
            </div>
            <div class="card-body">
                <p><strong>Напрямок:</strong> ${route.finalDestination}</p>
                <p><strong>Час відправлення:</strong> ${route.departureTime}</p>
                <p><strong>Ціна:</strong> ${route.ticketPrice} грн</p>
                <p><strong>Вільні місця:</strong> ${route.availableSeats}</p>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <form action="/ticket/confirm" method="post">
                    <input type="hidden" name="routeId" value="${route.id}">

                    <div class="form-group">
                        <label>ПІБ пасажира</label>
                        <input type="text" name="passengerName" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label>Телефон</label>
                        <input type="tel" name="passengerPhone" class="form-control"
                               placeholder="+380501234567" required>
                    </div>

                    <div class="form-group">
                        <label>Номер місця (1-${route.totalSeats})</label>
                        <input type="number" name="seatNumber" class="form-control"
                               min="1" max="${route.totalSeats}" required>
                        <#if occupiedSeats?? && (occupiedSeats?size > 0)>
                            <small class="text-danger">Зайняті: ${occupiedSeats?join(", ")}</small>
                        </#if>
                    </div>

                    <div class="form-group">
                        <label>Пункт призначення</label>
                        <select name="destination" class="form-control" required>
                            <option value="">Оберіть...</option>
                            <option value="${route.finalDestination}">${route.finalDestination}</option>
                            <#if route.intermediateStops??>
                                <#list route.intermediateStops?split(",") as stop>
                                    <option value="${stop?trim}">${stop?trim}</option>
                                </#list>
                            </#if>
                        </select>
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

                    <button type="submit" class="btn btn-success btn-block">Оформити квиток</button>
                    <a href="/" class="btn btn-secondary btn-block">Назад</a>
                </form>
            </div>
        </div>
    </#if>

</@c.page>