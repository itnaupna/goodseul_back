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
            const roomId = name > name2 ? `${name2}to${name}` : `${name}to${name2}`;

            // Check if the room exists
            fetch(`/api/lv1/chat/?person1=${name}&person2=${name2}`, {
                method: 'GET',
            })
                .then(res => res.json())
                .then(res => {
                    if(res) {
                        // If room exists, set the room ID
                        setLst(roomId);
                        alert('이미 생성되어있는 방 있음!');
                    } else {
                        // If room does not exist, create a new room
                        fetch("/api/lv1/chat", {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json'
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
                })
                .catch(e => {
                    console.log(e);
                    alert('방 체크 실패');
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
