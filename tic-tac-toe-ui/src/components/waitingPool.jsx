import React, {useContext, useEffect, useCallback} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import websocket from "../services/webSocketService";
import {StompContext} from "./parent";

function WaitingPool() {
    const location = useLocation();
    const navigate = useNavigate();
    const { stompClient } = useContext(StompContext);

    const handleGameStart = useCallback((details) => {
        const {board} = details;
        navigate(`/play/${board.roomId}`, {state: details});
    }, [navigate]);

    useEffect(() => {
            if(!location || !location.state) {
                setTimeout(() => {
                    navigate('/');
                }, 500);
                return;
            }

            if(location.state && location.state.userName) {
                websocket.subscribe(stompClient, handleGameStart);
            }
        }, [location, navigate, stompClient, handleGameStart]
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