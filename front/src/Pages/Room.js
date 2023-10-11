import React, { useEffect, useRef, useState } from "react";
import "./Room.css";
import {json, useLocation, useParams} from "react-router-dom";
import * as StompJS from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
const Room = () => {
  const { roomId } = useParams();
  const location = useLocation();
  const sendGetData = location.state;
  const client = useRef();
  const userNameRef = useRef();
  const msgRef = useRef();
  const [msg,setMsg] = useState([]);
  // const chatsRef
  useEffect(() => {
    connect();
  }, []);



  const connect = () => {
    let sock = new SockJS("http://localhost:8080/ws");

    client.current = StompJS.Stomp.over(sock);
    let ws = client.current;
    ws.debug = function(str) {
      console.log(str);
    };
    ws.connect({},(e) => {
      console.log("WebSocket connected: ", e); // 연결 로그
      ws.subscribe("/sub/" + roomId, data => {
        console.log("Received message: ", data);
        console.log(JSON.parse(data.body));
        AddChat(data);
      });

    }, (error) => {
      console.error("WebSocket connection error: ", error); // 에러 로그
    });
  };


  const AddChat = (data) => {
    const messageData = JSON.parse(data.body);
    setMsg((msg) => [
      ...msg,
      messageData,
    ]);

    fetch("http://localhost:8080/api/lv1/chat/message", {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        "Authorization": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsIm5pY2tuYW1lIjoia2RrIiwiZXhwIjoxNjk2OTE1MDk0LCJpZHgiOjEzfQ.IHepwxU5J-Pro7r_0KpHiKX0DvUulIR7nROg0yDNFf4nPZozLd3hm1t1C1nkT3IKOBoiWM-mGoetBRYZdT4D4Q"
      },
      body: JSON.stringify({
        sender: sendGetData.sender
      })
    })

  };

  const publish = (sender, receiver, message) => {
    client.current.send("/pub/message", {}, JSON.stringify({
      type : "TALK",
      sender,
      receiver,
      message: message.current.value,
    }));
  };

  return (
      <div>
        <h1>{roomId ? roomId : null}</h1>
        <div id="chats">{
          msg.map((item, i) => {
            return (
                <div key={i}>
                  <b>{item.sender} : </b>{item.message}
                </div>
            );
          })
        }</div>
        <div id="toolbox">
          <input placeholder="보낼메세지" ref={msgRef}></input>
          <button onClick={(e) => {
            publish(sendGetData.sender, sendGetData.receiver, msgRef); // 이름 변경
          }}>전송</button>
        </div>
      </div>
  );
};

export default Room;
