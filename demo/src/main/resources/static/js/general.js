function formatDate(newsDate) {
    // Date nesnesini oluştur
    let date = new Date(newsDate);

    // Gün, Ay ve Yılı al
    let day = String(date.getDate()).padStart(2, '0');
    let month = String(date.getMonth() + 1).padStart(2, '0'); // Aylar 0'dan başladığı için +1 ekliyoruz
    let year = date.getFullYear();

    // Saat ve Dakikayı al
    let hours = String(date.getHours()).padStart(2, '0');
    let minutes = String(date.getMinutes()).padStart(2, '0');

    // İstenen formatta düzenle
    return `${day}.${month}.${year} ${hours}:${minutes}`;
}

function fetchNews() {
    $.ajax({
        url: '/api/news/getAllNews', // Your API endpoint
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            var newsContainer = $('#news-container');
            newsContainer.empty(); // Clear existing news
            var i=0;
            // Iterate through the news items and append them
            $.each(data, function(index, news) {
                i= i + 2;
                var newsHtml = `
                            <div class="col-lg-3 col-sm-6 wow fadeInUp" data-wow-delay="0.${i}s">
                                <div class="service-item rounded pt-3">
                                    <div class="p-4">
                                        <img class="img-fluid" src="Pictures/${news.id}.jpg" alt="">
                                        <br>
                                        <h5>${news.titleNews}</h5>
                                        <p>${news.newsDate}</p>
                                        <p>${news.content}</p>
                                    </div>
                                </div>
                            </div>
                            `;

                newsContainer.append(newsHtml);
            });
        },
        error: function(xhr, status, error) {
            console.error('Error fetching news:', error);
        }
    });
}

