'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var roomstompClient = null;
var username = null;
var room = {
    roomID: null,
    roomUsers: {

    }
};
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if (username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        if (stompClient == null) {

            /* 少数服务器使用SockJS部署到服务器时，需要走nginx代理才可连接，否则无法连接
            或者可以使用WebSocket
            #websocket配置
            # location /{
            #     proxy_set_header Host $http_host;
            #     proxy_set_header X-Real-IP $remote_addr;
            #     proxy_set_header REMOTE-HOST $remote_addr;
            #     proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            #     proxy_pass http://localhost:8080;
            # }
            #   location /ws{
            #     proxy_http_version 1.1;
            #     proxy_set_header Upgrade $http_upgrade;
            #     proxy_set_header Connection "Upgrade";
            #     proxy_set_header Host $host;
            #             proxy_pass http://localhost:8080/ws;
            # }
            */

             var socket = new SockJS('/ws');
           // var host = location.host;
          //  var socket = new WebSocket('ws://' + host + '/ws');
            stompClient = Stomp.over(socket);
            stompClient.heartbeat.outgoing = 20000; // client will send heartbeats every 20000ms
            stompClient.heartbeat.incoming = 0;     // client does not want to receive heartbeats
            stompClient.connect({}, onConnected, onError);
        }

    }
    event.preventDefault();
}


function onConnected() {
    // 监听全体广播
    stompClient.subscribe('/topic/public', onMessageReceived);

    //监听自己的消息广播
    stompClient.subscribe('/user/' + username + '/room', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({ sender: username, type: 'JOIN' })
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = '无法连接到 WebSocket 服务器。 请刷新此页面重试!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT',
            roomId: room.roomID
        };

        stompClient.send("/app/chat.user", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    //如果是错误消息并且房间号为空，则是用户输入了重复的用户名
    if (message.type === 'ERROR' && room.roomID == null) {
        alert('账号重复,请重新输入账号');
        chatPage.classList.add('hidden');
        usernamePage.classList.remove('hidden');
        stompClient.disconnect()
        stompClient = null
    }
    //错误消息另外处理，不进行聊天处理
    if (message.type === 'ERROR') {
        return
    }
    var messageElement = document.createElement('li');
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        var roomContent = JSON.parse(message.content);
        message.content = message.sender + ' 加入房间！当前人数：' + roomContent.length;

        if (room.roomID == null) {
            room.roomID = message.roomId

            //监听房间 1是房间名
            roomstompClient = stompClient.subscribe('/user/' + room.roomID + '/message', onMessageReceived);
        }

    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' 退出房间!';
    }
    else {
        if (message.sender == username) {
            messageElement.classList.add('chat-message-right');
        } else {
            messageElement.classList.add('chat-message');
        }

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
