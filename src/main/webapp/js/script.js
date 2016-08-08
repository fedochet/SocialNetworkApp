window.addEventListener("DOMContentLoaded", function (event) {
    var postsElement = document.getElementById("posts");

    var pageUser = {
        userId: document.getElementById("pageUser-Id").textContent,
        username: document.getElementById("pageUser-username").textContent
    };

    function addPostsToTimeline(postsToAdd) {
        function createLikeButton(post) {
            var likeButton = document.createElement("button");
            likeButton.type = 'button';
            likeButton.className = 'btn pull-right ';
            if (post.canLike) {
                likeButton.className += 'btn-default';
            } else {
                likeButton.className += 'btn-primary';
            }

            likeButton.innerHTML += 'Like <span class="glyphicon glyphicon-thumbs-up " aria-hidden="true"></span> ';
            var badge = document.createElement("span");
            badge.className = "badge";
            if (post.likes) {
                badge.innerText = post.likes;
            }
            likeButton.appendChild(badge);

            likeButton.addEventListener('click', function (event) {
                function updateButton() {
                    if (post.canLike) {
                        post.likes++;
                    } else {
                        post.likes--;
                    }

                    post.canLike = !(post.canLike);
                    likeButton.className = 'btn pull-right ';
                    if (post.canLike) {
                        likeButton.className += 'btn-default';
                    } else {
                        likeButton.className += 'btn-primary';
                    }

                    if (post.likes!=0) {
                        badge.innerText = post.likes;
                    } else {
                        badge.innerText = "";
                    }
                }

                if (post.canLike) {
                    $.ajax({
                        url: '/rest/likes/addlike',
                        headers: {'Content-type': 'application/json'},
                        method: 'post',
                        data: JSON.stringify({postId: post.id})
                    }).done(updateButton);
                } else {
                    $.ajax({
                        url: '/rest/likes/removelike',
                        headers: {'Content-type': 'application/json'},
                        method: 'delete',
                        data: JSON.stringify({postId: post.id})
                    }).done(updateButton);
                }
            });

            return likeButton;
        }

        for (var i = 0; i < postsToAdd.length; i++) {
            var post = postsToAdd[i];
            var postHtml = "";

            var postElement = document.createElement("div");
            postElement.className = "well wall-post row";

            var postDate = epochToDate(post.creationTime.epochSecond);

            postElement.innerHTML += '<div class="col-xs-2"><img src="/images/avatar.png" class="user-post-avatar"></div>';
            postElement.innerHTML += '<div class="col-xs-10">';
            postElement.innerHTML += '<strong>@' + post.authorUsername + '</strong> ' + postDate;
            postElement.innerHTML += '<p>' + post.text + '</p>';
            postElement.innerHTML += '</div>';

            var likeBtnDiv = document.createElement('div');

            var likeButton = createLikeButton(post);

            likeBtnDiv.className = "col-xs-12";

            likeBtnDiv.appendChild(likeButton);
            postElement.appendChild(likeBtnDiv);
            postsElement.appendChild(postElement);
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
            url: "/rest/posts/getposts",
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