function fetchTripByCity() {
    var nereden = $("#nereden").val();
    var nereye = $("#nereye").val();

    if (nereden == "" && nereye == "") {
        fetchTrip();
        return;
    }else if(nereden == nereye){
        alert("Lütfen farklı şehirler seçin.");
        return;
    }else if (nereden=="" && nereye !=""){
        alert("Lütfen hangi ilden nakliye olacağını seçin.");
        return;
    }else if (nereden!="" && nereye ==""){
        alert("Lütfen hangi ile nakliye olacağını seçin.");
        return;
    }
    $.ajax({
        url: '/api/trip/getTripByCities', // Your API endpoint
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ nereden: nereden, nereye: nereye }),
        success: function(data) {
            var newsContainer = $('#showtrip');
            newsContainer.empty(); // Clear existing news

            // Iterate through the news items and append them
            var i=1;
            $.each(data, function(index, trip) {
                var newsHtml = `
                            <div class="col-lg-4 col-md-6 wow fadeInUp" data-wow-delay="0.${i}s">
                                <div class="package-item">
                                    <div class="overflow-hidden">
                                        <img class="img-fluid" src="Pictures/${trip.id}.jpg" alt="">
                                    </div>
                                    <div class="d-flex border-bottom">
                                        <small class="flex-fill text-center border-end py-2"><i class="fa fa-map-marker-alt text-primary me-2"></i>${trip.departureCity}</small>
                                        <small class="flex-fill text-center border-end py-2"><i class="fa fa-map-marker-alt text-primary me-2"></i>${trip.destinationCity}</small>
                                        <small class="flex-fill text-center py-2"><i class="fas fa-box text-primary me-2"></i>${trip.capacity}</small>
                                    </div>
                                    <div class="text-center p-4">
                                        <h3 class="mb-0">${trip.loadType}</h3>
                                        <p>${trip.tripDate}</p>
                                        <div class="d-flex justify-content-center mb-2">
                                            <a href="#" class="btn btn-sm btn-primary px-3 border-end" style="border-radius: 30px 0 0 30px;">Whatsapp</a>
                                            <a href="#" class="btn btn-sm btn-primary px-3" style="border-radius: 0 30px 30px 0;">${trip.phone}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                i+=2;
                newsContainer.append(newsHtml);
            });
        },
        error: function(xhr, status, error) {
            console.error('Bir hatayla karşılaşıldı:', error);
        }
    });
}

function fetchTrip() {
    $.ajax({
        url: '/api/trip/getAllTrip', // Your API endpoint
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            var newsContainer = $('#showtrip');
            newsContainer.empty(); // Clear existing news

            // Iterate through the news items and append them
            var i=1;
            $.each(data, function(index, trip) {
                var newsHtml = `
                            <div class="col-lg-4 col-md-6 wow fadeInUp" data-wow-delay="0.${i}s">
                                <div class="package-item">
                                    <div class="overflow-hidden">
                                        <img class="img-fluid" src="Pictures/${trip.id}.jpg" alt="">
                                    </div>
                                    <div class="d-flex border-bottom">
                                        <small class="flex-fill text-center border-end py-2"><i class="fa fa-map-marker-alt text-primary me-2"></i>${trip.departureCity}</small>
                                        <small class="flex-fill text-center border-end py-2"><i class="fa fa-map-marker-alt text-primary me-2"></i>${trip.destinationCity}</small>
                                        <small class="flex-fill text-center py-2"><i class="fas fa-box text-primary me-2"></i>${trip.capacity}</small>
                                    </div>
                                    <div class="text-center p-4">
                                        <h3 class="mb-0">${trip.loadType}</h3>
                                        <p>${trip.tripDate}</p>
                                        <div class="d-flex justify-content-center mb-2">
                                            <a href="#" class="btn btn-sm btn-primary px-3 border-end" style="border-radius: 30px 0 0 30px;">Whatsapp</a>
                                            <a href="#" class="btn btn-sm btn-primary px-3" style="border-radius: 0 30px 30px 0;">${trip.phone}</a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        `;
                i+=2;
                newsContainer.append(newsHtml);
            });
        },
        error: function(xhr, status, error) {
            console.error('Error fetching news:', error);
        }
    });
}

function login(){
    var email = $("#loginEmail").val();
    var password = $("#loginPassword").val();

    $.ajax({
        url: '/api/users/login',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ email: email, password: password }),
        success: function(response) {
            alert("Giriş başarılı!");
            // id_token'ı cookie olarak ayarla
            document.cookie = "user_id=" + response.trim() + "; path=/; max-age=" + (7 * 24 * 60 * 60) + "; secure; samesite=strict;";
            window.location.href = "/index.html";
        },
        error: function(error) {
            console.log("Giriş başarısız: " + error.responseText);
        }
    });
}

function signUp(){
    var name = $("#username").val();
    var surname = $("#surname").val();
    var email = $("#userEmail").val();
    var phone = $("#userPhone").val();
    var password = $("#userPassword").val();
    var userType = $("#userType").val();

    var user = {
        id: null,
        name: name,
        surname: surname,
        email: email,
        phone: phone,
        password: password,
        userType: userType
    };

    $.ajax({
        url: '/api/users/register',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(user),
        success: function(response) {
            alert(response.trim());
        },
        error: function(error) {
            alert("KAYIT başarısız: " + error.responseText);
        }
    });
}

function logout(){
    document.cookie = "user_id=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC; secure; samesite=strict;";
    window.location.href = "/index.html";
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

function createTrip(){
    var departureCity = $("#departureCity").val();
    var departureDistrict = $("#departureDistrict").val();
    var destinationCity = $("#destinationCity").val();
    var destinationDistrict = $("#destinationDistrict").val();
    var loadType = $("#loadType").val();
    var departureDate = $("#departureDate").val();
    var capacity = $("#capacity").val();
    var image = $("#image")[0].files[0]; // Dosya seçimini al

    var trip = {
        id: null,
        departureCity: departureCity,
        departureDistrict: departureDistrict,
        destinationCity: destinationCity,
        destinationDistrict: destinationDistrict,
        departureDate: departureDate,
        loadType: loadType,
        capacity: capacity
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
        },error: function(error) {
            alert("KAYIT başarısız: " + error.responseText);
        }
    });
}



