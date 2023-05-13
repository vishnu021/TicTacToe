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
        const {board} = gameDetails;
        navigate(`/play/${board.roomId}`, {state: gameDetails});
    }

    useEffect(() => {
            if(!location || !location.state) {
                const timeoutId = setTimeout(() => {
                    navigate('/');
                }, 500);
                return;
            }

            if(location.state && location.state.userName) {
                const { userName } = location.state;
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