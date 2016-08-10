window.addEventListener("DOMContentLoaded", function (event) {
    var postsElement = document.getElementById("posts");
    var timelineElement = document.getElementById("timeline");

    var followButton = document.getElementById("follow_button");
    if (followButton) {
        followButton.onclick = function () {
            function switchFollowButton() {
                sessionUser.canFollow = !(sessionUser.canFollow);
                followButton.className = "btn ";
                if (sessionUser.canFollow) {
                    followButton.className += "btn-default";
                    followButton.innerText =  localeStrings.followButtonText + " @" + pageUser.username
                } else {
                    followButton.className += "btn-primary";
                    followButton.innerText = localeStrings.unfollowButtonText + " @" + pageUser.username
                }
            }

            if (sessionUser.userId == 0) {
                console.log("No session is attached; should redirect to login page!");
                return;
            }

            if (sessionUser.canFollow) {
                $.ajax({
                    url: '/rest/secure/followers/subscribe',
                    headers: {'Content-type': 'application/json'},
                    method: 'post',
                    data: JSON.stringify({userId: pageUser.userId})
                }).done(switchFollowButton);
            } else {
                $.ajax({
                    url: '/rest/secure/followers/unsubscribe',
                    headers: {'Content-type': 'application/json'},
                    method: 'delete',
                    data: JSON.stringify({userId: pageUser.userId})
                }).done(switchFollowButton);
            }
        }
    }

    function addPostsToElement(postsToAdd, element) {
        function createDeleteButton(post, postElementToDelete) {
            var deleteButton = document.createElement("button");
            deleteButton.className = "close";
            deleteButton.type = "button";

            var timesSpan = document.createElement("span");
            timesSpan.innerHTML = '&times;';

            deleteButton.appendChild(timesSpan);

            deleteButton.addEventListener('click', function (event) {

                $.ajax({
                    url: "/rest/secure/posts/removepost",
                    method: 'delete',
                    headers: {'Content-type': 'application/json'},
                    data: JSON.stringify({postId: post.id})
                }).done(function() {
                    postElementToDelete.parentNode.removeChild(postElementToDelete);
                })
            });

            return deleteButton;
        }

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
                        url: '/rest/secure/likes/addlike',
                        headers: {'Content-type': 'application/json'},
                        method: 'post',
                        data: JSON.stringify({postId: post.id})
                    }).done(updateButton);
                } else {
                    $.ajax({
                        url: '/rest/secure/likes/removelike',
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

            var userInfoDiv = document.createElement("div");
            userInfoDiv.className = "col-xs-8";

            var usernameHref = document.createElement("a");
            usernameHref.href = "/user/" + post.authorUsername;
            usernameHref.innerHTML = '<strong>@' + post.authorUsername + '</strong>';

            userInfoDiv.appendChild(usernameHref);
            userInfoDiv.innerHTML+=' '+ postDate;
            userInfoDiv.innerHTML += '<p>' + post.text + '</p>';

            postElement.innerHTML += '<div class="col-xs-2"><img src="/images/avatar.png" class="user-post-avatar"></div>';
            postElement.appendChild(userInfoDiv);

            if (sessionUser.userId == post.authorId) {
                var deleteBtnDiv = document.createElement('div');
                deleteBtnDiv.className = "col-xs-2";
                var deleteButton = createDeleteButton(post, postElement);

                deleteBtnDiv.appendChild(deleteButton);
                postElement.appendChild(deleteBtnDiv);
            }

            var likeBtnDiv = document.createElement('div');
            likeBtnDiv.className = "col-xs-12";

            var likeButton = createLikeButton(post);

            likeBtnDiv.appendChild(likeButton);
            postElement.appendChild(likeBtnDiv);

            element.appendChild(postElement);
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

    function isNoMorePosts(messages) {
        return (messages.length == 0) || (lastElement(messages).id == 1);
    }

    function loadTimeline(offsetId, limit) {

        offsetId = offsetId || -1;
        limit = limit || 5;

        $.ajax({
            url: "/rest/secure/timeline/gettimeline",
            method: "GET",
            data: {
                offsetId: offsetId,
                limit: limit
            }
        }).done(function (receivedPosts) {
            console.log(receivedPosts);

            addPostsToElement(receivedPosts, timelineElement);

            if (isNoMorePosts(receivedPosts)) {
                window.onscroll = null;
            } else {
                window.onscroll = function (ev) {
                    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
                        loadTimeline(lastElement(receivedPosts).id - 1, 5)
                    }
                };
            }
        });
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

            addPostsToElement(receivedPosts, postsElement);

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

    if (postsElement) {
        loadPosts(pageUser, -1, 5);
    }

    if (timelineElement) {
        loadTimeline(-1, 5)
    }
});