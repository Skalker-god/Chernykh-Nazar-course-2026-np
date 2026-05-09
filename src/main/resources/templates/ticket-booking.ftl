<#import "common.ftl" as c>
<@c.page>

    <h2>Оформлення квитка</h2>

    <#if error??>
        <div class="alert alert-danger">❌ ${error}</div>
    </#if>

    <#if route??>
        <div class="card mb-3">
            <div class="card-header bg-primary text-white">
                Рейс ${route.routeNumber} — ${route.originCity} → ${route.finalDestination}
            </div>
            <div class="card-body">
                <p><strong>📅 Дата відправлення:</strong> ${route.departureDate}</p>
                <p><strong>⏰ Час відправлення:</strong> ${route.departureTime}</p>
                <p><strong>💰 Ціна:</strong> ${route.ticketPrice} грн</p>
                <p><strong>💺 Вільні місця:</strong> ${route.availableSeats}</p>
            </div>
        </div>

        <div class="card">
            <div class="card-body">
                <div class="form-group">
                    <label>Номер місця (1-${route.totalSeats})</label>
                    <input type="number" id="seatNumber" class="form-control"
                           min="1" max="${route.totalSeats}" value="1" required>
                    <#if occupiedSeats?? && (occupiedSeats?size > 0)>
                        <small class="text-danger">Зайняті: ${occupiedSeats?join(", ")}</small>
                    </#if>
                </div>

                <div class="form-group">
                    <label>Пункт призначення</label>
                    <select id="destination" class="form-control" required>
                        <option value="">Оберіть...</option>
                        <option value="${route.finalDestination}">${route.finalDestination}</option>
                        <#if route.intermediateStops??>
                            <#list route.intermediateStops?split(",") as stop>
                                <option value="${stop?trim}">${stop?trim}</option>
                            </#list>
                        </#if>
                    </select>
                </div>

                <hr>
                <form action="/cart/add" method="post" class="mb-2">
                    <input type="hidden" name="routeId" value="${route.id}">
                    <input type="hidden" name="destination" id="destinationCart">
                    <input type="hidden" name="seatNumber" id="seatNumberCart">
                    <button type="submit" class="btn btn-warning btn-block" onclick="return copyValues('Cart')">
                        🛒 Додати до кошика
                    </button>
                </form>

                <button type="button" class="btn btn-success btn-block" data-toggle="modal" data-target="#buyNowModal">
                    ✅ Купити зараз
                </button>
                <a href="/" class="btn btn-secondary btn-block mt-2">Назад</a>
            </div>
        </div>
    </#if>

    <div class="modal fade" id="buyNowModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Підтвердження квитка</h5>
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <form action="/ticket/confirm" method="post">
                    <div class="modal-body">
                        <input type="hidden" name="routeId" value="${route.id}">
                        <input type="hidden" name="destination" id="destinationDirect">
                        <input type="hidden" name="seatNumber" id="seatNumberDirect">

                        <#if user??>
                            <div class="alert alert-info">
                                <strong>Пасажир:</strong> ${user.fullName} (${user.phone})
                            </div>
                            <input type="hidden" name="passengerName" value="${user.fullName}">
                            <input type="hidden" name="passengerPhone" value="${user.phone}">
                        <#else>
                            <div class="form-group">
                                <label>ПІБ пасажира</label>
                                <input type="text" name="passengerName" class="form-control" required>
                            </div>
                            <div class="form-group">
                                <label>Телефон</label>
                                <input type="tel" name="passengerPhone" class="form-control" required>
                            </div>
                        </#if>

                        <div class="form-check">
                            <input type="checkbox" class="form-check-input" id="isAdvance" name="isAdvance" value="true">
                            <label class="form-check-label" for="isAdvance">Попередній продаж</label>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Скасувати</button>
                        <button type="submit" class="btn btn-success" onclick="return copyValues('Direct')">Оформити</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        function copyValues(type) {
            let seat = document.getElementById('seatNumber').value;
            let dest = document.getElementById('destination').value;
            if (!dest) { alert('Оберіть пункт призначення'); return false; }
            if (type === 'Cart') {
                document.getElementById('seatNumberCart').value = seat;
                document.getElementById('destinationCart').value = dest;
            } else {
                document.getElementById('seatNumberDirect').value = seat;
                document.getElementById('destinationDirect').value = dest;
            }
            return true;
        }
    </script>

</@c.page>