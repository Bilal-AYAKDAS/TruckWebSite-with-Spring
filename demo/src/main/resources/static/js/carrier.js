
function fetchTrips() {
    $.ajax({
        url: '/api/trip/getTripByUserId', // Your API endpoint
        type: 'GET',
        dataType: 'json',
        data: { userId: getCookie("user_id") },
        success: function(data) {
            var tripTableBody = $('#news-table-body');
            tripTableBody.empty(); // Var olan haberleri temizle

            // Haberleri iterasyonla ekle
            $.each(data, function(index, trip) {
                const delay = 0.1 + (index % 4) * 0.2;  // Animasyon gecikmesi
                const imageUrl = `Pictures/${trip.id}.jpg`; // Haber resmi URL'si (news.id'ye göre)

                const newsRowHtml = `
                    <tr class="wow fadeInUp" data-wow-delay="${delay}s">
                        <th scope="row">${index + 1}</th>
                        <td>
                            <img src="${imageUrl}"  class="img-fluid" style="max-width: 100px;">
                        </td>
                        <td>${trip.departureCity}</td>
                        <td>${trip.destinationCity}</td>
                        <td>${trip.loadType}</td>
                        <td>${trip.capacity}</td>
                        <td>${trip.tripDate}</td>
                        <td><a class="btn btn-sm btn-primary mx-1" href="#" onclick="openTripSave('${trip.id}')"><i class="fas fa-pencil-alt"></i> Düzenle</a></td>
                        <td><a class="btn btn-sm btn-danger mx-1" href="#" onclick="tripDelete('${trip.id}')"><i class="fas fa-trash"></i> Sil</a></td>
                    </tr>
                `;
                tripTableBody.append(newsRowHtml);
            });
        },
        error: function(xhr, status, error) {
            console.error('Haberleri alırken hata oluştu:', error);
        }
    });
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function formatDate(inputDate) {
    const date = new Date(inputDate);


    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2); // Ayları 0-11 arası döndüğü için +1 eklenir
    const day = ('0' + date.getDate()).slice(-2);
    const hours = ('0' + date.getHours()).slice(-2);
    const minutes = ('0' + date.getMinutes()).slice(-2);

    // İstenen format: yyyy/MM/dd HH:mm
    const formattedDate = `${year}/${month}/${day} ${hours}:${minutes}`;

    return formattedDate;
}

function tripPut(){
    var userId = getCookie("user_id");
    var id = $("#tripId").val();
    var nereden = $("#nereden").val();
    var nereye = $("#nereye").val();
    var tripDate = formatDate($("#tripDate").val());
    var yukTipi = $("#yukTipi").val();
    var kapasite = $("#kapasite").val();



    var image = $("#image")[0].files[0]; // Dosya seçimini al

    var trip = {
        userId:userId,
        id: id,
        departureCity: nereden,
        destinationCity: nereye,
        tripDate: tripDate,
        loadType: yukTipi,
        capacity: kapasite
    };

    var formData = new FormData();
    formData.append('trip', new Blob([JSON.stringify(trip)], { type: 'application/json' }));
    formData.append('image', image);

    $.ajax({
        url: '/api/trip/createTrip',
        type: 'POST',
        data: formData,
        processData: false, // FormData kullanırken false olmalı
        contentType: false, // FormData kullanırken false olmalı
        success: function(response) {
            alert(response.trim());
            fetchTrips();
        },error: function(error) {
            console.log(error.responseText);
            alert("KAYIT başarısız: " + error.responseText);
        }
    });
}

function openTripSave(id) {
    // Modal elementlerini seç
    var modal = document.getElementById("myModal");
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    document.body.classList.add("modal-open");

    $("#tripId").val(id);
    $("#image").val(null);
    $("#nereden").val("");
    $("#nereye").val("");
    $("#yukTipi").val("");
    $("#kapasite").val("");
    $("#tripDate").val("");

    if (id!="-1"){
        getTripById(id);
    }
    // X işaretine tıklayınca modal kapat
    span.onclick = function() {
        modal.style.display = "none";
        document.body.classList.remove("modal-open");
    }
    // Modal dışında bir yere tıklayınca modal kapat
    window.onclick = function(event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
}

function getTripById(id) {

    $.ajax({
        url: '/api/trip/getTripById',
        type: 'POST',
        dataType: 'json',
        data: { id: id },
        success: function(trip) {
            $("#tripId").val(trip.id);
            $("#nereden").val(trip.departureCity);
            $("#nereye").val(trip.destinationCity);
            $("#yukTipi").val(trip.loadType);
            $("#kapasite").val(trip.capacity);
            $("#tripDate").val(trip.tripDate.replaceAll("/","-"));


        },
        error: function(xhr, status, error) {
            console.error('Haber getirilirken hata oluştu:', error);
        }
    });
}

function tripDelete(id) {
    $.ajax({
        url: '/api/trip/deleteTrip',
        type: 'DELETE',
        data: { id: id },
        success: function(response) {
            alert(response.trim());
            fetchAdminNews();
        }
    });
}

function getUserMenu() {
    var userId = getCookie("user_id");
    if (userId) {
        $.ajax({
            url: '/api/users/getUserByID/' + userId,
            type: 'GET',
            contentType: 'application/json',
            success: function(user) {
                if (user && user.userType == "ADMIN") {
                    $("#transportList").show();
                    $("#newsList").show();
                } else if (user && user.userType == "CARRIER") {
                    $("#newsList").hide();
                    $("#transportList").show();
                } else {
                    alert(user.userType);
                    $("#newsList").hide();
                    $("#transportList").hide();
                }
            },
            error: function(error) {
                console.log("Kullanıcı menüsü alınamadı: " + error.responseText);
                $("#newsList").hide();
                $("#transportList").hide();
            }
        });
    }

}

function getUserName() {
    var userId = getCookie("user_id");
    if (userId) {
        $.ajax({
            url: '/api/users/getUserByID/' + userId,
            type: 'GET',
            contentType: 'application/json',
            success: function(user) {
                if (user && user.name) {
                    $("#user-name").text(user.name + " " + user.surname);
                    $("#user-name").show();
                    $("#loginBtn").text("Çıkış Yap");
                    $("#loginBtn").attr("onclick", "logout()");
                    $("#loginBtn").addClass("btn-danger");
                } else {
                    $("#user-name").hide();
                }
            },
            error: function(error) {
                console.log("Kullanıcı adı alınamadı: " + error.responseText);
                $("#user-name").hide();
            }
        });
    } else {
        $("#user-name").hide();
    }
}


function logout(){
    document.cookie = "user_id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC; secure; samesite=strict;";
    window.location.href = "/index.html";
}