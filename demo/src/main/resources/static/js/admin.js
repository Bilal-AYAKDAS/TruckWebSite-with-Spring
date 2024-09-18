function fetchAdminNews() {
    $.ajax({
        url: '/api/news/getAllNews', // Your API endpoint
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            var newsTableBody = $('#news-table-body');
            newsTableBody.empty(); // Var olan haberleri temizle

            // Haberleri iterasyonla ekle
            $.each(data, function(index, news) {
                const delay = 0.1 + (index % 4) * 0.2;  // Animasyon gecikmesi
                const imageUrl = `Pictures/${news.id}.jpg`; // Haber resmi URL'si (news.id'ye göre)

                const newsRowHtml = `
                    <tr class="wow fadeInUp" data-wow-delay="${delay}s">
                        <th scope="row">${index + 1}</th>
                        <td>
                            <img src="${imageUrl}" alt="${news.titleNews}" class="img-fluid" style="max-width: 100px;">
                        </td>
                        <td>${news.titleNews}</td>
                        <td>${news.content}</td>
                        <td><a class="btn btn-sm btn-primary mx-1" href="#" onclick="openNewsSave('${news.id}')"><i class="fas fa-pencil-alt"></i> Düzenle</a></td>
                        <td><a class="btn btn-sm btn-danger mx-1" href="#" onclick="newsDelete('${news.id}')"><i class="fas fa-trash"></i> Sil</a></td>
                    </tr>
                `;
                newsTableBody.append(newsRowHtml);
            });
        },
        error: function(xhr, status, error) {
            console.error('Haberleri alırken hata oluştu:', error);
        }
    });
}

function openNewsSave(id) {
    // Modal elementlerini seç
    var modal = document.getElementById("myModal");
    var span = document.getElementsByClassName("close")[0];

    modal.style.display = "block";
    document.body.classList.add("modal-open");

    $("#newsId").val(id);
    $("#image").val(null);
    $("#titleNews").val("");
    $("#newsDate").val("");
    $("#content").val("");

    if (id!="-1"){
        getNewsById(id);
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

function getNewsById(id) {

    $.ajax({
        url: '/api/news/getNewsById', // URL'nin doğru formatı
        type: 'GET',
        dataType: 'json', // Doğru veri tipi "json" olmalı
        data: { id: id }, // Parametre olarak query string içinde ID göndermek
        success: function(news) {
            var date = new Date(news.newsDate);
            var formattedDate = date.toISOString().slice(0, 16);
            // Gelen veriyle formu dolduruyoruz
            $("#titleNews").val(news.titleNews);
            $("#newsDate").val(formattedDate);
            $("#content").val(news.content);
        },
        error: function(xhr, status, error) {
            console.error('Haber getirilirken hata oluştu:', error);
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

function newsPut() {
    var id = $("#newsId").val();
    var titleNews = $("#titleNews").val();
    var newsDate = $("#newsDate").val();
    var content = $("#content").val();
    var image = $("#image")[0].files[0]; // Dosya seçimini al

    var news = {
        id: id,
        titleNews: titleNews,
        content: content,
        newsDate: newsDate
    };

    var formData = new FormData();
    formData.append('news', new Blob([JSON.stringify(news)], { type: 'application/json' }));
    formData.append('image', image);

    $.ajax({
        url: '/api/news/createNews',
        type: 'POST',
        data: formData,
        processData: false, // FormData kullanırken false olmalı
        contentType: false, // FormData kullanırken false olmalı
        success: function(response) {
            alert(response.trim());
            fetchAdminNews();
        },error: function(error) {
            console.log(error.responseText);
            alert("KAYIT başarısız: " + error.responseText);
        }
    });
}

function newsDelete(id) {
    $.ajax({
        url: '/api/news/deleteNews',
        type: 'DELETE',
        data: { id: id },
        success: function(response) {
            alert(response.trim());
            fetchAdminNews();
        }
    });
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