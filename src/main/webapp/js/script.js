window.addEventListener("DOMContentLoaded" , function (event) {
    var pageUser = {
        userId: document.getElementById("pageUser-Id").textContent,
        username: document.getElementById("pageUser-username").textContent
    };
    var posts = document.getElementById("posts");

    function epochToDate(seconds) {
        var date = new Date(0);
        date.setUTCSeconds(seconds);

        return date;
    }

    $.ajax({
        url:"/rest/getposts",
        method: "GET",
        data : {
            userId: pageUser.userId,
            offset: 0,
            limit: 20
        }
    }).done(function (messages) {
        console.log(messages);

        for (var i = 0; i<messages.length; i++) {
            var post = messages[i];
            var postHtml = "";
            var postDate = epochToDate(post.creationTime.epochSecond);

            postHtml += '<div class="well wall-post row">';
            postHtml += '<div class="col-xs-2"><img src="/images/avatar.png" class="user-post-avatar"></div>';
            postHtml += '<div class="col-xs-10">';
            postHtml += '<strong>'+pageUser.username+'</strong> '+ postDate;
            postHtml += '<p>'+post.text+'</p>';
            postHtml += '</div>';
            postHtml += '</div>';

            posts.innerHTML += postHtml;
        }
    });
});