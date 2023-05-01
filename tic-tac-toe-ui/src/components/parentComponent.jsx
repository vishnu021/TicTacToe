import React, { useState} from 'react';
import WelcomePageForm from "./welcomePageForm";
import websocket from '../services/webSocketService'
import {ToastContainer} from "react-toastify";
import GameBoard from "./GameBoard";

function ParentComponent(props) {
    const [userName, setUserName] = useState(null);
    const [stompClient, setStompClient] = useState(null);
    const [gameStarted, setGameStarted] = useState(null);
    const [gameDetails, setGameDetails] = useState(null);


    const handleGameStart = (gameDetails) => {
        console.log("Got response", gameDetails);
        setGameDetails(gameDetails);
        setGameStarted(true);
    }

    const connectToWebSocket = async (userName) => {
        const client = await websocket.initialise();
        websocket.register(client, userName);
        websocket.subscribe(client, handleGameStart);
        setStompClient(client);
    };

    if(gameDetails) {
        return (
            <GameBoard
                stompClient={stompClient}
                gameDetails={gameDetails}
            />
        );
    }

    if(userName) {
        return (
            <div className="d-flex align-items-center">
                <div className="container">
                    <h1>Waiting Pool ...</h1>
                </div>
            </div>
        );
    }


    return (
        <React.Fragment>
            <ToastContainer />
            <div className="d-flex align-items-center">
                <div className="container">
                    <WelcomePageForm setUserName={setUserName} initiateWebSocket={connectToWebSocket}  />
                </div>
            </div>
        </React.Fragment>
    );
}

export default ParentComponent;