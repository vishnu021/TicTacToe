import React, {useContext, useEffect, useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import websocket from "../services/webSocketService";
import {StompContext} from "./parent";

function WaitingPool(props) {
    const location = useLocation();
    const navigate = useNavigate();
    const { stompClient, setStompClient } = useContext(StompContext);
    const [gameDetails, setGameDetails] = useState(null);

    const handleGameStart = (gameDetails) => {
        setGameDetails(gameDetails);
        console.log("setting game details game room id : ", gameDetails.board.roomId)
        const {board} = gameDetails;
        navigate(`/play/${board.roomId}`, {state: gameDetails});
    }

    useEffect(() => {
            if(!location || !location.state) {
                console.log("Redirect to welcome page as location is", JSON.stringify(location));
                const timeoutId = setTimeout(() => {
                    navigate('/');
                }, 2000);
                return;
            }

            if(location.state && location.state.userName) {

                const { userName } = location.state;
                console.log("entered waiting pool with props : ", userName);
                console.log("entered waiting pool with stompClient : ", stompClient);
                websocket.subscribe(stompClient, handleGameStart);
            }
        }
    );

    return (
            <div className="d-flex align-items-center">
                <div className="container">
                    <h1>Waiting Pool ...</h1>
                </div>
            </div>
    );
}

export default WaitingPool;