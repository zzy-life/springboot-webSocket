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
            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);

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
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
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
