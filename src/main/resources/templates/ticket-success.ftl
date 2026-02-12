<#import "common.ftl" as c>
<@c.page>

    <div class="alert alert-success text-center">
        <h2>✅ Квиток успішно оформлено!</h2>
    </div>

    <#if ticket??>
        <div class="card">
            <div class="card-header bg-success text-white">
                Квиток №${ticket.id}
            </div>
            <div class="card-body">
                <h5>Інформація про рейс:</h5>
                <p><strong>Рейс:</strong> ${ticket.busRoute.routeNumber}</p>
                <p><strong>Напрямок:</strong> ${ticket.busRoute.finalDestination}</p>
                <p><strong>Ваш пункт:</strong> ${ticket.destination}</p>
                <p><strong>Час відправлення:</strong> ${ticket.busRoute.departureTime}</p>
                <p><strong>Дата поїздки:</strong> ${ticket.travelDate}</p>
                <p><strong>Місце:</strong> №${ticket.seatNumber}</p>

                <hr>

                <h5>Пасажир:</h5>
                <p><strong>ПІБ:</strong> ${ticket.passengerName}</p>
                <p><strong>Телефон:</strong> ${ticket.passengerPhone}</p>

                <hr>

                <p><strong>Ціна:</strong> ${ticket.busRoute.ticketPrice} грн</p>
                <p class="text-muted">
                    <small>Час покупки: ${ticket.purchaseDateTime}</small>
                </p>
            </div>
        </div>

        <div class="mt-3 text-center">
            <a href="/" class="btn btn-primary">На головну</a>
        </div>
    </#if>

</@c.page>