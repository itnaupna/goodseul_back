import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

const Lobby = () => {
  const [lst, setLst] = useState("");
  const [name, setName] = useState("");
  const [name2,setName2] = useState("");

    const RoomCreate = (e) => {

        if (name.length === 0) {
            alert('이름1 을 입력해주세요!');
            return;
        } else if(name2.length === 0) {
            alert('이름2 를 입력해주세요!');
            return;
        } else {
            // If room does not exist, create a new room
            fetch("http://localhost:8080/api/lv1/chat/room", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    "Authorization": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBY2Nlc3NUb2tlbiIsIm5pY2tuYW1lIjoia2RrIiwiZXhwIjoxNjk2OTE1MDk0LCJpZHgiOjEzfQ.IHepwxU5J-Pro7r_0KpHiKX0DvUulIR7nROg0yDNFf4nPZozLd3hm1t1C1nkT3IKOBoiWM-mGoetBRYZdT4D4Q"
                },
                body: JSON.stringify({
                    person1: name,
                    person2: name2
                })
            })
                .then(res => res.text())
                .then(res => {
                    setLst(res);
                    alert('생성성공');
                })
                .catch(e => {
                    console.log(e);
                    alert('생성실패');
                });
        }
    }

    return (
    <div>
        <input placeholder="가입 번호를 입력하세요" onChange={(e) => setName(e.target.value)} />
        <input placeholder="대화하려는 상대의 가입 번호를 입력하세요" onChange={(e) => setName2(e.target.value)} />
      <button onClick={RoomCreate}>방만들기</button>
      <ul>
        {
          <Link to={"/room/"+lst} state={{ sender : name , receiver : name2}}> {`${lst} 대화방`} </Link>
        }
      </ul>
    </div>
  );
};

export default Lobby;
