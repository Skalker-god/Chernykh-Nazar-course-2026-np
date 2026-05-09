<#import "common.ftl" as c>
<@c.page>

    <h2>👤 Особистий кабінет</h2>

    <#if RequestParameters.cancelled??>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>✅ Квиток успішно скасовано!</strong> Вільне місце повернуто.
            <button type="button" class="close" data-dismiss="alert"><span>&times;</span></button>
        </div>
    </#if>

    <div class="row">
        <div class="col-md-4">
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5>Інформація про акаунт</h5>
                </div>
                <div class="card-body">
                    <p><strong>ПІБ:</strong> ${user.fullName}</p>
                    <p><strong>Телефон:</strong> ${user.phone}</p>
                    <p><strong>Роль:</strong>
                        <#if user.role == 'ADMIN'>
                            <span class="badge badge-danger">Адміністратор</span>
                        <#elseif user.role == 'CASHIER'>
                            <span class="badge badge-warning">Касир</span>
                        <#else>
                            <span class="badge badge-info">Пасажир</span>
                        </#if>
                    </p>
                    <p><strong>Дата реєстрації:</strong> ${formattedDate}</p>
                    <p><strong>Статус акаунту:</strong>
                        <#if user.isActive>
                            <span class="badge badge-success">Активний</span>
                        <#else>
                            <span class="badge badge-danger">Заблокований</span>
                        </#if>
                        <#if !user.isActive>
                            <br><small class="text-muted">⚠️ Ви не можете купувати квитки. Зверніться до адміністратора.</small>
                        </#if>
                    </p>
                </div>
            </div>

            <div class="card">
                <div class="card-header">
                    <h5>Швидкі дії</h5>
                </div>
                <div class="card-body">
                    <a href="/" class="btn btn-primary btn-block">🚌 Розклад</a>
                    <a href="/cart" class="btn btn-warning btn-block">🛒 Кошик</a>
                    <#if user.role == 'CASHIER' || user.role == 'ADMIN'>
                        <a href="/boarding" class="btn btn-info btn-block">📋 Посадкові відомості</a>
                    </#if>
                    <hr>
                    <a href="/logout" class="btn btn-danger btn-block">🚪 Вийти</a>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h5>🎫 Мої квитки</h5>
                </div>
                <div class="card-body">
                    <#if tickets?? && (tickets?size > 0)>
                        <div class="table-responsive">
                            <table class="table table-hover">
                                <thead class="thead-light">
                                <tr>
                                    <th>Рейс</th>
                                    <th>Маршрут</th>
                                    <th>Ваша зупинка</th>
                                    <th>Дата</th>
                                    <th>Час</th>
                                    <th>Місце</th>
                                    <th>Ціна</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list tickets as ticket>
                                    <tr>
                                        <td><strong>${ticket.busRoute.routeNumber}</strong></td>
                                        <td>${ticket.busRoute.originCity} → ${ticket.busRoute.finalDestination}</td>
                                        <td>${ticket.destination}</td>
                                        <td>${ticket.travelDate}</td>
                                        <td>${ticket.busRoute.departureTime}</td>
                                        <td><span class="badge badge-info">№${ticket.seatNumber}</span></td>
                                        <td><strong>${ticket.busRoute.ticketPrice} грн</strong></td>
                                        <td>
                                            <button type="button"
                                                    class="btn btn-sm btn-danger"
                                                    data-toggle="modal"
                                                    data-target="#cancelModal${ticket.id}">
                                                🗑️ Скасувати
                                            </button>
                                        </td>
                                    </tr>

                                    <!-- Модальне вікно підтвердження -->
                                    <div class="modal fade" id="cancelModal${ticket.id}" tabindex="-1">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">Підтвердження скасування</h5>
                                                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                                                </div>
                                                <div class="modal-body">
                                                    <p>Ви впевнені, що хочете скасувати квиток?</p>
                                                    <div class="alert alert-warning">
                                                        <strong>Квиток №${ticket.id}</strong><br>
                                                        ${ticket.busRoute.originCity} → ${ticket.destination}<br>
                                                        ${ticket.travelDate} о ${ticket.busRoute.departureTime}<br>
                                                        Місце №${ticket.seatNumber}
                                                    </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Ні</button>
                                                    <form action="/profile/cancel-ticket" method="post" style="display:inline;">
                                                        <input type="hidden" name="ticketId" value="${ticket.id}">
                                                        <button type="submit" class="btn btn-danger">Так, скасувати</button>
                                                    </form>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </#list>
                                </tbody>
                            </table>
                        </div>
                        <div class="alert alert-info mt-3">
                            💡 Квиток можна скасувати не пізніше ніж за 2 години до відправлення.
                        </div>
                    <#else>
                        <div class="alert alert-warning text-center">
                            <h5>У вас поки немає активних квитків</h5>
                            <a href="/" class="btn btn-primary mt-2">🚌 Переглянути розклад</a>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
    </div>

</@c.page>