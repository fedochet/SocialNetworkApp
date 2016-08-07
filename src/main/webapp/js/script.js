window.addEventListener("DOMContentLoaded", function (event) {
    var postsElement = document.getElementById("posts");

    var pageUser = {
        userId: document.getElementById("pageUser-Id").textContent,
        username: document.getElementById("pageUser-username").textContent
    };

    function addPostsToTimeline(postsToAdd) {
        for (var i = 0; i < postsToAdd.length; i++) {
            var post = postsToAdd[i];
            var postHtml = "";

            var postDate = epochToDate(post.creationTime.epochSecond);
            postHtml += '<div class="well wall-post row">';
            postHtml += '<div class="col-xs-2"><img src="/images/avatar.png" class="user-post-avatar"></div>';
            postHtml += '<div class="col-xs-10">';
            postHtml += '<strong>@' + post.authorUsername + '</strong> ' + postDate;
            postHtml += '<p>' + post.text + '</p>';
            postHtml += '</div>';
            postHtml += '</div>';

            postsElement.innerHTML += postHtml;
        }
    }

    function epochToDate(seconds) {
        var date = new Date(0);

        date.setUTCSeconds(seconds);
        return date;
    }

    function lastElement(array) {
        return array[array.length - 1];
    }

    function loadPosts(user, offsetId, limit) {

        offsetId = offsetId || -1;
        limit = limit || 5;

        $.ajax({
            url: "/rest/getposts",
            method: "GET",
            data: {
                authorId: user.userId,
                offsetId: offsetId,
                limit: limit
            }
        }).done(function (receivedPosts) {
            console.log(receivedPosts);

            addPostsToTimeline(receivedPosts);

            function isNoMorePosts(messages) {
                var lastRequestHadZeroPosts = messages.length == 0;
                var isLastPostWereLastPostInDB = lastElement(messages).id == 1;

                return lastRequestHadZeroPosts || isLastPostWereLastPostInDB;
            }

            if (isNoMorePosts(receivedPosts)) {
                window.onscroll = null;
            } else {
                window.onscroll = function (ev) {
                    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
                        loadPosts(user, lastElement(receivedPosts).id - 1, 5)
                    }
                };
            }
        });
    }

    loadPosts(pageUser, -1, 5);
});