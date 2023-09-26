import { Route, Routes } from "react-router-dom";
import "./App.css";
import { useEffect, useState } from "react";
import Lobby from "./Pages/Lobby";
import Room from "./Pages/Room";

function App() {
  // const [msg, setMsg] = useState("");
  // useEffect(() => {
  //   fetch("/test")
  //     .then((res) => res.json())
  //     .then((res) => setMsg(res));
  // }, []);

  return (
    <div className="App">
      심플채팅방
      <hr />
      <Routes>
        <Route path="/" element={<Lobby />}></Route>
        <Route path="/room/:roomId" element={<Room />}></Route>
      </Routes>
    </div>
  );
}

export default App;
